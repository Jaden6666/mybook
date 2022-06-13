package com.jaden.mybook.biz;

import com.jaden.mybook.bean.Book;
import com.jaden.mybook.bean.Record;
import com.jaden.mybook.bean.Type;
import com.jaden.mybook.dao.BookDao;
import com.jaden.mybook.dao.RecordDao;
import com.jaden.mybook.dao.TypeDao;

import java.sql.SQLException;
import java.util.List;

public class BookBiz {
       BookDao bookDao = new BookDao();
    public List<Book> getBooksByTypeId(long typeId) {
        try {
            return bookDao.getBooksByTypeId(typeId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int add(long typeId, String name, double price, String desc, String pic,
                   String publish, String author, long stock, String address) {
        try {
            int count;
            count = bookDao.add(typeId,name,price,desc,pic,publish,author,stock,address);
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //重写
    public int add(Book book){
      return   add(book.getTypeId(),book.getName(),book.getPrice(),book.getDesc(),book.getPic(),book.getPublish(),book.getAuthor(),book.getStock(),book.getAddress());
    }

    public int modify(long id, long typeId, String name, double price, String desc, String pic,
                      String publish, String author, long stock, String address) {
        try {
            return bookDao.modify(id,typeId,name,price,desc,pic,publish,author,stock,address);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public int modify(Book book){
        int count = 0;
        try {
          count = bookDao.modify(book.getId(), book.getTypeId(), book.getName(), book.getPrice(),book.getDesc(),
            book.getPic(), book.getPublish(), book.getAuthor(), book.getStock(), book.getAddress());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
    public int remove(long id) throws Exception {
        //1.判断是否为外键
        RecordDao recordDao = new RecordDao();
        try {
            List<Record> records =recordDao.getRecordByBookId(id);
            if(records.size()>0){
                //自己抛出去一个异常
                throw new Exception("删除的书籍有子信息，不可删除");
            }
            //删除
            return bookDao.remove(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//重点，通过Biz补上缺失的图书类型
    public List<Book> getByPage(int pageIndex, int pageSize) {
        try {
            TypeBiz typeBiz = new TypeBiz();
            List<Book> books = null;
            books = bookDao.getByPage(pageIndex,pageSize);
            //处理type对象的数据问题
            for (Book book : books) {
               long typeId = book.getTypeId();
                //根据typeId找到对应type对象
                Type type =typeBiz.getById(typeId);
                //设置给book.setType
                book.setType(type);
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Book getById(long id) {
        Book book = null;
        TypeDao typeDao = new TypeDao();
        try {//补充上单个book的type属性
            //将完整的book对象返回
             book = bookDao.getById(id);//缺Type属性的book
            long typeId = book.getTypeId();
            Type type = typeDao.getById(typeId);//拥有type属性的新book
            book.setType(type);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return book;//按照bookId将完整的book对象返回
    }
    //根据名字
    public Book getByName(String bookName)  {
        Book book = null;
        try {
            book = bookDao.getByName(bookName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return book;
    }
//算页数--通过行数
    public int getPageCount(int pageSize) {
        try {
            int rowCount = bookDao.getCount();
            int pageCount =0;
            //根据行数得到页数
             pageCount = (rowCount - 1) / pageSize + 1;
             return pageCount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}






