package com.emmm.wshop.controller;

import com.emmm.wshop.generate.User;
import com.emmm.wshop.service.AuthService;
import com.emmm.wshop.service.TelVerificationService;
import com.emmm.wshop.service.UserContext;
import com.emmm.wshop.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final TelVerificationService telVerificationService;
//    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, TelVerificationService telVerificationService, UserService userService) {
        this.authService = authService;
        this.telVerificationService = telVerificationService;
//        this.userService = userService;
    }


    @PostMapping("/code")
    public void code(@RequestBody TelAndCode telAndCode,
                     HttpServletResponse response) {
        if (telVerificationService.verifyTelParameter(telAndCode)) {
            authService.sendVerificationCode(telAndCode.getTel());
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                telAndCode.getTel(),
                telAndCode.getCode());
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
    }

    public static class LoginResponse {
        private boolean login;
        private User user;

        public static LoginResponse notLogin() {
            return new LoginResponse(false, null);
        }

        public static LoginResponse Login(User user) {
            return new LoginResponse(true, user);
        }

        private LoginResponse(boolean login, User user) {
            this.login = login;
            this.user = user;
        }

        public boolean isLogin() {
            return login;
        }

        public User getUser() {
            return user;
        }
    }

    @GetMapping("/status")
    public Object loginStatus() {
        if (UserContext.getCurrentUser() == null) {
            return LoginResponse.notLogin();
        } else {
            return LoginResponse.Login(UserContext.getCurrentUser());
        }
//        System.out.println(SecurityUtils.getSubject().getPrincipal());
//        User userByTel = userService.getUserByTel((String) SecurityUtils.getSubject().getPrincipal());
//        System.out.println(userByTel.getTel());
//        return null;
    }

    public static class TelAndCode {
        private String tel;
        private String code;

        public TelAndCode(String tel, String code) {
            this.tel = tel;
            this.code = code;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
