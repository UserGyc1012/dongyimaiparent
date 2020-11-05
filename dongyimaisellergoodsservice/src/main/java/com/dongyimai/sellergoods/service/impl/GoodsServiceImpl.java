package com.dongyimai.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.dongyimai.group.Goods;
import com.dongyimai.mapper.*;
import com.dongyimai.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.dongyimai.pojo.TbGoodsExample.Criteria;
import com.dongyimai.sellergoods.service.GoodsService;

import com.dongyimai.entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 30000)
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

/*	*//**
	 * 按分页查询
	 *//*
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}*/

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setIsDelete("0");
		goods.getGoods().setAuditStatus("0");//是设置未审核状态
		goodsMapper.insert(goods.getGoods());//像Goods表插入数据
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置goodsDesc表中的goodsId；
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
		saveItemList(goods);//插入商品sku列表数据
		/*if ("1".equals(goods.getGoods().getIsEnableSpec())) {

			for (TbItem item : goods.getItemList()) {
//标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods, item);
				itemMapper.insert(item);
			}
		} else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品SPU+规格描述串作为SKU名称
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValus(goods, item);
			itemMapper.insert(item);
		}*/
	}
	private void setItemValus(Goods goods,TbItem item) {
		item.setGoodsId(goods.getGoods().getId());//商品SPU编号
		   item.setSellerId(goods.getGoods().getSellerId());//商家编号
		   item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		   item.setCreateTime(new Date());//创建日期
		   item.setUpdateTime(new Date());//修改日期
		   //品牌名称
		   TbBrand brand=brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		   item.setBrand(brand.getName());
		   //分类名称
		   TbItemCat itemCat=itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
            item.setCategory(itemCat.getName());
		   //商家名称
		   TbSeller seller=sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		   item.setSeller(seller.getNickName());
		   //图片地址（取SPU的第一个图片即可）
		   List<Map> imageList=JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);//后面加Map。class采用反射，不用强转数据类型，大大提高数据安全性，降低数据风险
		   if (imageList.size()>0){
		   	item.setImage((String) imageList.get(0).get("url"));
		   }
	}
/*插入sku列表数据*/
	private  void saveItemList(Goods goods){
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {

			for (TbItem item : goods.getItemList()) {
//标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods, item);
				itemMapper.insert(item);
			}
		} else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品SPU+规格描述串作为SKU名称
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValus(goods, item);
			itemMapper.insert(item);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的商品，需要重新设置状态
		goodsMapper.updateByPrimaryKey(goods.getGoods());//保存商品表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());//保存商品扩展表
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//添加新的sku列表数据
		saveItemList(goods);//插入商品SKU列表数据
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods=new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);//通过id查询goods信息封装在goods中
		goods.setGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);//通过id查询goods内容封装在goods中
		goods.setGoodsDesc(tbGoodsDesc);
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItemList(tbItems);//通过商品id查找item表中商品sku信息，封装在goods中；
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			TbGoods goods=goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);//假删除
		}
		//修改商品的sku为禁用，因为删除后商品在item表中人存在，所以要把item表中商品的status修改为不可用状态
		List<TbItem> listItem=findItemListByGoodsIdAndStatus(ids,"1");
		for (TbItem item : listItem) {
			item.setStatus("0");
			itemMapper.updateByPrimaryKey(item);
		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
						criteria.andIsDeleteEqualTo("0");//非删除状态

		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id:ids){
			//根据商品id获取商品信息
			TbGoods goods=goodsMapper.selectByPrimaryKey(id);
			//修改商品状态
			goods.setAuditStatus(status);
			//更新商品状态到数据库追踪
			goodsMapper.updateByPrimaryKey(goods);
			//修改sku的状态
			TbItemExample example=new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> itemList = itemMapper.selectByExample(example);
			//遍历sku集合
			for(TbItem item:itemList){
				//修改状态
				item.setStatus("1");
				itemMapper.updateByPrimaryKey(item);
			}
		}
	}

	@Override
	public List<TbItem> findItemListByGoodsIdAndStatus(Long[] goodIds, String status) {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(goodIds));//因为goodIds是作为数组传进来的，所以把他再转化为list集合形式，方便存取
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example);
	}

}
