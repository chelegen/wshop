package com.emmm.wshop.service;

public interface SmsCodeService {
    /**
     * 向目标手机号发送验证码，返回正确答案
     *
     * @param tel 目标手机号
     * @return 验证码正确答案
     */
    String sendSmsCode(String tel);
}
