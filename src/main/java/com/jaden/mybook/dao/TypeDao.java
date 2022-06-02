package com.jaden.mybook.dao;

import com.jaden.mybook.bean.Type;
import com.jaden.mybook.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TypeDao {
    //构建QueryRunner对象
    QueryRunner queryRunner = new QueryRunner();
    //添加类型--自动增长链(id不传)
    public int add(String name,long parentId) throws SQLException {
        Connection connection = DBHelper.getConnection();//减短操作时间--资源相关代码需要关注何时关闭
        // 因此connection复制几个到不同方法
        String sql="INSERT into type VALUES(null,?,?)";
        int count = queryRunner.update(connection,sql,name,parentId);
        connection.close();
        return count;
    }
    public Type getById(long typeId) throws SQLException {//获取特定类型
        Connection connection = DBHelper.getConnection();
        Type type = null;
        String sql = "select id,name,parentId from type where id=?";
        type = queryRunner.query(connection,sql,new BeanHandler<Type>(Type.class),typeId);
        connection.close();
        return type;
    }
    public List<Type> getAll() throws SQLException {
        Connection connection = DBHelper.getConnection();
        String sql = "select id,name,parentId from type ";//用*执行时数据多
        List<Type> types=queryRunner.query(connection,sql,new BeanListHandler<Type>(Type.class));
        connection.close();
        return types;//获取所有类型
    }
    public int modify(long id,String name,long parentId) throws SQLException {//修改类型
        Connection connection = DBHelper.getConnection();
        String sql="update type set name=?,parentId=? where id = ?";//id不改
        int count = queryRunner.update(connection,sql,name,parentId,id);
        connection.close();
        return count;
    }
    public int remove(long id) throws SQLException {//物理删除非逻辑删除
        Connection connection = DBHelper.getConnection();
        String sql="delete from type where id = ?";//id不改
        int count = queryRunner.update(connection,sql,id);
        connection.close();
        return count;
    }
    //测试
    public static void main(String[] args){
        try {
            List<Type> types =new TypeDao().getAll();
            System.out.println(types);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
