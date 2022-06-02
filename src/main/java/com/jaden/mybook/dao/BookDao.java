package com.jaden.mybook.dao;

import com.jaden.mybook.bean.Book;
import com.jaden.mybook.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookDao {
    QueryRunner queryRunner = new QueryRunner();

    //根据类型查询书籍信息
    public List<Book> getBooksByTypeId(long typeId) throws SQLException {
        Connection connection = DBHelper.getConnection();
        String sql ="select * from book where typeId = ?";
        List<Book> books = queryRunner.query(connection,sql,new BeanListHandler<Book>(Book.class),typeId);
        connection.close();
        return books;
    }

//    public static void main(String[] args) {
//        try {
//            List<Book>books = new BookDao().getBooksByTypeId(2);
//            System.out.println(books.size());//[],books对象存在，不存在数据
//            //打印size()为0
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            BookDao bookDao = new BookDao();
//            int count = bookDao.getCount();
//            List <Book> books = bookDao.getByPage(1,3);
//            for (Book book : books) {
//                System.out.println(book);
//            }
//            System.out.println(count);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        //改
//        BookDao bookDao = new BookDao();
//        try {
//            bookDao.modify(2,13,"测试",30,"测试测试aa","Images/cover/第一序列.jpeg202205181649.jpeg","华夏出版社","义",60,"休闲库");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


    //增
    public int add(long typeId,String name,double price,String desc,String pic,
                   String publish,String author,long stock,String address) throws SQLException {
        Connection connection = DBHelper.getConnection();
        String sql = "INSERT into book(typeId,`name`,price,`desc`,pic,publish,author,stock,address) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";//8个
        int count = queryRunner.update(connection,sql,typeId,name,price,desc,pic,publish,author,stock,address);
        connection.close();
        return count;
    }//改
    public int modify(long id,long typeId,String name,double price,String desc,String pic,
                      String publish,String author,long stock,String address) throws SQLException {
        Connection connection  = DBHelper.getConnection();
        String sql="update book set typeId= ?,`name` = ?,price =?,`desc`=?,pic = ?,publish = ?,author =?,stock=?,address = ? where id= ? ";
        int count = queryRunner.update(connection,sql,typeId,name,price,desc,pic,publish,author,stock,address,id);
        connection.close();
        return count;
    }
    /**
     * 修改书籍的数量
     * @param id
     * @param amount  整数:+1  负数 -1
     * @return
     * @throws SQLException
     */
    public int modify(long id,int amount) throws SQLException {
        Connection conn  = DBHelper.getConnection();
        String sql="update book set stock=stock + ? where id= ? ";
        int count = queryRunner.update(conn,sql,amount,id);
        DBHelper.close(conn);
        return count;

    }
    //删
    public int remove(long id) throws SQLException {
        Connection connection  = DBHelper.getConnection();
        String sql="delete from book where id=? ";
        int count = queryRunner.update(connection,sql,id);
        DBHelper.close(connection);
        return count;
    }//分页查询--排序此处按照主键id排
    public List<Book> getByPage(int pageIndex,int pageSize) throws SQLException {
        Connection connection = DBHelper.getConnection();
        String sql = "select * from book limit ?,?";
        //offset即开始?
        int offset = (pageIndex-1)*pageSize;
        List<Book> books=queryRunner.query(connection,sql,new BeanListHandler<Book>(Book.class),offset,pageSize);
        connection.close();
        return books;
    }
    public Book getById(long id) throws SQLException {
        Connection connection = DBHelper.getConnection();
        String sql = "select * from book where id=?";
        Book book=queryRunner.query(connection,sql,new BeanHandler<Book>(Book.class),id);
        connection.close();
        return book;
    }//获取书籍数量
    public int getCount() throws SQLException {
        Connection connection = DBHelper.getConnection();
        String sql = "SELECT COUNT(id) FROM book ";
        //封装为Object
        Object data = queryRunner.query(connection,sql,new ScalarHandler<>());
//        System.out.println(data.getClass());
//       Number data1 = queryRunner.query(connection,sql,new ScalarHandler<>());
//       int count1 =data1.intValue();
        int count = (int)((long) data);
        connection.close();
        return count;
    }
}
