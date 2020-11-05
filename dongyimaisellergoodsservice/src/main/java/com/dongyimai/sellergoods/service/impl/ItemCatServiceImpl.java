package com.dongyimai.sellergoods.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.dongyimai.mapper.TbItemCatMapper;
import com.dongyimai.pojo.TbItemCat;
import com.dongyimai.pojo.TbItemCatExample;
import com.dongyimai.pojo.TbItemCatExample.Criteria;
import com.dongyimai.sellergoods.service.ItemCatService;

import com.dongyimai.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 商品类目服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 3000)
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
						if (itemCat.getParentId()!=null){
							criteria.andParentIdEqualTo(itemCat.getParentId());
						}
		}

		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);
			//每次执行查询的时候，一行读取缓存进行存储，（因为每次执行增删改查都要执行此方法）
			//当数据量较大时效率低下，所以要进行代码优化
			List<TbItemCat> itemCatList=findAll();
			Map map=new HashMap();
			for (TbItemCat itemCat1 : itemCatList) {
				map.put(itemCat1.getName(),itemCat1.getTypeId());
			}
			redisTemplate.boundHashOps("itemCat").putAll(map);
			System.out.println("更新缓存商品分类表");
		return new PageResult(page.getTotal(), page.getResult());
	}
//根据上级id查找当前表中数据
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
	return	itemCatMapper.selectByExample(example);

	}

}
