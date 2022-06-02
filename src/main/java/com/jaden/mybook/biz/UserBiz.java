package com.jaden.mybook.biz;

import com.jaden.mybook.bean.User;
import com.jaden.mybook.dao.UserDao;

import java.sql.SQLException;

public class UserBiz {
    //构建UserDao对象,定义到方法外通用
    UserDao userDao = new UserDao();
    public User getUser(String name,String pwd){
        User user = null;
        try {
           user = userDao.getUser(name, pwd);//有异常不用继续抛，直接处理掉
        } catch (SQLException e) {
            throw new RuntimeException(e); //异常存储于日志内
        }
        return user;
    }
    public int modifyPwd(long id,String pwd){
        int count = 0;//打造一个数据行
        try {
            count = userDao.modifyPwd(id, pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
}
