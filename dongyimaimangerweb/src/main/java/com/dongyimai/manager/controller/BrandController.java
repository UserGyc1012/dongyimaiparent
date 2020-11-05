package com.dongyimai.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dongyimai.entity.PageResult;
import com.dongyimai.entity.Result;
import com.dongyimai.pojo.TbBrand;
import com.dongyimai.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
   private BrandService brandService;
 @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
     return brandService.findAll();
 }
 @RequestMapping("/findPage")
 public PageResult findPage(int page, int rows){

     //1.首页
     if (page==0){
         rows=1;
     }
    return brandService.findPage(page,rows);
 }
 //新增
    @RequestMapping("/save")
    public Result save(@RequestBody TbBrand tbBrand){
        try {
            brandService.save(tbBrand);
            return new Result(true,"新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新增失败");
        }

    }
    @RequestMapping("/findOne")
    public  TbBrand findOne(Long id){
       return brandService.findOne(id);
    }
    @RequestMapping("update")
    public  Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改成功");
        }
    }
    @RequestMapping("/dele")
    public  Result dele(Long[] ids){
        System.out.println("ids:" + ids);
        try {
            brandService.dele(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/search")
    public  PageResult search(@RequestBody TbBrand tbBrand,int page,int rows){
    return brandService.search(page,rows,tbBrand);
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}
