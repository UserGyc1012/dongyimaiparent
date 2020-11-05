package com.dongyimai.cart.service;

import com.dongyimai.entity.Cart;

import java.util.List;

/**
 * @ClassName CartService
 * @Deacription TODO
 * @Author 葛言超
 * @Date 2020/10/27 21:36
 * version:1.0 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface CartService {

    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    //合并购物车
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
