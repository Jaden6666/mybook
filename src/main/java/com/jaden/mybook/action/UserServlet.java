package com.jaden.mybook.action;

import com.jaden.mybook.bean.User;
import com.jaden.mybook.biz.UserBiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@WebServlet("/user.let")//映射
public class UserServlet extends HttpServlet {
    //构建biz
    UserBiz userBiz = new UserBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);//调用doPost,不写
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //补充session--验证码用到
        HttpSession session = req.getSession();
        //0.防乱码
        req.setCharacterEncoding("utf-8");//请求编码
        resp.setContentType("text/html;charset=utf-8");//响应
        //out对象
        PrintWriter out = resp.getWriter();
        //1.获取用户类型 login
        //通过type实现同一个url不同响应
        //登录地址   user.let?type=login
        //退出地址   user.let?type=exit
        //改密地址   user.let?type=modifyPwd
        //首页地址   user.let?type=home
        String method = req.getParameter("type");
        switch (method){
            case "login":
        //2.login.html中用户名和密码
                String name = req.getParameter("name");
                String pwd = req.getParameter("pwd");
                String userCode = req.getParameter("valcode");//用户表单提交的验证码
        //2.2提取session中的验证码
                // ValCodeServlet类中定义属性为code
                //转为string类型
                String code = session.getAttribute("code").toString();
                //不区分大小写的判断验证码
                if(!code.equalsIgnoreCase(userCode)){
                    out.println("<script>alert('验证码输入错误');location.href ='login.html';</script>");
                    return;//不需要继续运行
                }
//3.调用UserBiz的getUser 获取对象
        // 3.UserBiz 用多次在类定义
                User user = userBiz.getUser(name,pwd);
        //4.判空
                // 空->错误
                //非空->保存到session中
                if(user==null){
                    //需要提示用户错误，不是向系统输出,是PrintWriter
                    out.println("<script>alert('用户名或密码错误');location.href ='login.html';</script>");
                } else {//提示+跳转
                    //保存登录用户
                    session.setAttribute("user",user);//存的session为object类型
                    out.println("<script>alert('登陆成功');location.href ='index.jsp';</script>");
                }
        break;
                //退出地址   user.let?type=exit
            case "exit":
                //1.清除session
                session.invalidate();
                //2.跳转login.html--此时处于框架中，上方是页面跳转--top.jsp窗口中
                out.println("<script>parent.window.location.href ='login.html';</script>");
                break;
            case "modifyPwd":
                //改密
                //1.获取用户输入的密码
               String newpwd = req.getParameter("newpwd");
               //1.1自己优化密码一致的情况
               String newpwd2 = req.getParameter("newpwd2");
               if(!Objects.equals(newpwd, newpwd2)){
                   out.println("<script>alert('两次输入密码不一致');location.href ='set_pwd.jsp';</script>");
                   break;
               }
                //2.获取用户编号
                //理解这个操作，转为user
                // object向下转型(存储的是object)
//                ((User)session.getAttribute("user")).getId();
//                User user1 = (User)session.getAttribute("user");
//                user1.getId();
                Object object =session.getAttribute("user");
                long id =((User)object).getId();;
                //3.调用biz层
                int count = userBiz.modifyPwd(id,newpwd);
                //响应-->退出
                if(count>0){//依旧是框架中
                    out.println("<script>alert('密码修改成功');parent.window.location.href ='login.html';</script>");
                }else {
                    out.println("<script>alert('密码修改失败');</script>");
                }
                break;
            case "home" :{
                out.println("<script>parent.window.location.href ='index.jsp';</script>");
                break;
            }
    }
    }
}
