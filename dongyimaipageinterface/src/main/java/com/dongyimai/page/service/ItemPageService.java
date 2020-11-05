package com.dongyimai.page.service;

/*商品详细页接口*/
public interface ItemPageService {
/*
* 生成商品详细页
* goodsId
* */
public  boolean genItemHtml(Long goodsId);
    public void deleteItemHtml(Long[] ids);
}
