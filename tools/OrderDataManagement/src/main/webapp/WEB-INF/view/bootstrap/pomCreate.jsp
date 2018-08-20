<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html lang="en">
<head>
<%--<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />--%>
<title>测试订单管理系统</title>
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
    <script type='text/javascript'>
        function showhidediv(id){

            var type_3pp=document.getElementById("msg_2");

            var select_3pp=document.getElementById("msg_1");

            if (id == 'select_3pp') {

                if (select_3pp.style.display=='none') {

                    type_3pp.style.display='none';

                    select_3pp.style.display='block';

                }

            } else {

                if (type_3pp.style.display=='none') {

                    select_3pp.style.display='none';

                    type_3pp.style.display='block';

                }

            }

        }
    </script>
<style type="text/css">
        a
        {
            cursor:pointer;
        }
        a:hover
        {
            background:#5eb95e;
            color:#00F
        }


        #createOpt{
            margin-right: auto;
            margin-left: auto;
            width:800px;
            color: blue;
        }



        #showcreateorderinfo {
            margin-right: auto;
            margin-left: auto;
            width:800px;
        }
        #filterDiv {
            margin-right: auto;
            margin-left: auto;
            width:800px;
        }
        #textInput {
            margin-top: 10px;
        }
        #tableResult {
            margin-right: auto;
            margin-left: auto;
            width:900px;
        }
        .diffInput{
            width:399px;
            display:block;
            margin-bottom:10px;
        }
        /*#tableResult2  {*/
            /*border-collapse: collapse;*/
            /*width: 100%;*/
        /*}*/

        /*#th2, #td2 {*/
            /*text-align: left;*/
            /*padding: 8px;*/
        /*}*/

        /*tr:nth-child(even){background-color: #f2f2f2}*/


        /*table {*/
            /*border-collapse: collapse;*/
            /*width: 100%;*/
        /*}*/

        /*th, td {*/
            /*text-align: left;*/
            /*padding: 8px;*/
        /*}*/

        /*tr:nth-child(even){background-color: #f2f2f2}*/

        /*th {*/
            /*background-color: #4CAF50;*/
            /*color: white;*/
        /*}*/
        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            text-align: left;
            padding: 8px;
        }

        tr:nth-child(even){background-color: #f2f2f2}
    </style>
</head>


<body text="#F0F0F0">
 <p>&nbsp;</p>
 <p>
   <input name="imageField" type="image" id="imageField" src="../../static/img/ordermanagement.png" align="middle" />
 </p>


 <div id = "createOpt">
     <a href="/fastCreate">快速输入创建订单链接</a>

     <p style="color:red">请选使用的环境：
         <label for="label2"></label>
         <select name="select5" id="selectENV"  style="color:#000000">
             <option value="uat">UAT</option>
             <option value="pre">PRE</option>
             <option value="sit">SIT</option>
         </select>
     </p>
 <p><font color="black">请输入要选择的商品类型：</font> 
 </p>
   
 <!-- <p style="color:red" >sim</p> -->
<p style="color:#FF0000">
  <input name="sim select" type="radio"  value=""/> SIM
</p>


<form id="form1" name="form1" method="post" action="" />
<div id="div3ppselect" >
  <p  style="color:#FF0000" onclick='showhidediv("3pp")'>
  <input type="radio" id="rb3pp" name="radiobutton" value="radiobutton" /> 3PP
  </p>

<div id="msg_1" style="display:block;float:left;" onclick='showhidediv("ddd")';>
    <p id="photoTitle" style="color:blue"  >展开选项</p>
</div>



    <div id="msg_2" style="display:none;float:left;" >
    <p style="color:black">

        <table class="tablesorter" id = 'tableResult'>
            <p>&nbsp; </p>
        <thead>
        <tr style="background-color: #e6bf99">
            <th>选择</th>
            <th>商品URI</th>
            <th>商品数量</th>
        </tr>
        </thead>
           <tr >
               <th><input type="checkbox" name="checkbox" value="checkbox" id="checkbox" /></th>
               <th>http://item.gome.com.cn/9133725033-1122470667.html?intcmp=electronic-1000055475-2</th>
               <th><input type="text" name="textfield2" id = "input3pp1" /></th>
           </tr>

        <tr>
            <th><input type="checkbox" name="checkbox" value="checkbox" id="checkbox2" /></th>
            <th>http://item.gome.com.cn/9133725033-1122470667.html</th>
            <th><input type="text" name="textfield2" id = "input3pp13" /></th>
        </tr>
            </tbody>
        </table>


      <%--<input type="checkbox" name="checkbox" value="checkbox" id="checkbox" />--%>
      <%--<label for="checkbox"></label>--%>


      <%--<label >商品链接：</label>--%>
      <%--<input type="hidden" name="hiddenField">--%>
      <%--<font color="#000000">http://item.gome.com.cn/9133725033-1122470667.html?intcmp=electronic-1000055475-2</font></input>--%>
      <%--:数量--%>
      <%--<label for="label"></label>--%>
      <%--<input type="text" name="textfield2" id = "input3pp1" />--%>
    <%--</p>--%>

    <%--<p style="color:black">--%>
      <%--<input type="checkbox" name="checkbox" value="checkbox" id="input3pp2" />--%>
      <%--<label for="checkbox"></label>--%>
      <%--<label >商品链接：</label>--%>

      <%--<input type="hidden" name="hiddenField">--%>
      <%--<font color="#000000">http://item.gome.com.cn/9133725033-1122470666.html</font></input>--%>
      <%--:数量--%>
      <%--<label for="label"></label>--%>
      <%--<input type="text" name="textfield2" id="label" />--%>
    <%--</p>--%>

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
    </div>

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
<!--  
</form>

<div id="oterTypeProduct">
<form id="form2" name="form2" method="post" action="">
  <p style="color:red">
  <input type="radio" name="radiobutton" value="radiobutton" onclick='showhidediv("select_g3pp")'; />
    g3pp
	</p>
</form>

<form id="form3" name="form3" method="post" action="">
  <p style="color:red">
  <input type="radio" name="radiobutton" value="radiobutton" />
    pop
</p>
</div>

-->
 <script type='text/javascript'>



     function generateOrderData(orderURI , orderQuentity , payType , addressID , uid , orderType ) {
         var strPath = window.document.location.pathname;
         var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
        var url =  postPath + "/createOrderRequest.do"; //请求的网址
       var reqParams = { 'orderURI':orderURI,
           'orderQuentity':orderQuentity,
           'payType': payType ,
           'addressID': addressID,
           'uid': uid ,
           'orderType':orderType};//请求数据

        $.ajax({
            type:"POST",
            url: url,
            data:reqParams,
            async:false,
            dataType:"json",
            success:function(data){
                //ajx can not redirect page .if do this using form .
//                $("#subViewDiv").html( data );
//                window.location='/select'
//                location.reload();
                <!-- show create status. -->
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
        //获取当前项目的路径
//        var urlRootContext = (function () {
//            var strPath = window.document.location.pathname;
//            var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
//            return postPath;
//        })();

        $("#submitCreateOrder").bind("click",function(){
//            var orderURI = "http://item.gome.com.cn/9133725033-1122470667.html";
            var orderURI = "9133725033-1122470667";
//            $('#checkbox').is(':checked')
            var orderQuentity = 1;
            var payType = 0;
            var addressID = 2;
            var uid = "222266";
            var orderType = 0;
//            todo get request from element
            generateOrderData(orderURI , orderQuentity , payType , addressID , uid , orderType);
        });
        $("#showcreateorderinfo").hide();
    });
    </script>
</body>
</html>
