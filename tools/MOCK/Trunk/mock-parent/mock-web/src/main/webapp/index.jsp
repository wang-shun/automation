
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Mock Home</title>
    <link href="/assets/jquery-datepicker/jquery.datetimepicker.css" rel="stylesheet" type="text/css"/>
    <%--<link href="/css/home.css" rel="stylesheet" type="text/css"/>--%>

</head>

<frameset name="forumFrameSet" frameSpacing="5" borderColor="#9dbfe6" frameBorder="1"
          cols="162,*">
    <frame name="forumMenuTree"  src="/left"  scrolling=auto>
    <frame name="main" src="/api/search" DESIGNTIMEDRAGDROP="9">
    <noframes>
        <body>
        <p>此网页使用了框架，但您的浏览器不支持框架。</p>
        </body>
    </noframes>
</frameset>




</html>