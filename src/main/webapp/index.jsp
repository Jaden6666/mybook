<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html >
<head>
    <title>义图科技 - 网站后台管理中心</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="description" content="义的网站后台管理系统" />
    <meta name="keywords" content="快乐,网站管理系统,企业网站" />
    <meta name="Author" content="Jaden" />
    <meta name="CopyRight" content="义图科技" />
    <link rel="shortcut icon"href="./Style/yilogo.ico">
</head>
<frameset rows="64,*"  frameborder="no" border="0" framespacing="0">
    <!--头部-->
    <frame src="./top.jsp" name="top" noresize="noresize" frameborder="0"  scrolling="no" marginwidth="0" marginheight="0" />
    <!--主体部分-->
    <frameset cols="185,*">
        <!--主体左部分-->
        <frame src="./left.jsp" name="left" noresize="noresize" frameborder="0" scrolling="no" marginwidth="0" marginheight="0" />
        <!--主体右部分-->
        <frame src="./main.jsp" name="main" frameborder="0" scrolling="auto" marginwidth="0" marginheight="0" />
    </frameset>
</html>