package com.jaden.mybook.dao;

import com.jaden.mybook.bean.User;
import com.jaden.mybook.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDao  {
    //创建QueryRunner对象-->利用DBUtil
     QueryRunner queryRunner = new QueryRunner();

     //getUser()
     // 异常抛出(通知Biz)
    public User getUser(String name,String pwd) throws SQLException {
        //1.调用DBHelper获取对象
        Connection connection =DBHelper.getConnection();
        //2.sql执行
        //建议去数据库操作然后复制
        String sql = "SELECT * FROM `user` WHERE NAME =? AND pwd =? AND state ='1'";
        //语句太长可能有换行符，注意‘\n’,注意空格是否正确
        //将固定改为?  name=?
        //3.封装User
        //QueryRunner 使一行数据表封装为一个类
        //反射四个对象为一个
    User user = queryRunner.query(connection, sql, new BeanHandler<User>(User.class),name,pwd);
    //4.关闭连接对象
        connection.close();
    //5.返回user
        return user;
    }
    public int modifyPwd(long id,String pwd) throws SQLException {
        //pwd为新密码
        String sql ="UPDATE user set pwd =? WHERE id=?";
        Connection connection = DBHelper.getConnection();
        //返回一个数据行数范围
        int count = queryRunner.update(connection,sql,pwd,id);//注意顺序与sql语序一致
        connection.close();
        return count;
    }
//测试
    public static void main(String[] args) {
        try {
            System.out.println(new UserDao().getUser("super", "123"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
