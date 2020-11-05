package com.dongyimai.sellergoods.service;

import com.dongyimai.entity.PageResult;
import com.dongyimai.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    //查询所有品牌
    public List<TbBrand> findAll();
     //分页
    public PageResult findPage(int pageNum, int pageSize);
    public void save(TbBrand tbBrand);

   public TbBrand findOne(Long id);

   public void update(TbBrand tbBrand);

   public void dele(Long[] ids);
    public PageResult search(int page, int rows, TbBrand tbBrand);
    /*平拍下来框*/
    List<Map> selectOptionList();
}
