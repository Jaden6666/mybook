package com.jaden.mybook.listener;

import com.jaden.mybook.action.*;
import com.jaden.mybook.bean.Type;
import com.jaden.mybook.biz.TypeBiz;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;

@WebListener
//application创建自动执行此代码
public class TypeServletContextListener implements ServletContextListener {
    @Override//数据准备
    public void contextInitialized(ServletContextEvent sce) {
//        ServletContextListener.super.contextInitialized(sce);
        //1.获取当前数据库所有类型信息
        TypeBiz typeBiz = new TypeBiz();
        List<Type> types = typeBiz.getAll();
        //2.获取application对象
        //目前获取到sce
        ServletContext application = sce.getServletContext();
        //3.将信息存在application中
        application.setAttribute("types",types);//所有当前页面都可以使用types
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
