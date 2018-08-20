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
                <!-- caseid -->

                <!--api section-->
                <div class="collapse" id="api-section" style="display: inline;">
                    <div class="row" style="margin-left: 0px; margin-top: 4px">

                    </div>
                    <ul id="api-table" class="ui-sortable">
                        <li >
                            <span class="icon-th"></span>
                            <span class="badge">01</span>
                            <span>服务名称</span>
                            <div class="input-append">
                                <input style="margin-left: 10px;"  id="txt_api_name"  type="text" value="" class="input-text-light"
                                       placeholder="" api_name="" data-arg>

                            </div>
                    <span class="dropdown-toggle" data-toggle="dropdown">
                      是否有效：
                    </span>
                            <div class="input-append">
                                <input type="checkbox"  id="checkbox_api_enable" />
                            </div>
                        </li>
                        <li>
                            <span class="icon-th"></span>
                            <span class="badge">02</span>
                            <span> HOST </span>
                            <div class="input-append">
                                <select style="margin-left: 10px; width: 700px;" id="drop_api_host" ></select>
                            </div>
                        </li>
                        <li>
                            <span class="icon-th"></span>
                            <span class="badge">03</span>
                            <span> 模板 </span>
                            <div class="input-append">
                                <select style="margin-left: 10px; width: 700px;" id="drop_api_template" ></select>
                            </div>
                        </li>
                        <li>
                            <span class="icon-th"></span>
                            <span class="badge">04</span>
                            <span>关键字 </span>
                            <div class="">
                                 <span class="add-on-inline dropdown-toggle"  data-toggle="dropdown"
                                       style="margin-right: 2px;border-bottom: none;"><a href="#iconi"><i id="iconi_keyword" class="icon-pencil"></i></a>
                                    </span><textarea class="form-control" rows="4" id="api_keyword" style="width: 90%; margin-left: 0px;"></textarea>
                            </div>
                        </li>
                        <li>
                            <span class="icon-th"></span>
                            <span class="badge">05</span>
                            <span>描述  </span>
                            <div class="">
                                 <span class="add-on-inline dropdown-toggle"  data-toggle="dropdown"
                                       style="margin-right: 2px;border-bottom: none;"><a href="#iconi"><i id="iconi_descript" class="icon-pencil"></i></a>
                                    </span><textarea class="form-control" rows="6" id="api_descript" style="width: 90%; margin-left: 0px;"></textarea>
                            </div>
                        </li>
                        <li>
                            <span class="icon-th"></span>
                            <span class="badge">06</span>
                            <span>获取参数方式</span>
                            <div class="">
                                 <span class="add-on-inline dropdown-toggle"  data-toggle="dropdown"
                                       style="margin-right: 2px;border-bottom: none;"><a href="#iconi"><i id="iconi_intercept_param" class="icon-pencil"></i></a>
                                    </span><textarea class="form-control" rows="4" id="api_intercept_param" style="width: 90%; margin-left: 0px;"></textarea>
                            </div>
                        </li>
                    </ul>

                    <div class="row" style="margin-left: 20px" id="btn-div">
                        <div class="pull-left">
                            <button class="btn btn-primary" id="btn-save-api" apiid="${api_id}"  apitype="${api_types}">
                                保存
                            </button>
                            <button class="btn btn-info" id="btn-send-api" onclick="window.history.back(-1)">
                                放弃
                            </button>

                        </div>
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
<script type="text/javascript" src="/js/apiModify.js"></script>
<script type="text/javascript" src="/assets/bootstrap/js/bootstrap-typeahead.js"></script>
<script type="text/javascript" src="/assets/bootstrap/js/underscore.js"></script>
</body>
</html>
