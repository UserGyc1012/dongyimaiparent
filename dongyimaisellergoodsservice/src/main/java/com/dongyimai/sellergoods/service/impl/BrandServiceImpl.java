package com.dongyimai.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.entity.PageResult;
import com.dongyimai.mapper.TbBrandMapper;
import com.dongyimai.pojo.TbBrand;
import com.dongyimai.pojo.TbBrandExample;
import com.dongyimai.sellergoods.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        //pageHelper添加分页属性
        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
             //返回总页数，和当前显示页数。
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void save(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

     @Override
    public TbBrand findOne(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult search(int page, int rows, TbBrand tbBrand) {
        //1.分页属性
        PageHelper.startPage(page,rows);
        //2.按条件查询
        TbBrandExample example=new TbBrandExample();
        //Criteria为内部内，
        TbBrandExample.Criteria criteria=example.createCriteria();
        if (tbBrand!=null){
            if (tbBrand.getName()!=null && tbBrand.getName().length()>0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (tbBrand.getFirstChar()!=null && !"".equals(tbBrand.getFirstChar())){
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }
             //3查询
      Page<TbBrand> pageResult= (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        return new PageResult(pageResult.getTotal(),pageResult.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectOptionList();
    }
}
