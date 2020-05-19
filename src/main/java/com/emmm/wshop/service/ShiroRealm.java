package com.emmm.wshop.service;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroRealm extends AuthorizingRealm {
    private final VerificiationCodeCheckService verificiationCodeCheckService;

    @Autowired
    public ShiroRealm(VerificiationCodeCheckService verificiationCodeCheckService) {
        this.verificiationCodeCheckService = verificiationCodeCheckService;
        this.setCredentialsMatcher((token, info) -> new String((char[]) token.getCredentials()).equals(info.getCredentials()));
    }

    // 有没有权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    // 是不是这个
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String tel = (String) token.getPrincipal();
        String correctCode = verificiationCodeCheckService.getCorrectCode(tel);

        return new SimpleAuthenticationInfo(tel, correctCode, getName());
    }
}
