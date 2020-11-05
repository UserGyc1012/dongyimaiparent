package com.dongyimai.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.dongyimai.mapper.TbSpecificationOptionMapper;
import com.dongyimai.pojo.TbSpecificationOption;
import com.dongyimai.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.dongyimai.mapper.TbTypeTemplateMapper;
import com.dongyimai.pojo.TbTypeTemplate;
import com.dongyimai.pojo.TbTypeTemplateExample;
import com.dongyimai.pojo.TbTypeTemplateExample.Criteria;
import com.dongyimai.sellergoods.service.TypeTemplateService;

import com.dongyimai.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 3000)
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);		
		saveRedis();//存入数据到缓存
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
	return typeTemplateMapper.selectOptionList();
	}

    @Override
    public List<Map> findSpecList(Long id) {
		//根据模板的id获取对应模板的对象
		TbTypeTemplate tbTypeTemplate=typeTemplateMapper.selectByPrimaryKey(id);;
		//从模板对象中获取规格属性；
		List<Map> list=JSON.parseArray(tbTypeTemplate.getSpecIds(),Map.class);
		//遍历规格集合
		if (list!=null){
			for (Map map:list){
				Long specid= new Long((Integer)map.get("id"));//因为get获取的数据是1object类型
				//根据规格id获取规格选项
				TbSpecificationOptionExample example=new TbSpecificationOptionExample();
				TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
				criteria.andSpecIdEqualTo(specid);
				List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
				map.put("options",specificationOptionList);
				//此时map集合中的数据就是{"id":27,"text":"网络","options":[specificationOptionList]}
			}
		}
        return list;
    }

    /*将数据存入redis缓存中*/
	private  void saveRedis(){
		//获取模板数据
		List<TbTypeTemplate> tbTypeTemplateList=findAll();
		//循环模板
		for (TbTypeTemplate tbTypeTemplate : tbTypeTemplateList) {
			//存储品牌列表
			List<Map> brandList=JSON.parseArray(tbTypeTemplate.getBrandIds(),Map.class);
			//存入缓存
			redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);
		//存储规格列表
			List<Map> specList=findSpecList(tbTypeTemplate.getId());//根据模板id查询规格列表，包括规格选项
			redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
		}
	}

}
