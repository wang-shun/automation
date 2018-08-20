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
    <div id="main-panel_1">

        <!--api 表单-->
        <div class="tab-content" id="main-panel-data">

            <!--end api 表单-->
            <!--api_table_div 表单-->
            <div class="tab-content" id="api-panel-data">
                <div class="tab-pane active" id="-panel">
                    <div id="host_header_div" class="half-color-header filter_div" style="padding: 20px; margin-top: 30px;">

                        <table>
                            <tbody>
                            <tr>
                                <td>
                                    <div style="margin-left: 20px;" id="div_q_name" class="hidden">
                                        <span id="spq_type"></span> 队列名称 ：  ${mq_qname}
                                    </div>
                                </td>
                                <td>
                                    <a  id="btnAdd" class="btn btn-primary hidden" onclick="javascript:edit(null);">新增Message</a>
                                </td>
                                <td>

                                </td>
                                <td><a  id="btndelete" class="btn btn-primary hidden" onclick="javascript:deleteMessage(null);">消费Message</a></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>


                    <%--${mq_hostname} --> ${mq_qmgname} -->--%>

                    <div id="mq_table_div" class="half-trans-green table-div" style="display: inline;">
                        <table id="mq_list" class="table table-striped table-bordered table-hover half-trans-green">
                            <thead>
                            <tr>
                                <th style="width: 15%;">放入日期/时间</th>
                                <th style="width: 15%;">UserId</th>
                                <th style="width: 60%;">Message</th>
                                <th>操作</th>
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

    <!-- modal for status -->
    <div class="modal hide fade" id="modal-add-status" role="dialog"
         aria-hidden="true">
        <div class="modal-body">
            <div id="status-add">
                请确认是否加入队列
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true" onclick="addMessage()">
                <span>确认</span>
            </button>
            <button class="btn" data-dismiss="modal" aria-hidden="true" >
                <span>关闭</span>
            </button>
        </div>
    </div>



    <div class="modal fade" id="mq_detail_modal_mess" tabindex="-1" role="dialog"
         aria-labelledby="hosts_sending" aria-hidden="false"  data-keyboard="false" data-backdrop="static">
        <div class="modal-dialog" >
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close"
                            data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title" id="mq_detail_mess">
                        删除成功
                    </h4>
                </div>
                <div id="mq_detail_body_mess" class="modal-body" style="text-align: left">
                    <table id="hosts_detail_table_mess" class="table table-bordered">
                        <tbody>
                        <tr>
                            <th style="width: 20%;">Host</th>
                            <td><span class="hosts_detail_table_span" >${mq_hostname}</span></td>
                        </tr>
                        <tr>
                            <th>队列管理器</th>
                            <td><span class="hosts_detail_table_span" >${mq_qmgname}</span></td>
                        </tr>
                        <tr>
                            <th>队列名称</th>
                            <td><span class="hosts_detail_table_span" >${mq_qname}</span></td>
                        </tr>
                        <tr>
                            <td colspan="2">

                            <textarea class="form-control" defaultval="请输入内容" rows="12" id="usemessageText"
                                      style="width: 90%" ></textarea>
                            </td>
                        </tr>

                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal -->
    </div>

    <div class="modal fade" id="mq_detail_modal" tabindex="-1" role="dialog"
         aria-labelledby="hosts_sending" aria-hidden="false"  data-keyboard="false" data-backdrop="static">

        <div class="modal-dialog"  >
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
                <div id="mq_detail_body" class="modal-body" style="text-align: left">
                    <table id="hosts_detail_table" class="table table-bordered">
                        <tbody>
                        <tr>
                            <th style="width: 20%;">Host</th>
                            <td><span class="hosts_detail_table_span" >${mq_hostname}</span></td>
                        </tr>
                        <tr>
                            <th>队列管理器</th>
                            <td><span class="hosts_detail_table_span" >${mq_qmgname}</span></td>
                        </tr>
                        <tr>
                            <th>队列名称</th>
                            <td><span class="hosts_detail_table_span" >${mq_qname}</span></td>
                        </tr>
                        <tr>

                            <td colspan="2">

                            <textarea class="form-control" defaultval="请输入内容" rows="12" id="messageText"
                                      style="width: 100%" ></textarea>
                                <%--<a href="#iconi"><i id="iconi" class="icon-pencil"></i></a>--%>
                            </td>
                        </tr>

                        <tr>
                            <td colspan="2" >
                                <button class="btn btn-primary" id="btn-save-q" style="width: 100px;" >
                                加入队列
                            </button>
                                <button class="btn btn-primary" id="btn-format" style="width: 100px;"
                                        aria-hidden="true">
                                    格式化
                                </button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal -->
    </div>


    <div class="modal hide" id="modal-editor" role="dialog"
         aria-hidden="false" data-keyboard="false" data-backdrop="static">
        <div class="modal-header">
            <div class="row" style="margin-left: 0px">
                <h4 class="pull-left">编辑</h4>

                <div class="pull-right" style="margin-top: 10px;">
                    <input value="text" type="radio" name="editor-manner"
                           style="margin-top: 0px;" checked>
                    <span>text</span>
                    <span>&nbsp;</span>
                    <%--<input value="json" type="radio" name="editor-manner"--%>
                           <%--style="margin-top: 0px;">--%>
                    <%--<span>json</span>--%>
                </div>
            </div>
        </div>
        <div class="modal-body">
            <div id="editor-container">
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">
                取消
            </button>
            <button class="btn btn-primary" id="btn-save-text"
                    data-dismiss="modal" aria-hidden="true">
                保存
            </button>
           <button class="btn btn-primary" id="btn-format-text"
                              aria-hidden="true">
            格式化
        </button>
        </div>

    </div>

    <input type="hidden" id="hiddenValue" title="${title}" mq_qname="${mq_qname}" mq_hostname="${mq_hostname}" mq_qmgname="${mq_qmgname}" mq_channel="${mq_channel}" mq_ccsid="${mq_ccsid}" mq_port="${mq_port}" mq_pid="${mq_pid}" mq_type="${mq_type}" mq_iscmd="${mq_iscmd}" />


</div>


<!-- jquery -->
<script type="text/javascript" src="/assets/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/assets/jquery/jquery-ui-1.10.4.custom.min.js"></script>
<script type="text/javascript" src="/assets/datatables/js/jquery.dataTables.min.js"></script>
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
<script type="text/javascript" src="/js/mqs.js"></script>



</body>
</html>





