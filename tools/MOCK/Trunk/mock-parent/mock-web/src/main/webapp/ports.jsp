<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
<head>

    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="基于关键字、数据驱动的SpringMVC测试框架编辑器">
    <title>MockIDE</title>
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
            <button type="button" class="btn btn-navbar" data-toggle="collapse"
                    data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="javascript:void(0);">MockIDE</a>

            <div class="nav-collapse collapse">
                <ul class="nav">

                </ul>
            </div>
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
            <div class="tab-pane active" id="editor-panel">
                <!-- caseid -->

                <!--api section-->


                <hr/>

            </div>
            <!--end api 表单-->
            <!--api_table_div 表单-->
            <div class="tab-content" id="api-panel-data">
                <div class="tab-pane active" id="-panel">
                    <div id="host_header_div" class="half-color-header filter_div" style="padding: 20px">

                        <table>
                            <tbody>
                            <tr>
                                <td>
                                  <button type="button"  class="btn btn-primary" onclick="portAdd()"   >新增Port</button>

                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="table_div" class="half-trans-green table-div" style="display: inline;">
                        <table id="port_list" class="table table-striped table-bordered table-hover half-trans-green">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>端口号</th>
                                <th>是否有效</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                        </table>
                    </div>

                </div>
            </div>
            <!--end api_table_div 表单-->

        </div>

    </div>

    <div class="modal fade" id="sending_modal" tabindex="-1" role="dialog"
         aria-labelledby="hosts_sending" aria-hidden="false" data-keyboard="false" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header" style="text-align: center">
                    <button type="button" class="close"
                            data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title" id="hosts_sending" >
                        请等待
                    </h4>
                </div>
                <div id="hosts_sending_body" class="modal-body" style="text-align: center">
                    <span>正在查询......</span>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal -->
    </div>

    <div class="modal fade" id="port_detail_modal" tabindex="-1" role="dialog"
         aria-labelledby="hosts_sending" aria-hidden="false" data-keyboard="false" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close"
                            data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title" id="hosts_detail">
                        Port 详细内容
                    </h4>
                </div>
                <div id="port_detail_body" class="modal-body" style="text-align: left">
                    <table id="ports_detail_table" class="table table-bordered">
                        <tbody>
                        <tr>
                            <th>ID</th>
                            <td><span class="hosts_detail_table_span" id="port_detail_id"></span></td>
                        </tr>
                        <tr>
                            <th> 端口 </th>
                            <td><span class="hosts_detail_table_span" id="port_detail_number"></span></td>
                        </tr>
                        <tr>
                            <th>是否有效</th>
                            <td><span class="hosts_detail_table_span" id="port_detail_enable"></span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal -->
    </div>
    <div class="modal fade" id="port_modify_modal" tabindex="-1" role="dialog"
         aria-labelledby="hosts_sending" aria-hidden="false" data-keyboard="false" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close"
                            data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title" id="ports_modify_detail">
                        Port 内容
                    </h4>
                </div>
                <div id="port_modify_detail_body" class="modal-body" style="text-align: left">
                    <ul id="port-table" class="ui-sortable">
                        <li >
                            <span class="icon-th"></span>
                            <span class="badge">01</span>
                            <span>端口号</span>
                            <div class="input-append">
                                <input  id="port_modify_detail_number"  type="text" value="${port_number}" class="input-text-light"
                                       placeholder="" oldvalue="${port_number}" data-arg>

                            </div>
                    <span class="dropdown-toggle" data-toggle="dropdown" style="margin-left: 15px;">
                      是否有效：
                    </span>
                            <div class="input-append">
                                <input type="checkbox"  id="checkbox_port_enable" oldvalue="${enable}" />
                            </div>
                        </li>
                     </ul>

                </div>
                <div class="modal-footer">
                    <button type="button" id="btn_port_modify" class="btn btn-default" port_id="" port_type="" >保存</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">放弃</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal -->
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
    <div class="modal hide fade" id="result_modal-status" role="dialog"
         aria-hidden="true">
        <div class="modal-body">
            <div id="result-status">删除成功！
            </div>
        </div>
        <div class="modal-footer">
            <button id="deleteClose" class="btn" data-dismiss="modal" aria-hidden="true">
                <span>关闭</span>
            </button>
        </div>
    </div>
</div>
<input type="hidden" id="hidden" value="${view_port_id}" />
<!-- jquery -->
<script type="text/javascript" src="/assets/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/assets/jquery/jquery-ui-1.10.4.custom.min.js"></script>
<!-- bootstrap -->
<script type="text/javascript" src="/assets/bootstrap/js/bootstrap.min.js">
</script>
<script type="text/javascript"
        src="/assets/bootstrap-modal/js/bootstrap-modalmanager.js">
</script>
<script type="text/javascript"
        src="/assets/bootstrap-modal/js/bootstrap-modal.js">
</script>
<script type="text/javascript"
        src="/assets/jquery-plugin-placeholder/placeholder.js">
</script>
<!-- perfect-scrollbar -->
<script type="text/javascript"
        src="/assets/perfect-scrollbar/perfect-scrollbar.min.js">
</script>


<script type="text/javascript" src="/assets/datatables/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="/js/utils.js"></script>
<script type="text/javascript" src="/js/ports.js"></script>
<script type="text/javascript" src="/assets/bootstrap/js/bootstrap-typeahead.js"></script>
<script type="text/javascript" src="/assets/bootstrap/js/underscore.js"></script>
</body>
</html>
