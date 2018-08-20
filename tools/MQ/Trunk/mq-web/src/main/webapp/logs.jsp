<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
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
    <link rel="stylesheet" href="/assets/datatables/css/jquery.dataTables.min.css" />
    <link  href="/assets/jquery-datepicker/jquery.datetimepicker.css" rel="stylesheet" type="text/css"/>
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
                        <a class="brand" href="/home">GTP-Mock-MQ</a>
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
<!-- main container -->
<div id="main-container">


    <!-- sidebar -->
    <!-- main panel -->
    <div id="main-panel_1" style="margin-top: 40px">

        <!--api 表单-->
        <div class="tab-content" id="main-panel-data">

            <!--end api 表单-->
            <!--api_table_div 表单-->
            <div class="tab-content" id="api-panel-data">
                <div class="tab-pane active" id="-panel">
                    <div id="host_header_div" class="half-color-header filter_div " style="width: 95%;">
                        <table class="table table-bordered">

                            <tbody>
                            <tr>
                                <td align="left" style="width: 30%" colspan="3">
                                    <div class="input-group">
                                        <span class="input-group-addon " style="text-align: left;">模 板</span>
                                        <select class="report_filter form-control form-inline" id="log_template" style="width: 500px;">
                                        </select>
                                        <span style=""></span>
                                    </div>
                                </td>


                            </tr>
                            <tr>
                                <td align="left"  style="width:30%" >
                                    <div id="log_begin_div" class="input-group ">
                                        <span id="log_startDate" class="input-group-addon">开 始</span>
                                        <input id="log_timespan_begin_input" class="form-inline form-control"/>
                                        <span id="log_startDate_addon" class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                                    </div>
                                </td>
                                <td align="left" style="width:30%; white-space: nowrap;" >
                                    <div id="log_end_div" class="input-group">
                                        <span id="log_endDate" class="input-group-addon">结 束</span>
                                        <input id="log_timespan_end_input" class="form-inline form-control"/>
                                        <span id="log_endDate_addon" class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                                    </div>

                                </td>
                                <td>
                                    <a id="log_generate_btn" class="btn btn-primary">查询</a>
                                </td>

                                </tr>

                            </tbody>
                        </table>

                    </div>


                </div>

                <div id="log_table_div" class="half-trans-green table-div" style="display: inline;">
                    <table id="log_list" class="table table-striped table-bordered table-hover half-trans-green hidden">
                        <thead>
                        <tr>
                            <th style="width:10%;">时间</th>
                            <th style="width:10%;">host</th>
                            <th style="width:10%;">队列名称</th>
                            <th style="width:10%;">模板名称</th>
                            <th style="width:10%;">操作类型</th>
                            <th style="width:50%;">消息内容</th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>

    </div>

</div>

    <!-- modal for status -->
    <div class="modal hide fade" id="modal-status" role="dialog"
         aria-hidden="true">
        <div class="modal-body">
            <div id="status">
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">
                <span>关闭</span>
            </button>
        </div>
    </div>

    <!-- modal for status -->
    <div class="modal hide fade" id="modal-delete-status" role="dialog"
         aria-hidden="true">
        <div class="modal-body">
            <div id="status-delete">
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true" onclick="btnDelete()">
                <span>确认</span>
            </button>
            <button class="btn" data-dismiss="modal" aria-hidden="true">
                <span>关闭</span>
            </button>
        </div>
    </div>

    <div class="modal fade" id="log_detail_modal" tabindex="-1" role="dialog"
         aria-labelledby="hosts_sending" aria-hidden="false" data-keyboard="false" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="mq_detail">
                    详细内容
                </h4>
            </div>
            <div id="log_detail_body" class="modal-body" style="text-align: left">
                <table id="hosts_detail_table" class="table table-bordered">
                    <tbody>
                    <tr>
                        <th>时间</th>
                        <td><span class="hosts_detail_table_span" id="spTime"></span></td>
                    </tr>
                    <tr>
                        <th>操作类型</th>
                        <td><span class="hosts_detail_table_span" id="spuseType"></span></td>
                    </tr>
                    <tr>
                        <th style="width: 20%;">Host</th>
                        <td><span class="hosts_detail_table_span" id="spHost"></span></td>
                    </tr>

                    <tr>
                        <th>队列管理器</th>
                        <td><span class="hosts_detail_table_span" id="spJndi"></span></td>
                    </tr>
                    <tr>
                        <th>队列名称</th>
                        <td><span class="hosts_detail_table_span" id="spqName"></span></td>
                    </tr>
                    <tr>
                        <th>IP</th>
                        <td><span class="hosts_detail_table_span" id="spIP"></span></td>
                    </tr>
                    <tr>
                        <th>模板</th>
                        <td><span class="hosts_detail_table_span" id="spTemplate"></span></td>
                    </tr>
                    <tr>
                        <td colspan="2">

                            <textarea class="form-control" defaultval="请输入内容" rows="12" id="messageText"
                                      style="width: 100%"></textarea>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    </div>

</div>

<input type="hidden" id="hiddenValue" port="${port}" templatepart="${templatepart}" channel="${channel}" qmgName="${qmgName}" host="${host}" />
<!-- jquery -->
<script type="text/javascript" src="/assets/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/assets/jquery/jquery-ui-1.10.4.custom.min.js"></script>
<script type="text/javascript" src="/assets/datatables/js/jquery.dataTables.min.js"></script>
<script src="/assets/jquery-datepicker/jquery.datetimepicker.js" type="text/javascript"></script>
<!-- jsoneditor -->
<!-- bignumber -->
<script type="text/javascript" src="/assets/jsoneditor/lib/bignumber/bignumber.min.js">
</script>
<!-- jsoneditor -->
<script type="text/javascript" src="/assets/jsoneditor/jsoneditor-min.js">
</script>
<!-- ace code editor -->
<script type="text/javascript" src="/assets/jsoneditor/lib/ace/ace-min.js" charset="utf-8">
</script>
<!-- json lint -->
<script type="text/javascript"
        src="/assets/jsoneditor/lib/jsonlint/jsonlint.js">
</script>
<!-- bootstrap -->
<script type="text/javascript" src="/assets/bootstrap/js/bootstrap.min.js">
</script>
<script type="text/javascript"
        src="/assets/bootstrap-modal/js/bootstrap-modalmanager.js">
</script>
<script type="text/javascript"
        src="/assets/bootstrap-modal/js/bootstrap-modal.js">
</script>

<script type="text/javascript" src="/js/utils.js"></script>
<script type="text/javascript" src="/js/logs.js"></script>



</body>
</html>
