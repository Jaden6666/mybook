package com.jaden.mybook.action;

import com.jaden.mybook.bean.Book;
import com.jaden.mybook.biz.BookBiz;
import com.jaden.mybook.dao.BookDao;
import com.jaden.mybook.util.DateHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/book.let")
public class BookServlet extends HttpServlet {
    BookDao bookDao = new BookDao();
    BookBiz bookBiz = new BookBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    /**
     * /book.let?type=add 添加图书
     * /book.let?type=modifypre&id=xx  修改之前的准备
     * /book.let?type=modify  改
     * /book.let?type=remove&id=xxx 删
     * /book.let?type=query&pageIndex=1  分页查询(转发request)
     * /book.let?type=details&id=xx  展示书籍详细信息
     * /book.let?type=inquier 查询图书信息
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //0.1设置编码
        req.setCharacterEncoding("utf-8");//响应
        resp.setContentType("text/html;charset=utf-8");//输出
        //0.2各种对象
        PrintWriter out = resp.getWriter();//out对象
        //1.添加业务
        String type = req.getParameter("type");
        switch (type){
            case "add":
                try {
                    add(req,resp,out);
                } catch (FileUploadException e) {
                    resp.sendError(500,"文件上传失败");
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    resp.sendError(500,e.getLocalizedMessage());//自己不清楚什么异常，get到
                    throw new RuntimeException(e);
                }
                break; 
            case "modifypre":
                out.println("<script>alert('正在转发请求');</script>");
                long bookId = Long.parseLong(req.getParameter("id"));
                Book book =bookBiz.getById(bookId);
                req.setAttribute("book",book);
                req.getRequestDispatcher("book_modify.jsp").forward(req,resp);
                break; 
            case "modify":
                try {
                    modify(req,resp,out);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case "remove":remove(req,resp,out);
                break;
            case "query":query(req,resp,out);
                break;
            case "details":details(req,resp,out);
                break;
            case "inquier":inquier(req,resp,out);
                break;
            default:resp.sendError(404);    //输入与目标不相等
        }
    }

    private void inquier(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws ServletException, IOException {
        //1.从表单获取输入，查询方式
        String typeText = req.getParameter("inquireText");
//        long parentId = Long.parseLong(req.getParameter("parentType"));//将字符串转long
        //查询类型
        String inquireType = req.getParameter("inquireType");
        switch (inquireType){
            case "bookId":
                //按照id查询
                long bookId = Long.parseLong(typeText);
                //2.根据编号获取图书对象
                try {
                    if(bookDao.getById(bookId)!=null) {
                    Book book = bookBiz.getById(bookId);
                        //3.将对象保存到req
                        req.setAttribute("book", book);
                        //4.转发到 jsp页面
                        req.getRequestDispatcher("book_inquire_details.jsp").forward(req, resp);
                    }
                    else {
                        out.println("<script>alert('找不到该编号');location.href='book_inquire.jsp';</script>");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }


    }

    private void details(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws ServletException, IOException {
        //1.获取图书的编号
        long bookId =  Long.parseLong(req.getParameter("id"));
        //2.根据编号获取图书对象
        Book book = bookBiz.getById(bookId);
        //3.将对象保存到req
        req.setAttribute("book",book);
        //4.转发到 jsp页面
        req.getRequestDispatcher("book_details.jsp").forward(req,resp);
    }

    /**
     * book.let?type=query&pageIndex = 1
     * 页数
     * 当前页码
     * @param req
     * @param resp
     * @param out
     */
    private void query(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws ServletException, IOException {
        //1.获取信息
        int pageSize=3;
        //页数
        int pageCount = bookBiz.getPageCount(pageSize);
        //页码
        int pageIndex = Integer.parseInt(req.getParameter("pageIndex"));
        if(pageIndex<1){
            pageIndex=1;
        }
        if(pageIndex>pageCount)
        {
            pageIndex = pageCount;
        }
        //信息
        List<Book> books =bookBiz.getByPage(pageIndex,pageSize);
        //2.存入 响应对象
        req.setAttribute("pageCount",pageCount);
        req.setAttribute("books",books);
        //3.转发到jsp界面
        req.getRequestDispatcher("book_list.jsp?pageIndex="+pageIndex).forward(req,resp);//pageIndex可以从地址栏转移

    }

    private void remove(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        //获取删除id
        long removeId = Long.parseLong(req.getParameter("id"));
        //调用biz
        try {
          int count = bookBiz.remove(removeId);
          if (count>0){
              out.println("<script>alert('删除成功');location.href='book.let?type=query&pageIndex=1';</script>");
          }else {
              out.println("<script>alert('删除失败');location.href='book.let?type=query&pageIndex=1';</script>");
          }
        } catch (Exception e) {
            out.println("<script>alert('"+e.getMessage()+"');location.href='book.let?type=query&pageIndex=1';</script>");
        }
        //提示加跳转->查询
    }

    /**
     * 与添加类似
     * @param req
     * @param resp
     * @param out
     */
    //此处用老师的源码替换->正常
    private void modify(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws Exception {

        //1.构建一个磁盘工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //1.1 设置大小
        factory.setSizeThreshold(1024*9);
        //1.2 临时仓库
        File file = new File("c:\\temp");
        if(!file.exists()){
            file.mkdir();//创建文件夹
        }
        factory.setRepository(file);

        //2.文件上传+表单数据
        ServletFileUpload  fileUpload = new ServletFileUpload(factory);

        //3.将请求解析成一个个FileItem(文件+表单元素)
        List<FileItem> fileItems = fileUpload.parseRequest(req);

        //4.遍历FileItem
        Book book  =new Book();
        for(FileItem  item: fileItems){
            if(item.isFormField()){
                //4.1 元素名称和用户填写的值  name: 文城
                String  name = item.getFieldName();
                String value = item.getString("utf-8");//防止乱码
                switch(name){
                    case "id":
                        book.setId(Long.parseLong(value));
                        break;
                    case "pic":
                        book.setPic(value);
                        break;
                    case "typeId":
                        book.setTypeId(Long.parseLong(value));
                        break;
                    case "name":
                        book.setName(value);
                        break;
                    case "price":
                        book.setPrice(Double.parseDouble(value));
                        break;
                    case "desc":
                        book.setDesc(value);
                        break;
                    case "publish":
                        book.setPublish(value);
                        break;
                    case "author":
                        book.setAuthor(value);
                        break;
                    case "stock":
                        book.setStock(Long.parseLong(value));
                        break;
                    case "address":
                        book.setAddress(value);
                        break;
                }

            }else {
                String fileName = item.getName();
                if(fileName.trim().length()>0) {
                //1.改名
                //1.1获取后缀名suffix
                String suffixName =fileName.substring(fileName.lastIndexOf("."));
                //1.2加时间标签   CoreJava202205181545.jpeg
                fileName = fileName+DateHelper.getImageName()+suffixName;
                //2.保存
                //2.1实际路径 D:\ME\Java\JavaWeb\mybook\src\main\webapp\Images\cover
                String path = req.getServletContext().getRealPath("/Images/cover");
                //实际文件位置=实际路径+文件名
                String filePath = path +"/"+fileName;
                //2.3 数据库存储的路径---->只是上传了文件名
                String dbPath = "Images/cover/"+fileName;
                book.setPic(dbPath);

                    //4.3 保存文件
                    item.write(new File(filePath));
                }
            }
        }

        //5.将信息保存到数据库
        int count = bookBiz.modify(book);
        if(count>0){
            out.println("<script>alert('修改书籍成功');location.href='book.let?type=query&pageIndex=1';</script>");
        }else{
            out.println("<script>alert('修改书籍失败');location.href='book.let?type=query&pageIndex=1';</script>");
        }
    }
//    /book.let?type=modifypre&id=xx

//    private void modifypre(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws ServletException, IOException {
//        long bookId = Long.parseLong(req.getParameter("id"));
//        Book book =bookBiz.getById(bookId);
//        req.setAttribute("book",book);
//        req.getRequestDispatcher("book_modify.jsp").forward(req,resp);
//    }

    /**
     * 1.enctype="multipart/form-data" 之前用req.getParameter("name");
     * 现在不行
     * 2.需要从服务器上传图片-->借助三方jar包FileUpload+io
     * 3.文件路径
     * D:\ME\Java\JavaWeb\mybook\src\main\webapp\Images\cover\CoreJava,jpeg--实际路径
     * http://localhost:8080/mybook_explored_war/Images/cover/CoreJava.jpeg--虚拟路径（服务器）
     * Mysql存储的pic属性中为虚拟路径
     * @param req
     * @param resp
     * @param out
     */
    private void add(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws Exception {
        //需要工厂->存放磁盘文件
        //1.构建磁盘工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //1.1磁盘大小设置(每一次读写的大小)
        factory.setSizeThreshold(1024*9);
        //1.2临时仓库
        File  file = new File("d;\\Temp\\testTemp");
        //不能判断是否有这个文件夹
        if(!file.exists()){
            file.mkdir();//如果没有则创建
        }//将该工厂设为仓库
        factory.setRepository(file);
        //2.上传文件+表单
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        //3.将请求解析成FileItem
        //此处的异常需要抛出而不是处理,抛给switch case捕获
        List<FileItem> fileItems = servletFileUpload.parseRequest(req);
        //4.遍历fileItems 区分是表单还是文件
        Book book = new Book();
        for (FileItem item : fileItems) {
            //form多
            if(item.isFormField()){
                //元素名称+值
                String name = item.getFieldName();//依旧得编码utf-8-->上方的设置在文件不生效
                String value = item.getString("utf-8");//防止乱码
                switch(name){//这样比较low，可以用反射来操作
                    case "typeId":
                        book.setTypeId(Long.parseLong(value));
                        break;
                    case "name":
                        book.setName(value);
                        break;
                    case "price":
                        book.setPrice(Double.parseDouble(value));
                        break;
                    case "desc":
                        book.setDesc(value);
                        break;
//                    case "pic":   pic目前不存在因为，pic是文件
//                        book.setPic(value);
//                        break;
                    case "publish":
                        book.setPublish(value);
                        break;
                    case "author":
                        book.setAuthor(value);
                        break;
                    case "stock":
                        book.setStock(Long.parseLong(value));
                        break;
                    case "address":
                        book.setAddress(value);
                        break;
                }
            }else {
                //文件-->文件名
                //文件的保存比较复杂
                //文件名如果相同怎么处理-加一个时间标签
                //DateHelper
                String fileName = item.getName();
                //位置问题
//             D:\ME\Java\JavaWeb\mybook\src\main\webapp\Images\cover\CoreJava,jpeg--实际路径
//             http://localhost:8080/mybook_explored_war/Images/cover/CoreJava.jpeg--虚拟路径（服务器）
//             Mysql存储的pic属性中为虚拟路径
                //1.改名
                //1.1获取后缀名suffix
                String suffixName =fileName.substring(fileName.lastIndexOf("."));
                //1.2加时间标签   CoreJava202205181545.jpeg
                fileName = fileName+DateHelper.getImageName()+suffixName;
                //2.保存
                //2.1实际路径 D:\ME\Java\JavaWeb\mybook\src\main\webapp\Images\cover
                String path = req.getServletContext().getRealPath("/Images/cover");
                //实际文件位置=实际路径+文件名
                String filePath = path +"/"+fileName;
                //2.3 数据库存储的路径---->只是上传了文件名
                String dbPath = "Images/cover/"+fileName;
                book.setPic(dbPath);
                //文件上传
                item.write(new File(filePath));//写入实际位置
            }
        }
        //将信息写入数据库
        int count = bookBiz.add(book);
        if(count>0){//不能直接跳到BookList-->因为和type不一样，这个不存在application内
            //跳到servlet(本servlet)
            out.println("<script>alert('添加书籍成功');location.href ='book.let?type=query&pageIndex=1';</script>");
        }else {
            out.println("<script>alert('添加书籍失败');location.href ='book.let?type=add';</script>");

        }

    }
}
