package com.emmm.wshop.service;

import com.emmm.wshop.dao.UserDao;
import com.emmm.wshop.generate.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        try {
            userDao.insertUser(user);
        } catch (PersistenceException e) {
            return userDao.getUserByTel(tel);
        }
        return user;
    }

    /**
     * 根据tel返回用户，如果用户不存在，返回null
     *
     * @param tel
     * @return
     */
//    public Optional<User> getUserByTel(String tel) {
    public User getUserByTel(String tel) {
//        return Optional.ofNullable(userDao.getUserByTel(tel));
        return userDao.getUserByTel(tel);
    }
}
