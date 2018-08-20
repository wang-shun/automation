<%--
  Created by IntelliJ IDEA.
  User: hacke
  Date: 2016/9/23
  Time: 14:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<html>
<head>
  <title>测试订单管理系统 查询订单</title>
  <link href="<%=request.getContextPath()%>/static/js/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <script src="<%=request.getContextPath()%>/static/js/jQuery/jquery-2.1.4.min.js"></script>
  <script src="<%=request.getContextPath()%>/static/js/bootstrap/js/bootstrap.min.js"></script>
  <script src="<%=request.getContextPath()%>/static/js/bootstrap/js/bootstrap-paginator.min.js"></script>
  <!-- jQuery: required (tablesorter works with jQuery 1.2.3+) -->
  <script src="<%=request.getContextPath()%>/static/js/jtableSorter/jquery.tablesorter.min.js"></script>
  <!-- Pick a theme, load the plugin & initialize plugin -->
  <link href="<%=request.getContextPath()%>/static/css/theme.default.min.css" rel="stylesheet">
  <%--<link href="<%=request.getContextPath()%>/static/css/jq.css" rel="stylesheet">--%>
  <script src="<%=request.getContextPath()%>/static/js/jtableSorter/jquery.tablesorter.widgets.min.js"></script>
  <script src="<%=request.getContextPath()%>/static/js/flot/jquery.flot.js"></script>


  <!-- JQuery, Bootstrap and Custom CSS files -->
  <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/jquery-ui.css" type="text/css" media="all">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/bootstrap.css" type="text/css" media="all">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/piechartbase.css" type="text/css" media="all">

  <script src="<%=request.getContextPath()%>/static/js/jQuery/jquery-ui.js"></script>
  <script src="<%=request.getContextPath()%>/static/js/flot/jquery.flot.pie.js"></script>

  <!-- This is the main javascript that defines the pie chart and its attributes -->
  <script  src="<%=request.getContextPath()%>/static/js/bootstrap/js/diffpiechart.js"></script>



  <style type="text/css">
    #selectOpt{
      margin-right: auto;
      margin-left: auto;
      width:800px;
      color: blue;
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
    #tableResult {
      margin-right: auto;
      margin-left: auto;
      width:900px;
    }
  </style>


</head>


<body text="#F0F0F0">
<p>
  <input name="" type="image" src="../../static/img/order-management-system-integration.png" align="middle" />
</p>

<div id="selectOpt">
  <p style="color:red">请选使用的环境：
    <select name="select5" id="selectENV"  style="color:#000000">
      <option value="uat">UAT</option>
      <option value="pre">PRE</option>
      <option value="sit">SIT</option>
    </select>
  </p>
<p style="color:#0033FF">&nbsp; 请选择商品类型 </p>
<p>
  <input name="" type="checkbox" style="color:#FF0000" value="sim" id="checkbox_sim" >
  <font color="#FF0000">SIM</font>
  </input>
</p>

<p>
  <input name="" type="checkbox" style="color:#FF0000" value="3pp" id="checkbox_3pp" >
  <font color="#FF0000">3PP</font>
  </input>

</p>
<p>
  <input name="" type="checkbox" style="color:#FF0000" value="g3pp" id="checkbox_g3pp" >
  <font color="#FF0000">G3PP</font>
  </input>
</p>
<p style="color:#0000FF"> 是否筛选区域：
  <label>
    <select name="select" id="select_dist">
      <option value="bj3">北京朝阳区三环内</option>
      <option value="sh3">上海浦东区三环内</option>
      <option value="sd">山东济南石头街</option>
    </select>
  </label>
</p>
<p style="color:#0000FF">是否筛选支付方式：
  <label>
    <select name="select2" id="select_payment">
      <option value="gomepay">国美支付</option>
      <option value="unpay">银联快捷支付</option>
      <option value="weichatpay">微信支付</option>
      <option value="delaypay">货到付款</option>
    </select>
  </label>
</p>
<p style="color:#0000FF">是否筛选订单状态：
  <label>
    <select name="select3" id="select_orderStatus">
      <option value="pp">PP</option>
      <option value="dl">DL</option>
    </select>
  </label>
</p>
<p style="color:#0000FF">&nbsp;</p>
  <p style="color:#000000">输入订单号查询： <input name="inputoid" type="text" id="inputorderid" >
  </p>
  <label style="color:#000000">
    <button type="button"  class="btn submit order" style="color:#0000FF" id="selectOrder" > 查询</button>
  </label>
  <a href="/create">返回创建订单页面：</a>
</div>
<p style="color:#0000FF">&nbsp;</p>
<p style="color:#0000FF">&nbsp; </p>

<table class="orderInfotable" id = 'tableResult'>
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
<script type='text/javascript'>
  function selectOrderData(systemENV,orderID, payType , addressID , uid , orderStatus ) {
    var strPath = window.document.location.pathname;
    var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
    var url =  postPath + "/selectOrderInfo"; //请求的网址
    var reqParams = {
      'systemENV':systemENV,
      'orderID':orderID,
      'payType': payType ,
      'addressID': addressID,
      'uid': uid ,
      'orderStatus':orderStatus};//请求数据

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
        $("#tableResult").show();
      }
    });
  }
  //渲染完就执行
  $(function() {
    $("#selectOrder").bind("click",function(){
//            var orderURI = "http://item.gome.com.cn/9133725033-1122470667.html";
      var systemENV = $("#selectENV").val();
      var orderid = $("#inputorderid").val();
      var payType =  $("#select_payment").val();
      var addressID =  $("#select_dist").val();
      var uid = "222266";
      var orderStatus =  $("#select_orderStatus").val();
//            todo get request from element
      selectOrderData(systemENV, orderid , payType , addressID , uid , orderStatus);
    });
    $("#tableResult").hide();
  });
</script>

</body>
</html>
