package com.jaden.mybook.biz;

import com.jaden.mybook.dao.BookDao;
import com.jaden.mybook.dao.TypeDao;
import com.jaden.mybook.bean.*;
import java.sql.SQLException;
import java.util.List;

public class TypeBiz {
   TypeDao typeDao = new TypeDao();
    public List<Type> getAll(){
        try {
            return typeDao.getAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;//另一种try的方法，不需要return-->throw new RuntimeException(e)
        }
    }
    public int add(String name,long parentId){
        int count = 0;
        try {
             count = typeDao.add(name,parentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
    public int modify(long id,String name,long parentId){
        try {
            int count = typeDao.modify(id, name, parentId);
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//        return 0:删除失败 return exception 业务不许删
    public int remove(long id) throws Exception {//注意删除，表与表是逻辑关系--主外键
        //如果不能remove（存在子项）
        BookDao bookDao = new BookDao();
        int count = 0;
        try {
            List<Book> books = bookDao.getBooksByTypeId(id);
            if(books.size()>0){
              //不能删除,信息需要通知Servlet
              //需要告诉用户不是删除失败，也不是数据库异常而是业务层不能操作
              throw new Exception("删除的类型被子信息占用，删除失败");//注意这个异常抛出
          }
            count = typeDao.remove(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
    public Type getById(long id){
        try {
            return typeDao.getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
