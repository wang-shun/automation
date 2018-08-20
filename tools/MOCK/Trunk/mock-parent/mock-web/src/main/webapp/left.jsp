<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/css/index.css"/>
    <link rel="stylesheet" href="/assets/jqTree-0.20.0/jqtree.css" />
    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"/>
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <button type="button" class="btn btn-navbar" data-toggle="collapse"
                    data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>


            <div class="nav-collapse collapse">
                <ul class="nav">

                </ul>
            </div>
        </div>
    </div>
</div>

<div class="main-div" style="margin-top:-2px; " >
    <div id="case-tree-data" style="width: 350px;margin-left: 10px;">
    </div>
</div>


<script type="text/javascript" src="/assets/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/assets/jquery-cookie/jquery.cookie.js"></script>
<script type="text/javascript" src="/js/left.js"></script>
<script type="text/javascript" src="/assets/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/assets/jqTree-0.20.0/tree.jquery.js">
</script>

<%--<script type="text/javascript" src="/js/frame.js"></script>--%>
</body>
</html>








