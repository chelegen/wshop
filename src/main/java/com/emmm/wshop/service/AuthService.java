package com.emmm.wshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final SmsCodeService smsCodeService;
    private final VerificiationCodeCheckService verificiationCodeCheckService;

    @Autowired
    public AuthService(UserService userService, SmsCodeService smsCodeService, VerificiationCodeCheckService verificiationCodeCheckService) {
        this.userService = userService;
        this.smsCodeService = smsCodeService;
        this.verificiationCodeCheckService = verificiationCodeCheckService;
    }

    public void sendVerificationCode(String tel) {
        userService.createUserIfNotExist(tel);
        String correctCode = smsCodeService.sendSmsCode(tel);
        verificiationCodeCheckService.addCode(tel, correctCode);
    }
}
