package com.dongyimai.shop.service;

import com.dongyimai.pojo.TbSeller;
import com.dongyimai.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

//认证类
public class UserDetailsServiceImpl implements UserDetailsService {
    private SellerService sellerService;
    public void setSellerService(SellerService sellerService){
        this.sellerService=sellerService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("经过了UserDetailsServiceImpl");
        //构建角色列表
        List<GrantedAuthority> grantedAuths=new ArrayList<GrantedAuthority>();
grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
//得到商家对象
        TbSeller seller=sellerService.findOne(username);
        if (seller!=null&seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),grantedAuths);//返回结果声明只有密码是123456的用户才能登路
        }else{
            return null;
        }
    }
}
