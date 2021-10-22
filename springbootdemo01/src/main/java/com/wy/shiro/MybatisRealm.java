package com.wy.shiro;

import com.wy.bean.Admin;
import com.wy.bean.AdminExample;
import com.wy.service.AdminService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 自定义的realm 和 mybatis 数据库 结合的 realm
 *
 * realm 中，包含 认证（登录） 和 授权 2个部分
 *
 * AuthorizingRealm  为什么要继承认证？？？有登录不一定授权，授权了一定登录
 */

public class MybatisRealm extends AuthorizingRealm {
    @Autowired
    private AdminService adminService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //写授权！！！  查询3表 可得 到  角色 和 权限。。。

        // 第一个：  拿到  account
        String principal = (String) principalCollection.getPrimaryPrincipal();
        //第二个：使用五表联查
        Map map = new HashMap<>();
        map.put("adminAccount",principal);
        List<Map> maps = adminService.selectMore(map);
        // maps 包含了角色名称和权限名称。  逐个用set集合
        Set<String> roleNames = new HashSet<>();
        //追加内容
        List perms = new ArrayList();
        for (Map map1 : maps) {
            String roleName = (String) map1.get("roleName");//角色名称
            String qxPerms = (String) map1.get("qxName");//权限名称
            // 循环遍历到  roleNames 集合中
            roleNames.add(roleName);
            perms.add(qxPerms);
        }
        // 把角色和权限给与 登录的账户   SimpleAuthorization授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roleNames);
        info.addStringPermissions(perms);
        return info;  // 触发 授权：  1. 界面UI触发，适用于单体项目    2.java方法注解触发，适用于 前后端分离， 3. 不常用的 ，自己硬编码触发
                      // 界面触发要用到 aop 和 jar包支持。
    }

    //认证（登录）
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //AuthenticationToken 这个参数是什么？？？  其实 就是 UsernamePasswordToken（“账号","密码"）
        String account = (String) authenticationToken.getPrincipal(); //拿到 用户登录人的账户名
        //拿到账户名后，能否拿到 数据库中的密码？？？  能 拿到后做对比
        //怎么拿？？  单表的 查询 admin
        AdminExample example = new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();
        criteria.andAdminAccountEqualTo(account);
        List<Admin> admins = adminService.selectByExample(example);
        Admin dbAdmin = null;
        if(admins!=null &&admins.size()>0){
            dbAdmin = admins.get(0);
            //获取账户名和密码
            String pwd = dbAdmin.getAdminPwd();
            String salt = dbAdmin.getSalt();

            //获取token认证 获取数据库中的密码
            SimpleAuthenticationInfo simpleAuthenticationInfo =
                    new SimpleAuthenticationInfo(account, pwd, ByteSource.Util.bytes(salt), this.getName());
            System.out.println("ByteSource.Util.bytes(salt) = " + ByteSource.Util.bytes(salt));
            return simpleAuthenticationInfo;
        }



        return null;
    }
}
