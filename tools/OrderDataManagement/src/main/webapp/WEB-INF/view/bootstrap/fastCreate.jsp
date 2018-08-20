<%--
  Created by IntelliJ IDEA.
  User: hacke
  Date: 2016/9/24
  Time: 22:47
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html >
<html lang="en">
<head>
    <title>订单创建</title>
  <link href="<%=request.getContextPath()%>/static/js/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <script src="<%=request.getContextPath()%>/static/js/jQuery/jquery-2.1.4.min.js"></script>
  <script src="<%=request.getContextPath()%>/static/js/bootstrap/js/bootstrap.min.js"></script>
  <!-- Pick a theme, load the plugin & initialize plugin -->
  <link href="<%=request.getContextPath()%>/static/css/theme.default.min.css" rel="stylesheet">


  <!-- JQuery, Bootstrap and Custom CSS files -->
  <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/jquery-ui.css" type="text/css" media="all">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/bootstrap.css" type="text/css" media="all">

  <script src="<%=request.getContextPath()%>/static/js/jQuery/jquery-ui.js"></script>

  <!-- This is the main javascript that defines the pie chart and its attributes -->




  <style type="text/css">
    #fastselectdiv{
      margin-right: auto;
      margin-left: auto;
      width:800px;
      color: blue;
    }
    #showcreateorderinfo {
      margin-right: auto;
      margin-left: auto;
      width:900px;
    }
    table {
      border-collapse: collapse;
      width: 100%;
    }

    th, td {
      text-align: left;
      padding: 8px;
    }

    tr:nth-child(even){background-color: #f2f2f2}

    th {
      background-color: #4CAF50;
      color: white;
    }
  </style>

</head>
<body text="#F0F0F0">
<p>&nbsp;</p>
<p>
  <label for="imageField"></label>
  <input name="imageField" type="image" id="imageField" src="../../static/img/ordermanagement.png" align="middle" />




<div id="fastselectdiv" >
  <p style="color:red">请选使用的环境：
    <select name="select5" id="selectENV"  style="color:#000000">
      <option value="uat">UAT</option>
      <option value="pre">PRE</option>
      <option value="sit">SIT</option>
    </select>
  </p>
  <p style="color:#000000">请输入商品SKU：  <input name="productSku" type="text" id="productSku"></p>


    <p style="color:#000000">请输入商品编号： <input name="productPid" type="text" id="productPid" >
    </p>

  <p style="color:#000000">请输入商品数量： <input name="productQuantity" type="text" id="productQuantity" >
  </p>

  <p style="color:red">绑定下单地址：
    <label for="select"></label>
    <select name="select" id="select" style="color:#000000">
      <option value="bj3">北京朝阳区三环内</option>
      <option value="sh3">上海浦东区三环内</option>
      <option value="sd">山东济南石头街</option>
    </select>
  </p>
  <p style="color:red">请选择支付方式：
    <label for="label2"></label>
    <select name="select2" id="label2"  style="color:#000000">
      <option value="gomepay">国美支付</option>
      <option value="unpay">银联快捷支付</option>
      <option value="weichatpay">微信支付</option>
      <option value="delaypay">货到付款</option>
    </select>
  </p>
  <p style="color:red">期望状态：
    <label for="label3"></label>
    <select name="select3" id="label3" style="color:#000000">
      <option value="pp">PP</option>
      <option value="dl">DL</option>
    </select>
  </p>
<p style="color:red">&nbsp;</p>
  <p style="color:red">
    <button type="button"  class="btn submit order" style="color:#0000FF" id="submitCreateOrder" > 提交</button>
  </p>
  <a href="/select">进入订单查询页面：</a>
</div>
</div>

<div id = "showcreateorderinfo">
  <p>创建订单信息：</p>
  <table class="orderInfotable" id = 'resptableResult'>
    <thead id="itemTableData">
    <tr>
      <th>订单号</th>
      <th>订单状态</th>
      <th>订单日志</th>
    </tr>
    </thead>
    <tbody id="tableBody">

    </tbody>

  </table>
</div>

<script type='text/javascript'>

  //获取当前项目的路径
//  var urlRootContext = (function () {
//    var strPath = window.document.location.pathname;
//    var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
//    return postPath;
//  })();

  function fastGenerateOrderData(sku ,pid, orderQuentity , payType , addressID , uid , orderType ) {
//    var orderSku = $("productSku").val();
//    var orderPid = $("productPid").val();
//    var orderQuentity = $("productQuantity").val();
    var strPath = window.document.location.pathname;
    var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
    var url =  postPath + "/fastCreateOrderRequest.do"; //请求的网址
    var reqParams = {
      'sku':sku,
      'pid':pid,
      'orderQuentity':orderQuentity,
      'payType': payType ,
      'addressID': addressID,
      'uid': uid ,
      'orderType':orderType};//请求数据

    $.ajax({
      type:"GET",
      url: url,
      data:reqParams,
      async:false,
      dataType:"json",
      success:function(data){
        $("#tableBody").empty();//清空表格内容
        if (data != null ) {
          $("#tableBody").append('<tr>');
          $("#tableBody").append('<td>' + data.orderID + '</td>');
          $("#tableBody").append('<td>' + data.orderStatus + '</td>');
          $("#tableBody").append('<td>' + data.orderLogs + '</td>');
          $("#tableBody").append('</tr>');

        } else {
          $("#tableBody").append('<tr><th colspan ="4"><center>查询无数据</center></th></tr>');
        }
        $("#showcreateorderinfo").show();
      }
    });
  }
  //渲染完就执行
  $(function() {

    $("#submitCreateOrder").bind("click",
            function(){
//            var orderURI = "http://item.gome.com.cn/9133725033-1122470667.html";
              var orderSku = $("#productSku").val();
              var orderPid = $("#productPid").val();
              var orderQuentity = $("#productQuantity").val();
      var payType = 0;
      var addressID = 2;
      var uid = "222266";
      var orderType = 0;
      fastGenerateOrderData(orderSku , orderPid,orderQuentity , payType , addressID , uid , orderType);
    });
    $("#showcreateorderinfo").hide();
  });
</script>

</body>
</html>
