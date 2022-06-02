package com.jaden.mybook.action;

import com.jaden.mybook.bean.Type;
import com.jaden.mybook.biz.TypeBiz;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet ("/type.let")
public class TypeServlet extends HttpServlet {
    TypeBiz typeBiz = new TypeBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);//依旧不写doPost
    }
    /*      ctrl +shift+/多行注释
    /type.let?type=add 添加类型
    /type.let?type=modifypre 修改的准备(确定改哪一行数据)
    /type.let?type=modify  修改类型
    /type.let?type=remove&id=xx  删除类型-->不需要表单，但是需要确定删who的编号
    查询不需要，直接在/type_list.jsp中查询即可(类型数据存放位置：application对象)
    *application同一个项目只有一个，经常使用却不变化-->需要一个监视器
    * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //0.完成查询
        //0.1设置编码
        req.setCharacterEncoding("utf-8");//响应
        resp.setContentType("text/html;charset=utf-8");//输出
        //0.2各种对象
        PrintWriter out = resp.getWriter();//out对象
        ServletContext application = req.getServletContext();//application对象
        //1.添加业务
        String type = req.getParameter("type");
        switch (type){
            case "add":
                add(req,resp,out,application);
                break;
            case "modifypre":
                modifypre(req,resp,out,application);
                break;
            case "modify":
                modify(req,resp,out,application);
                break;
            case "remove":
                remove(req,resp,out,application);
                break;
        }








    }

    private void remove(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
//     1.获取需要删除的id
        long id = Long.parseLong(req.getParameter("id"));
//     2.调用方法，biz层异常
        try {
            int count = typeBiz.remove(id);
            if(count>0){
                List<Type> types = typeBiz.getAll();
                application.setAttribute("types",types);//重新放application属性-->更新
                //提示并跳转
                out.println("<script>alert('删除成功');location.href ='type_list.jsp';</script>");
            }else {
                out.println("<script>alert('删除失败');location.href ='type_list.jsp';</script>");
            }
        } catch (Exception e) {//异常信息由biz传入
            out.println("<script>alert('"+e.getMessage()+"');location.href='type_list.jsp'</script>");
//            throw new RuntimeException(e);
        }
    }




    private void modify(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        //1.获取表单中的数据 id(hidden) name parentId
        long id = Long.parseLong(req.getParameter("typeId"));
        String name = req.getParameter("typeName");
        long parentId = Long.parseLong(req.getParameter("parentType"));
        //2.调用biz修改方法
        int count = typeBiz.modify(id, name, parentId);
        //3.更新application
        if(count>0){
            List<Type> types = typeBiz.getAll();
            application.setAttribute("types",types);//重新放application属性-->更新
            //提示并跳转
            out.println("<script>alert('修改成功');location.href ='type_list.jsp';</script>");
        }else {
            out.println("<script>alert('修改失败');location.href ='type_list.jsp';</script>");
        }
    }
//preview
    private void modifypre(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) throws ServletException, IOException {
        //1.获取需要修改的type对象的编号(id)
        long id = Long.parseLong(req.getParameter("id"));
        //2.根据id获取type对象
        Type typeBizById = typeBiz.getById(id);
        //3.把type存到req中，同一个功能req
        req.setAttribute("type",typeBizById);//存属性,传入type
        //转发
        req.getRequestDispatcher("type_modify.jsp").forward(req,resp);

    }
    /**
     * 1.进入type_add.jsp
     */
    private void add(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        //1.从表单获取名字，父类型
        String typeName = req.getParameter("typeName");
        long parentId = Long.parseLong(req.getParameter("parentType"));//将字符串转long
        //2.调用方法biz
       int count = typeBiz.add(typeName,parentId);
        //3.更新application中的types
        if(count>0){
            List<Type> types = typeBiz.getAll();
            application.setAttribute("types",types);//重新放application属性-->更新
            //提示并跳转
            out.println("<script>alert('添加成功');location.href ='type_list.jsp';</script>");
        }else {
            out.println("<script>alert('添加失败');location.href ='type_add.jsp';</script>");
        }
    }

}
