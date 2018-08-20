<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="基于关键字、数据驱动的SpringMVC测试框架编辑器">
    <title>GTP-Mock-MQ</title>
    <!-- jsoneditor -->
    <link rel="stylesheet" type="text/css"
          href="/assets/jsoneditor/jsoneditor-min.css"/>

    <!-- perfect-scrollbar -->
    <link rel="stylesheet" type="text/css"
          href="/assets/perfect-scrollbar/perfect-scrollbar.min.css"/>
    <!-- bootstrap -->
    <link rel="stylesheet" type="text/css"
          href="/assets/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="/css/mqs.css"/>
    <link rel="stylesheet" type="text/css"
          href="/assets/bootstrap/css/bootstrap-responsive.min.css"/>
    <!-- bootstrap-modal -->
    <link rel="stylesheet" type="text/css"
          href="/assets/bootstrap-modal/css/bootstrap-modal.css"/>
    <link rel="stylesheet" href="/assets/datatables/css/jquery.dataTables.min.css"/>
    <!--[if lt IE 9]>
    <script type="text/javascript" src="/assets/html5shiv/html5shiv.min.js">
    </script>
    <script type="text/javascript" src="/assets/respond/respond.min.js">
    </script>
    <![endif]-->
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">

            <div class="nav-collapse collapse">
                <ul class="nav">
                    <li class="navbar-nav navbar-right">
                        <a class="brand" href="javascript:void(0);">GTP-Mock-MQ</a>
                    </li>

                </ul>

            </div>
            <ul class="nav navbar-nav navbar-left">
                <li><a><span>&mid;</span></a></li>
            </ul>

            <ul class="nav navbar-nav">
                <li class="nav-tab">
                    <a class="brand" href="javascript:openUrl('http://10.144.32.129'); ">Gome Test Platform</a>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-left">
                <li><a><span>&mid;</span></a></li>
            </ul>
            <ul class="nav navbar-nav">
                <li class="nav-tab">
                    <a class="brand"
                       href="javascript:openUrl('http://wiki.ds.gome.com.cn/pages/viewpage.action?pageId=4915895'); ">使用说明</a>
                </li>
            </ul>


        </div>
    </div>
</div>
<div class="container">
    <%--<div class="row test-filter">--%>
    <div class="main-div" style="margin-bottom: 0px;padding-bottom: 0px;">
        <div id="home_div">
            <div id="jum-div" class="container">
                <div class="jumbotron">
                    <h1>GTP Mock MQ</h1>
                </div>
            </div>
            <div class="container">
                <div class="row sub-row">
                    <div class="col-sm-4">
                        <div class="thumbnail">
                            <div class="caption">


                                <p>&nbsp;&nbsp;欢迎使用Gome-Mock-MQ管理工具</p>
                                <p>&nbsp;&nbsp;如有问题请联系: liuyao-ds1@yolo24.com ; zhangjiadi@yolo24.com</p>

                            </div>
                        </div>
                    </div>

                </div>
            </div>

            <div style="border-bottom: 2px solid #000;margin-top: 10px;margin-bottom: 10px;"></div><br>
            <div class="container">
                <div class="row">
                    <div style="text-align: center">&copy;2015&nbsp; Gome-Test</div><br><br><br>
                </div>
            </div>
        </div>

        <%--</div>--%>
    </div>
</div>
<script type="text/javascript" src="/js/utils.js"></script>
</body>
</html>
