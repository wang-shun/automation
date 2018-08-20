<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
<head>

    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="基于关键字、数据驱动的SpringMVC测试框架编辑器">
    <title>WebIDE</title>
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
        <!--api 表单-->
        <div class="tab-content" id="main-panel-data">
            <div class="tab-pane active" id="editor-panel">


                <!-- SetUp Section -->
                <div class="collapse" id="setup-section" style="display: inline;">
                    <div class="row" style="margin-left: 0px; margin-top: 10px;">
                        <div style="display: inline-block">
                            <h4 class="pull-left" style="margin-top: 4px;">服务名称</h4>

                            <span class="api_detail_name span3 input-text-light" id="api_detail_name" style="margin-left: 20px; margin-top: -6px;display:inline;width: 290px;">${api_nameStr}</span>
                            <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0); " id="a_details" style="margin-left: 10px;"><span  aria-hidden="true">details</span></a>

                        </div>

                    </div>

                    <div class="row" style="margin-left: 0px; margin-top: 10px;">
                        <div style="display: inline-block">
                            <h4 class="pull-left" style="margin-top: 4px;">SetUp</h4>
                            <input class="span3 input-text-light"
                                   id="input-step-class-name"
                                   type="text"
                                   placeholder="Steps类名"
                                   value="com.gome.test.mock.dao.ApiDao"
                                   oldvalue="com.gome.test.mock.dao.ApiDao"
                                   style="margin-left: 20px; margin-top: -6px;display:inline;width: 290px;">
                        </div>
                        <div class="dropdown" style="display:inline-block">
                            <h4 class="pull-left" style="margin-top: 4px; ">Method</h4>
                            <input id="setup_search" type="text" class="span5 input-text-light"
                                   style="margin-left: 20px; margin-top: -6px;display:inline;width: 250px;" data-provide="typeahead"/>
                            <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0); " style="margin-left: 10px;"><span class="icon-question-sign" aria-hidden="true"> </span></a>

                            <ul class="dropdown-menu" id="menu-setup" style="left:310px;top:-1px">
                                <li class="hide" id="setup-menu-template">
                                    <a href="javascript:void(0);"
                                       data-content="template menu description"
                                       rel="popover" data-original-title="Description"
                                       data-placement="left" data-container="body">menu</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <ul id="setSteps" class="ui-sortable">
                        <li class="hide" id="setup-step-template">
                            <span class="icon-th"></span>
                            <span class="badge">0</span>
                            <input type="text" value="" class="input-text-light"
                                   placeholder="method" data-keyword>

                            <div class="input-append">
                                <input type="text" value="" class="input-text-light"
                                       placeholder="" data-arg>
                 <span class="add-on-inline dropdown-toggle"
                       data-toggle="dropdown" style="margin-right: 5px;">
                      <i class="icon-pencil"></i>
                    </span>
                            </div>
                  <span class="icon-remove"
                        data-placement="right"
                        data-toggle="tooltip"
                        data-trigger="hover"
                        data-original-title="remove step">
                  </span>

                            <div class="hide help" contenteditable="true" data-doc></div>
                        </li>
                    </ul>

                </div>
                <div class="row" style="margin-left: 20px" id="btn-div">
                    <div class="pull-left">
                        <button class="btn btn-primary" id="btn-save-api" apiid="${api_id}" >
                            保存
                        </button>
                        <button class="btn btn-info" id="btn-send-api" onclick="window.history.back(-1)">
                            放弃
                        </button>

                    </div>
                </div>
                <hr/>

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
    </div>

    <!-- modal for text/json/xml editor -->
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
                    <input value="json" type="radio" name="editor-manner"
                           style="margin-top: 0px;">
                    <span>json</span>
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
        </div>
    </div>

    <div class="modal fade" id="api_detail_modal" tabindex="-1" role="dialog"
         aria-labelledby="hosts_sending" aria-hidden="false" data-keyboard="false" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close"
                            data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title" id="hosts_detail">
                        api 详细内容
                    </h4>
                </div>
                <div id="api_detail_body" class="modal-body" style="text-align: left">
                    <table id="api_detail_table" class="table table-bordered">
                        <tbody>
                        <tr>
                            <th>ID</th>
                            <td><span class="hosts_detail_table_span" id="api_detail_id">${api_id}</span></td>
                        </tr>
                        <tr>
                            <th>服务名称</th>
                            <td><span class="hosts_detail_table_span" >${api_name}</span></td>
                        </tr>
                        <tr>
                            <th>HOST_名称</th>
                            <td><span class="hosts_detail_table_span"  >${api_host_name}</span></td>
                        </tr>
                        <tr>
                            <th>HOST_域名</th>
                            <td><span class="hosts_detail_table_span" id="api_detail_host_domain">${api_host_domain}</span></td>
                        </tr>
                        <tr>
                            <th>HOST_地址</th>
                            <td><span class="hosts_detail_table_span" id="api_detail_host_url">${api_host_url}</span></td>
                        </tr>
                        <tr>
                            <th>关键字</th>
                            <td><span class="hosts_detail_table_span" id="api_detail_keyWords">${api_keywords}</span></td>
                        </tr>
                        <tr>
                            <th>描述</th>
                            <td><span class="hosts_detail_table_span" id="api_detail_descript">${api_descript}</span></td>
                        </tr>

                        <tr>
                            <th>是否有效</th>
                            <td><span class="hosts_detail_table_span" id="api_detail_enable">${api_enable}</span></td>
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

<span type="hidden" id="sp_hidden_flow" class="hidden" >${api_flowcontent}</span>
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
    <!-- -->
    <script type="text/javascript" src="/assets/jqTree-0.20.0/tree.jquery.js">
    </script>

    <script type="text/javascript" src="/assets/zeroclipboard-1.3.5/ZeroClipboard.min.js">
    </script>

    <script type="text/javascript" src="/js/utils.js"></script>
    <script type="text/javascript" src="/js/workFlowModify.js"></script>
    <script type="text/javascript" src="/assets/bootstrap/js/bootstrap-typeahead.js"></script>
    <script type="text/javascript" src="/assets/bootstrap/js/underscore.js"></script>
</body>
</html>
