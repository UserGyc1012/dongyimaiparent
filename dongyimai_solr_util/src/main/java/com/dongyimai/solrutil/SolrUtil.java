package com.dongyimai.solrutil;

import com.alibaba.fastjson.JSON;
import com.dongyimai.mapper.TbItemMapper;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
@Autowired
private TbItemMapper itemMapper;
@Autowired
private SolrTemplate solrTemplate;

public void importItemData(){
    TbItemExample example=new TbItemExample();
    TbItemExample.Criteria criteria = example.createCriteria();
    criteria.andStatusEqualTo("1");
    List<TbItem> itemList = itemMapper.selectByExample(example);
    for (TbItem item : itemList) {
        //规格转换
        Map specMap=JSON.parseObject(item.getSpec());//将spec字段中的json字符串转换为map
       item.setSpecMap(specMap);//给带注解的字段赋值
        System.out.println(item.getId()+"----"+item.getTitle());

    }
    //同步
    solrTemplate.saveBeans(itemList);
    solrTemplate.commit();
    System.out.println("同步完成");
}

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil= (SolrUtil) context.getBean("solrUtil");
            solrUtil.importItemData();
    }
}
