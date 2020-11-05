package com.dongyimai.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(timeout = 30000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //搜索关键字空格处理
        String keywords= (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        //定义返回值对象
        Map<String,Object> map=new HashMap<String,Object>();
//创建solr的查询对象
        /*Query query=new SimpleQuery("*:*");
        //拼接查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
       //item_keywords是schema.xml中配置的参数，。is就是item_keywords等于searchMap中的keywords
        //把搜索框中的字段keywords（例如“华为”）给Criteria中item_keywords这个字段。这样就可以将solr中的数据查出来了。
        //想查询对象中添加查询条件
        query.addCriteria(criteria);
        //执行查询
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        //将结果封装到map容器中
        map.put("rows",page.getContent());//getContent查询到的所有数据*/
        //查询列表
        //1.按关键字查询高亮显示
        map.putAll(searchList(searchMap));
        //。2.根据关键字查询商品分类
        List categoryList=searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //查询品牌和规格
        //3、根据商品类目查询对应的品牌、规格
        //读取分类名称
        String categoryName=(String) searchMap.get("category");
       if(!"".equals(categoryName)){
           map.putAll(searchBrandAndSpecList(categoryName));
        }else{
            if(categoryList.size() > 0){
                map.putAll(searchBrandAndSpecList(categoryList.get(0)+""));
            }
       }

        return map;
    }

    @Override
    public void importList(List<TbItem> itemList) {
        for (TbItem tbItem : itemList) {
            Map specMap=JSON.parseObject(tbItem.getSpec());
            tbItem.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品的id"+goodsIdList);
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();//仅删除被管理员删除在solr中的商品
    }

    //根据关键字查询，对查询的结果进行高亮
    private Map searchList(Map searchMap){
        Map map=new HashMap();

        //1、创建一个支持高亮查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2、设定需要高亮处理字段
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        //3、设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //4、设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //5、关联高亮选项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);

        //6、设定查询条件 根据关键字查询
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //关联查询条件到查询器对象
        query.addCriteria(criteria);
        //二、过滤分类
        if(!"".equals(searchMap.get("category"))){
            //创建过滤条件
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            //装载过滤条件
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            //将过滤条件加入在查询条件中
            query.addFilterQuery(filterQuery);
        }

        //三、过滤品牌
        if(!"".equals(searchMap.get("brand"))){
            //创建过滤条件
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            //装载过滤条件
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            //将过滤条件加入在查询条件中
            query.addFilterQuery(filterQuery);
        }
        //四、过滤规格
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map)searchMap.get("spec");
            for(Map.Entry<String,String> entry : specMap.entrySet()){
                //item_spec_网络 移动3G
                Criteria criteria1 = new Criteria("item_spec_"+entry.getKey()).is(entry.getValue());
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //过滤价格筛选
        if (!"".equals(searchMap.get("price"))){
            String[] price =((String)searchMap.get("price")).split("-");
            if (!price[0].equals("0")){//如果价格区间起点不等于0
                //创建过滤条件
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
              //装载过滤条件
               FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
               //将过滤条件封装到查询中
                query.addFilterQuery(filterQuery);
            }
        //如果区间终点不等于*
            if (!price[1].equals("*")){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //6分页
        Integer pageNo= Integer.parseInt(searchMap.get("pageNo")+"");
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize= (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            pageSize=20;
        }
        //封装起始页
        query.setOffset((pageNo-1)*pageSize);//第几页从第几条开始查询
        query.setRows(pageSize);

        //排序
        String sortValue= (String) searchMap.get("sort");//sort是前台传过来的条件，表示升序还是降序
String sortField= (String) searchMap.get("sortField");//sortField表示根据什么排序
        if (sortValue!=null&&!sortValue.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }
        //7、发出带高亮数据查询请求
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //8、获取高亮集合入口
        List<HighlightEntry<TbItem>> highlightEntryList = page.getHighlighted();
        //9、遍历高亮集合
        for(HighlightEntry<TbItem> highlightEntry:highlightEntryList){
            //获取基本数据对象
            TbItem tbItem = highlightEntry.getEntity();
            if(highlightEntry.getHighlights().size()>0&&highlightEntry.getHighlights().get(0).getSnipplets().size()>0) {
                List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
                //高亮结果集合
                List<String> snipplets = highlightList.get(0).getSnipplets();
                //获取第一个高亮字段对应的高亮结果，设置到商品标题
                tbItem.setTitle(snipplets.get(0));
            }

        }
        map.put("totalPages",page.getTotalPages());//总页数封装到map中
        map.put("total",page.getTotalElements());//总条数封装到map中
        //把带高亮数据集合存放map
        map.put("rows",page.getContent());
        return map;
    }
    /*查询分类列表*/
    private List searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        Query query = new SimpleQuery();

        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //跟剧列得到分组结果集
        GroupResult<TbItem> groupResult=page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;

    }
    /*查按品牌和规格列表
    * category分类名称
    * */
    private  Map searchBrandAndSpecList(String category){

        Map map=new HashMap();
        Long typeId= (Long) redisTemplate.boundHashOps("itemCat").get(category);

        //根据模板id查询品牌列表
        if (typeId!=null){
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);

       map.put("brandList",brandList);//返回值添加到品牌列表map中
            //根据模板id查询规格列表
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);//返回值添加到规格列表的map中
        }
        return map;
    }

}
