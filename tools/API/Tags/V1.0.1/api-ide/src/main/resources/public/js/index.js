var editor = null;
var request_editor = null;
var edit_obj = null;
var verify_descriptors = null;
var setup_descriptors = null;
var teardown_descriptors = null;
var regDelKeyWord = /^_comments*/i;//added by zonglin.li 要删除的json节点需要匹配的正则表达式
var responseEditor = null;
var responseCopyEditor = null;
$(document).ready(function () {
    $('.ui-sortable').sortable({
        update: function (event, ui) {
            var ul = ui.item.parent();
            reOrder(ul);
        }
    });

    loadCaseTree();
    loadDirTree();
    doUpdateSetUpMenu();
    doUpdateVerifyMenu();
    doUpdateTearDownMenu();
});

function reOrder(ul) {
    var lis = $(ul).children(':not(.hide)');
    for (var i = 0; i < lis.length; ++i) {
        var item = $(lis[i]);
        var id = lis.length - i;
        if (id < 10) {
            id = "0" + id;
        }
        $(item.children('span')[1]).html(id);
    }
}

$('body').on('mousedown', '[contenteditable="true"]', function () {
    this.focus();
});

$('body').on('mouseover', '[data-toggle="tooltip"]', function () {
    this.style.cursor = 'pointer';
    $(this).tooltip('show');
});

$('body').on('click', '.icon-th', function (e) {
    var help = $(this).parent().children('div.help');
    help.toggle();
    help.focus();
});

$('body').on('mouseover', '.icon-th', function () {
    this.style.cursor = 'pointer';
});

$('body').on('mouseover', '.icon-pencil', function () {
    this.style.cursor = 'pointer';
});

$('body').on('click', '#menu-add-node li.hide a', function (e) {
    e.preventDefault();
});

$('body').on('click', '.icon-pencil', function () {
    $('#modal-editor').modal('show');
    edit_obj = $(this).parent().prev('input');
    try {
        JSON.parse(edit_obj.val());
        $('input[name="editor-manner"][value="json"]').click();
    } catch (e) {
        $('input[name="editor-manner"][value="text"]').click();
    }
});

$('body').on('mouseenter', 'a[rel=popover]', function () {
    $(this).popover('show');
});

$('body').on('mouseleave', 'a[rel=popover]', function () {
    $(this).popover('hide');
});

$('body').on('click', '.icon-remove', function () {
    var item = $(this).parent();
    var parent = item.parent();
    item.remove();
    reOrder(parent);
});

$('#btn-headers').bind('click', function () {
    $(this).button('toggle');
});

$('#btn-url-params').bind('click', function () {
    $(this).button('toggle');
});

$('#btn-verifies').bind('click', function () {
    $(this).button('toggle');
});

$('#input-verify-class-name').bind('change', function () {
    doUpdateVerifyMenu();
});

$('#input-setup-class-name').bind('change', function () {
    doUpdateSetUpMenu();
});

$('#input-teardown-class-name').bind('change', function () {
    doUpdateTearDownMenu();
});

$('#method').bind('change', function () {
    var method = $(this).val();
    if ('GET' === method || 'DELETE' === method) {
        $("#entity-menu button")[0].click();
        $('#entity-section').addClass('hide');
    } else {
        $('#entity-section').removeClass('hide');
    }
});

function getHttpRequest() {
    var httpRequest = {};
    httpRequest['httpUrl'] = $('#url').val();
    httpRequest['httpMethod'] = $('#method').val();
    httpRequest['headers'] = getHeaders();
    httpRequest['urlParams'] = getUrlParams();
    httpRequest['entities'] = getEntities();
    return httpRequest;
}
function getCaseId() {
    var caseId = $('#caseIdEdit').val();
    return caseId;
}
function getOrderCaseId() {
    var caseId = $('#order-case-id').val();
    return caseId;
}
function getOrderCaseContinue() {
    var continueOrNot = $('#order-case-continue').prop('checked');
    if (continueOrNot === true) {
        return 'true';
    } else {
        return 'false';
    }
}
function loadHttpRequestFrom(httpRequest) {
    if (httpRequest) {
        $('#url').val(httpRequest['httpUrl']);
        $('#method').val(httpRequest['httpMethod']);
        if ('GET' === httpRequest['httpMethod']
            || 'DELETE' === httpRequest['httpMethod']) {
            $('#entity-section').addClass('hide');
        } else {
            $('#entity-section').removeClass('hide');
        }
        loadHeadersFrom(httpRequest['headers']);
        loadUrlParamsFrom(httpRequest['urlParams']);
        loadEntityFrom(httpRequest['entities']);
    }
}
function loadHttpCaseIdFrom(caseId) {
    if (caseId) {
        $('#caseIdEdit').val(caseId);
    }
}
function loadOrderIdFrom(caseId) {
    if (caseId) {
        $('#order-case-id').val(caseId);
    }
}
function loadOrderContinueFrom(continueOrnot) {
    if (continueOrnot && (continueOrnot === true)) {
        $('#order-case-continue').prop('checked', true);
    } else {
        $('#order-case-continue').prop('checked', false);
    }
}
function loadOrderNodeFrom(orderNodes, steps) {
    if (orderNodes) {
        $('#select-modules').empty();
        var options = "";
        var data = orderNodes;
        var option;
        for (var j = 0; j < steps.length; j++) {
            for (var i = 0; i < data.length; ++i) {
                option = data[i];
                if (steps[j] === option['id']) {
                    options += '<option value="' + option['id'] + '" selected>' + option['name'] + '</option>';
                    data.splice(i, 1);
                }
            }
        }
        for (var i = 0; i < data.length; ++i) {
            option = data[i];
            options += '<option value="' + option['id'] + '">' + option['name'] + '</option>';
        }
        $('#select-modules').append(options);
        $('#select-modules').multiSelect('refresh');
    }
}
$('#btn-send-request').bind('click', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    $('#modal-loading').modal('show');
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: 'request',
        data: {'case': JSON.stringify(getPureCaseInfo())},
        success: function (response) {
            if (response['isError']) {
                $('#modal-loading').modal('hide');
                $('#response').empty();
                $('#response').append("<hr/>");
                $('#response').append('<div id="aceEditor"></div>');
                $('#aceEditor').append('发送失败：' + response['message']);
                ace.edit('aceEditor').setReadOnly(true);
            } else {
                data = response['data'];
                $('#response').empty();
                $('#responseCopy').empty();
                try {
                    var jsonObj = JSON.parse(data);
                    responseEditor = new jsoneditor.JSONEditor(
                        $('#response')[0],
                        {
                            mode: 'view',
                            modes: ['code', 'tree', 'view']
                        }, jsonObj);
                    responseCopyEditor = new jsoneditor.JSONEditor(
                        $('#responseCopy')[0],
                        {
                            mode: 'code',
                            modes: ['code', 'tree', 'view']
                        }, null);
                } catch (e) {
                    $('#response').append('<div id="aceEditor"></div>');
                    $('#aceEditor').append(data);
                    ace.edit('aceEditor').setReadOnly(true);
                }
                showCopyResponse(node);
                $('#modal-loading').modal('hide');
            }
        }
    });
});

$('#btn-copy-response').bind('click', function () {
    var responseText = responseEditor.get();
    responseCopyEditor.set(responseText);
});

$('#btn-load-response').bind('click', function () {
    loadCopyResponse();
});

$('#btn-save-response').bind('click', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var data = responseCopyEditor.get();
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'workbench/case/update/response',
            data: {'node': node.id, 'responseCopy': JSON.stringify(data)},
            success: function (response) {
                if (response['isError']) {
                    $('#modal-loading').modal('hide');
                    $('#status').text('保存响应失败：' + response['message']);
                    $('#modal-status').modal('show');
                } else {
                    $('#modal-loading').modal('hide');
                }
            }
        });
    }
    $('#modal-loading').modal('hide');
});

$('#btn-run').bind('click', function () {
    $('#responseSection').addClass('hide');
    $('#localResponse').addClass('hide');
    $('#modal-loading').modal('show');
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: 'run',
//    data: {'case': JSON.stringify(getCaseInfo())},
        data: {'case': JSON.stringify(getPureCaseInfo())},
        success: function (data) {
            $('#modal-loading').modal('hide');
            $('#response').empty();
            $('#response').append("<hr/>");
            $('#response').append('<div id="aceEditor"></div>');
            if (data['isError']) {
                $('#aceEditor').append('运行状态：FAILURE 错误信息：' + data['message']
                + '\n运行日志：\n' + data['data']);
                ace.edit("aceEditor").setReadOnly(true);
            } else {
                $('#aceEditor').append('运行状态：SUCCESS\n运行日志：\n' + data['data']);
                ace.edit("aceEditor").setReadOnly(true);
            }
        }
    });
    showForRun();
});

function loadCase(node) {
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/case/query',
        data: {'node': node},
        success: function (response) {
            if (response['isError']) {
                $('#status').text('更新用例详情错误:' + response['message']);
                $('#modal-status').modal('show');
            } else {
                var data = response['data'];
                loadCaseInfoFrom(data, node);
            }
        }
    });
}


function loadOrderCase(node) {
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/order/case/query',
        data: {'node': node},
        success: function (response) {
            if (response['isError']) {
                $('#status').text('获取OrderCase详情错误:' + response['message']);
                $('#modal-status').modal('show');
            } else {
                var data = response['data'];
                loadOrderCaseInfoFrom(data);
                $('#modal-loading').modal('hide');
            }
        },
        error: function () {
            $('#modal-loading').modal('hide');
        }
    });
}

function selectCase(node) {
    $('#responseSection').addClass('hide');
    $('#btn-setup').removeClass('hide');
    $('#btn-teardown').removeClass('hide');
    $('#request').removeClass('hide');
    $('#btn-div').removeClass('hide');
    // update case tree menu bar
    $('#caseId').removeClass('hide');
    $('#multi-select').addClass('hide');
    $('#menu-delete-node').removeClass('hide');
    $('#menu-rename-node').removeClass('hide');
    $('#menu-rename-node').unbind('click');
    $('#menu-rename-node').on('click', function () {
        var node = $('#case-tree-data').tree('getSelectedNode');
        $('#oldCaseName').val(node.name);
        $('#modal-rename-case').modal('show');
    });
    //$('#menu-rename-node').find('a').attr('href','#modal-rename-case');
    $('#menu-add-folder').addClass('hide');
    $('#menu-add-file').addClass('hide');
    $('#menu-add-suite').addClass('hide');
    $('#menu-add-case').removeClass('hide');
    $('#menu-add-order-suite').addClass('hide');
    $('#menu-add-order-case').addClass('hide');
    // update case editor pannel
    $('#btn-run').removeClass('hide');
    $('#btn-save-case').removeClass('hide');
    $('#btn-copy').removeClass('hide');
    updateClipData();
    loadCase(node.id);
    // remove variables hide
    var nodeId = node.id;
    if ((nodeId.match('p-suites-')) !== null) {
        if ((nodeId.match('p-suites-tpl')) !== null) {
            $('#variable-writeble-section').removeClass('hide');
            $('#variable-writeble-section.input-append').addClass('hide');
            $('#variable-writeble-section.icon-remove').removeClass('hide');
            $('#p-cases-section').addClass('hide');
        }
        else {
            $('#variable-writeble-section').removeClass('hide');
            $('#variable-writeble-section.input-append').removeClass('hide');
            $('#variable-writeble-section.icon-remove').addClass('hide');
            $('#p-cases-section').addClass('hide');
            $('#variable-writeble-section').find();
        }
    } else {
        $('#variable-writeble-section').addClass('hide');
        $('#p-cases-section').addClass('hide');
    }


}
function selectOrderSuite(node) {
    $('#caseId').addClass('hide');
    $('#multi-select').removeClass('hide');
    $('#btn-copy').addClass('hide');
    $('#btn-setup').addClass('hide');
    $('#btn-teardown').addClass('hide');
    $('#request').addClass('hide');
    $('#btn-div').addClass('hide');
    $('#responseSection').addClass('hide');
    $('#entity-section').addClass('hide');

    $('#menu-delete-node').addClass('hide');
    $('#menu-rename-node').removeClass('hide');
    $('#menu-rename-node').unbind('click');
    $('#menu-rename-node').on('click', function () {
        var node = $('#case-tree-data').tree('getSelectedNode');
        var oldSuiteName = node.name;
        if (oldSuiteName.length === 9) {
            oldSuiteName = "";
        } else {
            oldSuiteName = oldSuiteName.substring(9);
        }
        $('#oldOrderSuiteName').val(oldSuiteName);
        $('#modal-rename-order-suite').modal('show');
    });
    //$('#menu-rename-node').find('a').attr('href','#modal-rename-case');
    $('#menu-add-folder').addClass('hide');
    $('#menu-add-file').addClass('hide');
    $('#menu-add-suite').addClass('hide');
    $('#menu-add-case').addClass('hide');
    $('#menu-add-order-suite').addClass('hide');
    $('#menu-add-order-case').removeClass('hide');
    $('#btn-save-order-case').addClass('hide');

}
function selectOrder(node) {
    $('#caseId').addClass('hide');
    $('#multi-select').addClass('hide');
    $('#btn-setup').removeClass('hide');
    $('#btn-teardown').removeClass('hide');
    $('#request').removeClass('hide');
    $('#btn-div').removeClass('hide');
    $('#responseSection').removeClass('hide');

    $('#menu-delete-node').addClass('hide');
    $('#menu-rename-node').addClass('hide');
    //$('#menu-rename-node').find('a').attr('href','#modal-rename-case');
    $('#menu-add-folder').addClass('hide');
    $('#menu-add-file').addClass('hide');
    $('#menu-add-suite').addClass('hide');
    $('#menu-add-case').addClass('hide');
    $('#menu-add-order-suite').removeClass('hide');
    $('#menu-add-order-case').addClass('hide');
    $('#btn-save-order-case').addClass('hide');

}
function selectOrderCase(node) {
    // update case tree menu bar
    $('#caseId').addClass('hide');
    $('#multi-select').removeClass('hide');
    $('#btn-copy').addClass('hide');
    $('#btn-setup').addClass('hide');
    $('#btn-teardown').addClass('hide');
    $('#request').addClass('hide');
    $('#btn-div').addClass('hide');
    $('#responseSection').addClass('hide');
    $('#entity-section').addClass('hide');

    $('#menu-delete-node').removeClass('hide');
    $('#menu-rename-node').removeClass('hide');
    $('#menu-rename-node').unbind('click');
    $('#menu-rename-node').on('click', function () {
        var node = $('#case-tree-data').tree('getSelectedNode');
        $('#oldOrderCaseName').val(node.name);
        $('#modal-rename-order-case').modal('show');
    });
    //$('#menu-rename-node').find('a').attr('href','#modal-rename-case');
    $('#menu-add-folder').addClass('hide');
    $('#menu-add-file').addClass('hide');
    $('#menu-add-suite').addClass('hide');
    $('#menu-add-case').addClass('hide');
    $('#menu-add-order-suite').addClass('hide');
    $('#menu-add-order-case').removeClass('hide');
    $('#modal-loading').modal('show');
    $('#btn-save-order-case').removeClass('hide');
    $('#btn-setup').removeClass('collapsed');
    $('#setup-section').removeClass('in').css("height","0px");
    $('#btn-teardown').removeClass('collapsed');
    $('#teardown-section').removeClass('in').css("height","0px");
    $('#btn-headers').removeClass('collapsed').removeClass("active");
    $('#headers-section').removeClass('in').css("height","0px");
    $('#btn-url-params').removeClass('collapsed').removeClass("active");
    $('#url-params-section').removeClass('in').css("height","0px");
    $('#btn-verifies').removeClass('collapsed').removeClass("active");
    $('#verifies-section').removeClass('in').css("height","0px");
    loadOrderCase(node.id);

}

function updateClipData() {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $.ajax({
            type: 'GET',
            async: false,
            url: 'workbench/case/id',
            data: {'node': node.id},
            success: function (response) {
                data = response['data'];
                $('#btn-copy').attr('data-clipboard-text', data);
            }
        });
    }
}

function selectSuite() {
    // update case tree menu bar
    $('#caseId').addClass('hide');
    $('#multi-select').addClass('hide');
    $('#btn-setup').removeClass('hide');
    $('#btn-teardown').removeClass('hide');
    $('#request').removeClass('hide');
    $('#btn-div').removeClass('hide');
    $('#response').removeClass('hide');
    $('#menu-delete-node').removeClass('hide');
    $('#menu-rename-node').removeClass('hide');
    $('#menu-rename-node').unbind('click');
    $('#menu-rename-node').on('click', function () {
        var node = $('#case-tree-data').tree('getSelectedNode');
        var oldSuiteName = node.name;
        if (oldSuiteName.match('p-suites-tpl-') !== null) {
            oldSuiteName = oldSuiteName.substring(13);
            $('#oldPSuiteTplName').val(oldSuiteName);
            $('#modal-rename-p-suite-tpl').modal('show');
        }
        if (oldSuiteName.match('p-suites-') !== null) {
            if (nodeId.match('p-suites-tpl') === null) {
                oldSuiteName = oldSuiteName.substring(9);
                $('#oldPSuiteName').val(oldSuiteName);
                $('#modal-rename-p-suite').modal('show');
            }
        }
        if (oldSuiteName.match('testsuites') !== null) {
            if (oldSuiteName.length === 10) {
                oldSuiteName = "";
            } else {
                oldSuiteName = oldSuiteName.substring(10);
            }
            $('#oldSuiteName').val(oldSuiteName);
            $('#modal-rename-suite').modal('show');
        }
    });
    //$('#menu-rename-node').find('a').attr('href','#modal-rename-suite');
    $('#menu-add-folder').addClass('hide');
    $('#menu-add-file').addClass('hide');
    $('#menu-add-suite').addClass('hide');
    $('#menu-add-case').removeClass('hide');
    $('#menu-add-order-suite').addClass('hide');
    $('#menu-add-order-case').addClass('hide');
    // update case editor pannel
    $('#btn-run').addClass('hide');
    $('#btn-save-case').addClass('hide');
    $('#btn-copy').addClass('hide');
    // hide relative variables
    $('#btn-variable').addClass('hide');
    $('#variable-writeble-section').addClass('hide');
    var node = $('#case-tree-data').tree('getSelectedNode');
    var nodeId = node.id;
    if ((nodeId.match('p-suites-')) !== null) {
        if ((nodeId.match('p-suites-tpl')) === null) {
            $('#p-cases-section').removeClass('hide');
            doLoadPCases(nodeId);
        } else {
            $('#p-cases-section').addClass('hide');
        }
    } else {
        $('#p-cases-section').addClass('hide');
    }
}

function selectFile() {
    // update case tree menu bar
    $('#caseId').addClass('hide');
    $('#multi-select').addClass('hide');
    $('#btn-setup').removeClass('hide');
    $('#btn-teardown').removeClass('hide');
    $('#request').removeClass('hide');
    $('#btn-div').removeClass('hide');
    $('#response').removeClass('hide');
    $('#menu-delete-node').removeClass('hide');
    $('#menu-rename-node').addClass('hide');
    $('#menu-add-folder').addClass('hide');
    $('#menu-add-file').addClass('hide');
    $('#menu-add-suite').removeClass('hide');
    $('#menu-add-case').addClass('hide');
    $('#menu-add-order-suite').addClass('hide');
    $('#menu-add-order-case').addClass('hide');
    // update case editor pannel
    $('#btn-run').addClass('hide');
    $('#btn-save-case').addClass('hide');
    $('#btn-copy').addClass('hide');
}

function selectFolder() {
    // update case tree menu bar
    $('#caseId').addClass('hide');
    $('#multi-select').addClass('hide');
    $('#btn-setup').removeClass('hide');
    $('#btn-teardown').removeClass('hide');
    $('#request').removeClass('hide');
    $('#btn-div').removeClass('hide');
    $('#response').removeClass('hide');
    $('#menu-delete-node').removeClass('hide');
    $('#menu-rename-node').addClass('hide');
    $('#menu-add-folder').removeClass('hide');
    $('#menu-add-file').removeClass('hide');
    $('#menu-add-suite').addClass('hide');
    $('#menu-add-case').addClass('hide');
    $('#menu-add-order-suite').addClass('hide');
    $('#menu-add-order-case').addClass('hide');
    // update case editor pannel
    $('#btn-run').addClass('hide');
    $('#btn-save-case').addClass('hide');
    $('#btn-copy').addClass('hide');
}

function selectOther() {
    // update case tree menu bar
    $('#caseId').addClass('hide');
    $('#multi-select').addClass('hide');
    $('#btn-setup').removeClass('hide');
    $('#btn-teardown').removeClass('hide');
    $('#request').removeClass('hide');
    $('#btn-div').removeClass('hide');
    $('#response').removeClass('hide');
    $('#menu-delete-node').addClass('hide');
    $('#menu-rename-node').addClass('hide');
    $('#menu-add-folder').removeClass('hide');
    $('#menu-add-file').addClass('hide');
    $('#menu-add-suite').addClass('hide');
    $('#menu-add-case').addClass('hide');
    $('#menu-add-order-suite').addClass('hide');
    $('#menu-add-order-case').addClass('hide');
    // update case editor pannel
    $('#btn-run').addClass('hide');
    $('#btn-save-case').addClass('hide');
    $('#btn-copy').addClass('hide');
}

function moveCase(event, moved_id, target_id, position, parent_id) {
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/case/move',
        data: {'moved_node': moved_id, 'target_node': target_id, 'position': position, 'parent_node': parent_id},
        success: function (response) {
            if (response['isError']) {
                $('#status').text('移动用例错误:' + response['message']);
                $('#modal-status').modal('show');
            } else {
                event.move_info.do_move();
            }
        }
    });
}

function loadCaseTree() {
    var caseTree = $('#case-tree-data');
    caseTree.bind('tree.select', function (event) {
        if (event.node) {
            var node = event.node;
            if ("case" === node.type) {
                selectCase(node);
            } else if ("suite" === node.type) {
                selectSuite();
            } else if ("xlsx" === node.type) {
                selectFile();
            } else if ("folder" === node.type) {
                selectFolder();
            } else if ("ordercase" === node.type) {
                selectOrderCase(node);
            } else if ("ordersuite" === node.type) {
                selectOrderSuite(node);
            } else if ("order" === node.type) {
                selectOrder(node);
            } else {
                selectOther();
            }
        } else {
            selectOther();
        }
    });

    caseTree.bind('tree.click', function (event) {
        event.preventDefault();
        var node = event.node;
        $('#case-tree-data').tree('selectNode', node);
    });

    caseTree.bind('tree.move', function (event) {
        event.preventDefault();
        moveCase(event, event.move_info.moved_node.id, event.move_info.target_node.id, event.move_info.position, event.move_info.previous_parent.id);
    });
    caseTree.bind('tree.open', function (event) {
        var node = event.node;
        $('#case-tree-data').tree('selectNode', node);
    });

    caseTree.bind('tree.close', function (event) {
        var node = event.node;
        if (node && 'case' !== node.type) {
            node.load_on_demand = true;
        }
        $('#case-tree-data').tree('selectNode', node);
    });

    caseTree.bind('tree.dblclick', function (event) {
        var node = event.node;
        $('#case-tree-data').tree('toggle', node);
    });

    caseTree.tree({
        dataUrl: function (node) {
            var data = {};
            if (node) {
                data['type'] = node.type;
                data['node'] = node.id;
            } else {
                data['type'] = 'folder';
                data['node'] = '';
            }
            return {
                'url': 'workbench/tree/node/children',
                'data': data
            };
        },
        dataFilter: function (data) {
            if (data['isError']) {
                $('#status').text('加载用例树失败：' + data['message']);
                $('#modal-status').modal('show');
                return [];
            } else {
                return data['data'];
            }
        },
        dragAndDrop: true,
        onCanMove: function (node) {
            if (!node.type === "case") {
                return false;
            }
            else {
                return true;
            }
        },
        onCanMoveTo: function (moved_node, target_node, position) {
            return (target_node.parent.type === "suite") && (position !== "inside") && (target_node.children.length === 0) && (target_node.parent === moved_node.parent);

        },
        autoEscape: false,
        onCreateLi: function (node, $li) {
            var e = '';
            if ('xlsx' === node.type) {
                e = '<span class="icon-file"></span><span>&nbsp;</span>';
            } else if ('folder' === node.type) {
                e = '<span class="icon-folder-close"></span><span>&nbsp;</span>';
            } else if ('suite' === node.type) {
                e = '<span class="label label-info">suite</span><span>&nbsp;</span>';
            } else if ('case' === node.type) {
                e = '<span class="label label-success">case</span><span>&nbsp;</span>';
            } else if ('order' === node.type) {
                e = '<span class="label label-success">Order</span><span>&nbsp;</span>';
            } else if ('ordersuite' === node.type) {
                e = '<span class="label label-info">Osuite</span><span>&nbsp;</span>';
            } else if ('ordercase' === node.type) {
                e = '<span class="label label-success">Ocase</span><span>&nbsp;</span>';
            }

            $li.find('.jqtree-title').before(e);
        },
        selectable: true
    });
    $('#main-panel-workspace').removeClass('active');
    $('#main-panel-case').addClass('active');
}

function getCaseInfo() {
    var data = {};
    data['id'] = getCaseId();
    data['httpRequest'] = getHttpRequest();
    data['verifyClass'] = getVerifyClass();
    data['verifySteps'] = getVerifySteps();
    data['setUpClass'] = getSetUpClass();
    data['setUpSteps'] = getSetUpSteps();
    data['tearDownClass'] = getTearDownClass();
    data['tearDownSteps'] = getTearDownSteps();
    data['caseVariables'] = getWritableVariableMenu();
    return data;
}

function getOrderCaseInfo() {
    var selection = $(".ms-selection").find(".ms-list").find(".ms-selected");
    var modules = [];
    var steps = [];
    var moduleIds = $('#select-modules').val();
    if (moduleIds) {
        for (var i = 0; i < selection.length; ++i) {

            var id = $(selection[i]).attr("id");
            id = id.substring(0, id.length - "-selection".length);
            var moduleId = id;
            var moduleName = $('#select-modules').find(
                'option[value="' + moduleId + '"]').html();
            var module = {'id': moduleId, 'name': moduleName};
            var step = moduleId;
            modules.push(module);
            steps.push(step);
        }
    }
    // assemble data
    var data = {};
    data['orderNodes'] = modules;
    data['steps'] = steps;
    data['id'] = getOrderCaseId();
    data['continueAfterFailure'] = getOrderCaseContinue();
    return data;
}

function loadCaseInfoFrom(data, node) {
    loadHttpCaseIdFrom(data['id']);
    loadHttpRequestFrom(data['httpRequest']);
    loadVerifyClassFrom(data['verifyClass']);
    loadVerifyStepsFrom(data['verifySteps']);
    loadSetUpClassFrom(data['setUpClass']);
    loadSetUpStepsFrom(data['setUpSteps']);
    loadTearDownClassFrom(data['tearDownClass']);
    loadTearDownStepsFrom(data['tearDownSteps']);
    loadCaseVariablesFrom(data['caseVariables'], node);
}
function loadOrderCaseInfoFrom(data) {
    loadOrderIdFrom(data['id']);
    loadOrderContinueFrom(data['continueAfterFailure']);
    loadOrderNodeFrom(data['orderNodes'], data['steps']);
}

ZeroClipboard.config({
    moviePath: 'assets/zeroclipboard-1.3.5/ZeroClipboard.swf'
});
var client = new ZeroClipboard($("#btn-copy"));
client.on('load', function () {
    client.on('complete', function () {
        alert('Copied: ' + $('#btn-copy').attr('data-clipboard-text'));
    });
});

$('#btn-save-case').bind('click', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var data = getCaseInfo();
        data['name'] = node.name;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'workbench/case/update',
            data: {'node': node.id, 'case': JSON.stringify(data)},
            success: function (response) {
                if (response['isError']) {
                    $('#modal-loading').modal('hide');
                    $('#status').text('保存用例失败：' + response['message']);
                    $('#modal-status').modal('show');
                } else {
                    data = response['data'];
                    var newNode = {
                        id: data
                    };
                    updateClipData();
                    $('#case-tree-data').tree('updateNode', node, newNode);
                    node = $('#case-tree-data').tree('getNodeById', newNode.id);
                    $('#case-tree-data').tree('selectNode', node);
                    $('#modal-loading').modal('hide');
                }
            }
        });
    }
});

$('#btn-save-order-case').bind('click', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var data = getOrderCaseInfo();
        data['caseDesc'] = node.name;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'workbench/order/case/update',
            data: {'node': node.id, 'ordercase': JSON.stringify(data)},
            success: function (response) {
                if (response['isError']) {
                    $('#modal-loading').modal('hide');
                    $('#status').text('保存OrderCase用例失败：' + response['message']);
                    $('#modal-status').modal('show');
                } else {
                    data = response['data'];
                    var newNode = {
                        id: data
                    };
                    $('#case-tree-data').tree('updateNode', node, newNode);
                    node = $('#case-tree-data').tree('getNodeById', newNode.id);
                    $('#case-tree-data').tree('selectNode', node);
                    $('#modal-loading').modal('hide');
                }
            }
        });
    }
});
$('body').on('click', '#btn-new-folder', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    var node_id = '';
    if (node) {
        node_id = node.id;
    }
    $('#modal-loading').modal('show');
    var name = $('#folder').val().trim();
    if (name === '') {
        $('#modal-loading').modal('hide');
        $('#status').text('folder名称不能为空');
        $('#modal-status').modal('show');
    } else {
        $.ajax({
            type: 'POST',
            url: 'workbench/tree/node/folder/add',
            data: {'node': node_id, 'folder': name},
            success: function (response) {
                if (response['isError']) {
                    $('#modal-loading').modal('hide');
                    $('#status').text('新增文件夹失败：' + response['message']);
                    $('#modal-status').modal('show');
                } else {
                    data = response['data'];
                    var newNode = {
                        label: name,
                        id: data,
                        type: 'folder',
                        loadOnDemand: true,
                        children: []
                    };
                    if (node) {
                        $('#case-tree-data').tree('openNode', node);
                        $('#case-tree-data').tree('appendNode', newNode, node);
                    } else {
                        $('#case-tree-data').tree('appendNode', newNode);
                    }
                    node = $('#case-tree-data').tree('getNodeById', newNode.id);
                    $('#case-tree-data').tree('selectNode', node);
                    $('#modal-loading').modal('hide');
                }
            }
        });
    }
});

$('body').on('click', '#btn-new-file', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    $('#modal-loading').modal('show');
    var name = $('#excelName').val().trim();
    if (name === '') {
        $('#modal-loading').modal('hide');
        $('#status').text('file名称不能为空');
        $('#modal-status').modal('show');
    } else {
        $.ajax({
            type: 'POST',
            url: 'workbench/tree/node/file/add',
            data: {'node': node.id, 'file': name},
            success: function (response) {
                if (!response['isError']) {
                    data = response['data'];
                    var newNode = {
                        label: name + '.xlsx',
                        id: data,
                        type: 'xlsx',
                        loadOnDemand: true,
                        children: []
                    };
                    var newChildNode = {
                        label: 'testsuites',
                        id: data + '#testsuites',
                        type: 'suite',
                        loadOnDemand: true,
                        children: []
                    };
                    if (node) {
                        $('#case-tree-data').tree('openNode', node);
                        $('#case-tree-data').tree('appendNode', newNode, node);
                    } else {
                        $('#case-tree-data').tree('appendNode', newNode);
                    }
                    node = $('#case-tree-data').tree('getNodeById', newNode.id);
                    $('#case-tree-data').tree('appendNode', newChildNode, node);
                    $('#case-tree-data').tree('selectNode', node);
                    $('#modal-loading').modal('hide');
                } else {
                    $('#modal-loading').modal('hide');
                    $('#status').text('新增文件失败：' + response['message']);
                    $('#modal-status').modal('show');
                }
            }
        });
    }
});

$('body').on('click', '#btn-new-suite', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#testsuite').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/suite/add',
                data: {'node': node.id, 'suite': name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'testsuites' + name,
                            id: data,
                            type: 'suite',
                            loadOnDemand: true,
                            children: []
                        };
                        if (node) {
                            $('#case-tree-data').tree('openNode', node);
                            $('#case-tree-data').tree('appendNode', newNode, node);
                        } else {
                            $('#case-tree-data').tree('appendNode', newNode);
                        }
                        node = $('#case-tree-data').tree('getNodeById', newNode.id);
                        $('#case-tree-data').tree('selectNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('新增suite失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});

//add new test suite for dev added by zonglin.li
$('body').on('click', '#btn-new-dev-suite', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#testsuite-for-dev').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/devsuite/add',
                data: {'node': node.id, 'suite': name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'testsuitesForDev' + name,
                            id: data,
                            type: 'suite',
                            loadOnDemand: true,
                            children: []
                        };
                        if (node) {
                            $('#case-tree-data').tree('openNode', node);
                            $('#case-tree-data').tree('appendNode', newNode, node);
                        } else {
                            $('#case-tree-data').tree('appendNode', newNode);
                        }
                        node = $('#case-tree-data').tree('getNodeById', newNode.id);
                        $('#case-tree-data').tree('selectNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('新增suite失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});


$('body').on('click', '#btn-save-new', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        if ('case' === node.type) {
            node = node.parent;
        }
        var data = getCaseInfo();
        data['name'] = $('#caseName').val().trim();
        if (data['name'] === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('用例名不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/case/add',
                data: {'node': node.id, 'case': JSON.stringify(data)},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        $('#case-tree-data').tree('appendNode',
                            {
                                label: $('#caseName').val().trim(),
                                id: data,
                                type: 'case'
                            },
                            node);
                        node = $('#case-tree-data').tree('getNodeById', data);
                        $('#case-tree-data').tree('selectNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('新增用例失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});

$('body').on('click', '#btn-save-new-order-case', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        if ('ordercase' === node.type) {
            node = node.parent;
        }
        var data = getOrderCaseInfo();
        data['caseDesc'] = $('#ordercaseName').val().trim();
        if (data['caseDesc'] === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('用例名不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/order/case/add',
                data: {'node': node.id, 'ordercase': JSON.stringify(data)},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        $('#case-tree-data').tree('appendNode',
                            {
                                label: $('#ordercaseName').val().trim(),
                                id: data,
                                type: 'ordercase'
                            },
                            node);
                        node = $('#case-tree-data').tree('getNodeById', data);
                        $('#case-tree-data').tree('selectNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('新增用例失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});
$('body').on('click', '#btn-new-order-suite', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#ordersuite').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/order/suite/add',
                data: {'node': node.id, 'ordersuite': name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'OrderList' + name,
                            id: data,
                            type: 'ordersuite',
                            loadOnDemand: true,
                            children: []
                        };
                        if (node) {
                            $('#case-tree-data').tree('openNode', node);
                            $('#case-tree-data').tree('appendNode', newNode, node);
                        } else {
                            $('#case-tree-data').tree('appendNode', newNode);
                        }
                        node = $('#case-tree-data').tree('getNodeById', newNode.id);
                        $('#case-tree-data').tree('selectNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('新增Ordersuite失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});
$('body').on('click', '#btn-rename-case', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var data = getCaseInfo();
        data['name'] = $('#oldCaseName').val().trim();
        if (data['name'] === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('用例名不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/case/update/case/name',
                data: {'node': node.id, 'case': JSON.stringify(data)},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        $('#case-tree-data').tree('updateNode', node,
                            {
                                label: $('#oldCaseName').val().trim(),
                                id: data,
                                type: 'case'
                            });
                        node = $('#case-tree-data').tree('getNodeById', data);
                        $('#case-tree-data').tree('selectNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('重命名用例失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});

$('body').on('click', '#btn-rename-order-case', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var data = getOrderCaseInfo();
        data['caseDesc'] = $('#oldOrderCaseName').val().trim();
        if (data['name'] === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('用例名不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/order/case/update/order/case/name',
                data: {'node': node.id, 'ordercase': JSON.stringify(data)},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        $('#case-tree-data').tree('updateNode', node,
                            {
                                label: $('#oldOrderCaseName').val().trim(),
                                id: data,
                                type: 'ordercase'
                            });
                        node = $('#case-tree-data').tree('getNodeById', data);
                        $('#case-tree-data').tree('selectNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('重命名用例失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});

$('body').on('click', '#btn-rename-suite', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    var oldSuiteName = node.name;
    if (oldSuiteName.length === 10) {
        oldSuiteName = "";
    } else {
        oldSuiteName = oldSuiteName.substring(10);
    }
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#oldSuiteName').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/suite/update',
                data: {'node': node.id, 'suite': oldSuiteName, newsuite: name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'testsuites' + name,
                            id: data,
                            type: 'suite'
                        };
                        $('#case-tree-data').tree('updateNode', node, newNode);

                        node = $('#case-tree-data').tree('getNodeById', newNode.id);
                        $('#case-tree-data').tree('selectNode', node.parent);
                        $('#case-tree-data').tree('closeNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('重命名suite失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});
$('body').on('click', '#btn-rename-order-suite', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    var oldSuiteName = node.name;
    if (oldSuiteName.length === 9) {
        oldSuiteName = "";
    } else {
        oldSuiteName = oldSuiteName.substring(9);
    }
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#oldOrderSuiteName').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('Ordersuite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/order/suite/update',
                data: {'node': node.id, 'ordersuite': oldSuiteName, newordersuite: name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'OrderList' + name,
                            id: data,
                            type: 'ordersuite'
                        };
                        $('#case-tree-data').tree('updateNode', node, newNode);

                        node = $('#case-tree-data').tree('getNodeById', newNode.id);
                        $('#case-tree-data').tree('selectNode', node.parent);
                        $('#case-tree-data').tree('closeNode', node);
                        $('#modal-loading').modal('hide');
                    } else {
                        $('#modal-loading').modal('hide');
                        $('#status').text('重命名Ordersuite失败：' + response['message']);
                        $('#modal-status').modal('show');
                    }
                }
            });
        }
    }
});

function reloadTree() {
    $('#case-tree-data').tree('loadDataFromUrl',
        'workbench/tree/node/children?type=folder&node=', null,
        function () {
            selectOther();
        }
    );
}

$('#nav-compile-workbench').on('click', function () {
    $('#modal-loading').modal('show');
    $.ajax({
        type: 'POST',
        url: 'workbench/compile',
        success: function (response) {
            if (!response['isError']) {
                doUpdateSetUpMenu();
                doUpdateVerifyMenu();
                doUpdateTearDownMenu();
                $('#modal-loading').modal('hide');
                $('#status').text('编译工作区成功');
                $('#modal-status').modal('show');
            } else {
                $('#modal-loading').modal('hide');
                $('#status').text('编译工作区失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
});

$('#nav-shutdown').on('click', function () {
    $.ajax({
        type: 'POST',
        url: 'shutdown'
    });
    $('body').children().remove();
    $('body').append("JIDE服务已经成功关闭，谢谢使用！");
});

////////////////////////////////////////////////////////////////////////////////
//
// SetUp
//
////////////////////////////////////////////////////////////////////////////////

function loadSetUpClassFrom(setUpClass) {
    if (!setUpClass) {
        setUpClass = "com.gome.test.api.environment.EnvManager";
    }
    $('#input-setup-class-name').val(setUpClass);
    doUpdateSetUpMenu();
}

function loadSetUpStepsFrom(setUpSteps) {
    if (!setUpSteps) {
        setUpSteps = [];
    }
    $('#setUpSteps li:not(#setup-step-template)').remove();
    for (var i = 0; i < setUpSteps.length; ++i) {
        // clone setup step template
        var setUpStepDom = $('#setup-step-template').clone(true);
        setUpStepDom.removeAttr('id');
        setUpStepDom.removeAttr('class');
        var id = setUpSteps.length - i;
        if (id < 10) {
            id = "0" + id;
        }
        // parse setup step
        var setUpStep = setUpSteps[i];
        if (setUpStep['doc']) {
            setUpStepDom.children('div.help').text(setUpStep['doc']);
        }
        var setUpMethod = setUpStep['keyword'];
        $(setUpStepDom.children('span')[1]).html(id);
        var methodDom = $(setUpStepDom.children('input')[0]);
        methodDom.val(setUpMethod);
        var setUpParam = setUpStep['args'];
        for (var j = 0; j < setUpParam.length; ++j) {
            var paramDom = $(setUpStepDom.children('input')[1]).clone(true);
            paramDom.val(setUpParam[j]);
            $(setUpStepDom.children('span')[2]).before(paramDom);
        }
        $(setUpStepDom.children('input')[1]).remove();
        $('#setup-step-template').before(setUpStepDom);
    }
}

$('#menu-setup').on('click', 'a', function () {
    var setUpMethod = $(this).text();
    var params = null;
    for (var i = 0; i < setup_descriptors.length; ++i) {
        if (setUpMethod === setup_descriptors[i]['method']) {
            params = setup_descriptors[i]['params'];
            break;
        }
    }
    var id = $('#setUpSteps').children().length;
    if (id < 10) {
        id = "0" + id;
    }
    var setUpStepDom = $('#setup-step-template').clone(true);
    setUpStepDom.removeAttr('id');
    setUpStepDom.removeAttr('class');
    $(setUpStepDom.children('span')[1]).html(id);
    var methodDom = $(setUpStepDom.children('input')[0]);
    methodDom.val(setUpMethod);
    for (var i = 0; i < params.length; ++i) {
        var paramDom = $(setUpStepDom.children('input')[1]).clone(true);
        paramDom.attr('placeholder', params[i]);
        $(setUpStepDom.children('span')[2]).before(paramDom);
    }
    $(setUpStepDom.children('input')[1]).remove();
    $('#setUpSteps').prepend(setUpStepDom);
});

function getSetUpClass() {
    return $('#input-setup-class-name').val();
}

function getSetUpSteps() {
    var steps = [];
    var setUpSteps = $('#setUpSteps li:not(#setup-step-template)');
    for (var i = 0; i < setUpSteps.length; ++i) {
        var setUpStep = $(setUpSteps[i]);
        var step = {};
        step['doc'] = setUpStep.find('[data-doc]').text();
        step['keyword'] = setUpStep.find('[data-keyword]').val();
        var args = [];
        var m = setUpStep.find("[data-arg]");
        for (var j = 0; j < m.length; ++j) {
            args.push($(m[j]).val());
        }
        step['args'] = args;
        steps.push(step);
    }
    return steps;
}

function doUpdateSetUpMenu() {
    $('#menu-setup').children(':not(#setup-menu-template)').remove();
    var data = {};
    data['setUpClass'] = $('#input-setup-class-name').val();
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/menu/setup/query',
        data: data,
        success: function (response) {
            if (response['isError']) {
                $('#status').text('加载SetUp方法列表失败: ' + response['message']);
                $('#modal-status').modal('show');
            } else {
                data = response['data'];
                setup_descriptors = data;
                for (var i = 0; i < data.length; ++i) {
                    var item = data[i];
                    var setUpMenuDom = $('#setup-menu-template').clone(true);
                    setUpMenuDom.removeAttr('id');
                    setUpMenuDom.removeAttr('class');
                    setUpMenuDom.find('a').attr('data-content', item['description']);
                    setUpMenuDom.find('a').text(item['method']);
                    $('#menu-setup').append(setUpMenuDom);
                }
                $('#menu-setup').perfectScrollbar();
            }
        }
    });
}

////////////////////////////////////////////////////////////////////////////////
//
// Headers
//
////////////////////////////////////////////////////////////////////////////////

function loadHeadersFrom(headers) {//load from back
    $('#headers').children(':not(#header-template)').remove();
    if (headers) {
        var headerTpl = $('#header-template');
        for (var i = 0; i < headers.length; ++i) {
            var header = headers[i];
            var headerDom = headerTpl.clone(true);
            headerDom.removeAttr('id');
            headerDom.removeAttr('class');
            var id = headers.length - i;
            if (id < 10) {
                id = "0" + id;
            }
            $(headerDom.children('span')[1]).html(id);
            headerDom.find('[data-key]').val(header['key']);
            headerDom.find('[data-value]').val(header['value']);
            headerDom.find('[data-encryption]').val(header['encryption']);
            if ('AES' === header['encryption']) {
                headerDom.find('[data-secret]').attr('type', 'text');
            }
            headerDom.find('[data-secret]').val(header['secret']);
            if (header['doc']) {
                headerDom.find('[data-doc]').text(header['doc']);
            }
            headerDom.find('[data-comments]').val(header['comments']);//added by zonglin.li
            var node = $('#case-tree-data').tree('getSelectedNode');//added by zonglin.li
            headerDom = hideCommentsColumns(node, headerDom);//added by zonglin.li
            headerTpl.before(headerDom);
        }
    }
}

$('#link-add-header').on('click', function () {
    var headerTpl = $('#header-template');
    var headerDom = headerTpl.clone(true);
    headerDom.removeAttr('id');
    headerDom.removeAttr('class');
    var headers = headerTpl.parent();
    var id = headers.children().length;
    if (id < 10) {
        id = "0" + id;
    }
    $(headerDom.children('span')[1]).html(id);
    var node = $('#case-tree-data').tree('getSelectedNode');//added by zonglin.li
    headerDom = hideCommentsColumns(node, headerDom);//added by zonglin.li
    headers.prepend(headerDom);
});

function getHeaders() {//get from front
    var headers = [];
    var headerList = $('#headers').children(':not(#header-template)');
    for (var i = 0; i < headerList.length; ++i) {
        var headerDom = headerList[i];
        var key = $(headerDom).find('[data-key]').val();
        var value = $(headerDom).find('[data-value]').val();
        var encryption = $(headerDom).find('[data-encryption]').val();
        var secret = null;
        if ('AES' === encryption) {
            secret = $(headerDom).find('[data-secret]').val();
        }
        var doc = $(headerDom).find('[data-doc]').text();
        var comments = $(headerDom).find('[data-comments]').val();//add by zonglin.li
        var header = {};
        header['key'] = key;
        header['value'] = value;
        header['encryption'] = encryption;
        header['secret'] = secret;
        header['doc'] = doc;
        header['comments'] = comments;
        headers.push(header);
    }
    return headers;
}

$('#headers').on('change', '[data-encryption]', function () {
    var encryption = $(this).val();
    var parent = $(this).parent();
    var secret = parent.find('[data-secret]');
    if (encryption === 'AES') {
        secret.attr('type', 'text');
        secret.attr('placeholder', 'secret');
    } else {
        secret.removeAttr('placeholder');
        secret.attr('type', 'hidden');
    }
});

////////////////////////////////////////////////////////////////////////////////
//
// URL Parameters
//
////////////////////////////////////////////////////////////////////////////////

function loadUrlParamsFrom(urlParams) {
    $('#url-params').children(':not(#url-param-template)').remove();
    if (urlParams) {
        var urlParamTpl = $('#url-param-template');
        for (var i = 0; i < urlParams.length; ++i) {
            var urlParam = urlParams[i];
            var urlParamDom = urlParamTpl.clone(true);
            urlParamDom.removeAttr('id');
            urlParamDom.removeAttr('class');
            var id = urlParams.length - i;
            if (id < 10) {
                id = "0" + id;
            }
            $(urlParamDom.children('span')[1]).html(id);
            urlParamDom.find('[data-key]').val(urlParam['key']);
            urlParamDom.find('[data-value]').val(urlParam['value']);
            urlParamDom.find('[data-encryption]').val(urlParam['encryption']);
            if ('AES' === urlParam['encryption']) {
                urlParamDom.find('[data-secret]').attr('type', 'text');
            }
            urlParamDom.find('[data-secret]').val(urlParam['secret']);
            if (urlParam['doc']) {
                urlParamDom.find('[data-doc]').text(urlParam['doc']);
            }
            urlParamDom.find('[data-comments]').val(urlParam['comments']);//added by zonglin.li
            var node = $('#case-tree-data').tree('getSelectedNode');//added by zonglin.li
            urlParamDom = hideCommentsColumns(node, urlParamDom);//added by zonglin.li
            urlParamTpl.before(urlParamDom);
        }
    }
}

$('#link-add-url-param').on('click', function () {
    var urlParamTpl = $('#url-param-template');
    var urlParamDom = urlParamTpl.clone(true);
    urlParamDom.removeAttr('id');
    urlParamDom.removeAttr('class');
    var urlParams = urlParamTpl.parent();
    var id = urlParams.children().length;
    if (id < 10) {
        id = "0" + id;
    }
    $(urlParamDom.children('span')[1]).html(id);
    var node = $('#case-tree-data').tree('getSelectedNode');//added by zonglin.li
    urlParamDom = hideCommentsColumns(node, urlParamDom);//added by zonglin.li
    urlParams.prepend(urlParamDom);
});

function getUrlParams() {
    var urlParams = [];
    var urlParamList = $('#url-params').children(':not(#url-param-template)');
    for (var i = 0; i < urlParamList.length; ++i) {
        var urlParamDom = urlParamList[i];
        var key = $(urlParamDom).find('[data-key]').val();
        var value = $(urlParamDom).find('[data-value]').val();
        var encryption = $(urlParamDom).find('[data-encryption]').val();
        var secret = null;
        if ('AES' === encryption) {
            secret = $(urlParamDom).find('[data-secret]').val();
        }
        var doc = $(urlParamDom).find('[data-doc]').text();
        var comments = $(urlParamDom).find('[data-comments]').val();
        var urlParam = {};
        urlParam['key'] = key;
        urlParam['value'] = value;
        urlParam['encryption'] = encryption;
        urlParam['secret'] = secret;
        urlParam['doc'] = doc;
        urlParam['comments'] = comments;
        urlParams.push(urlParam);
    }
    return urlParams;
}

$('#url-params').on('change', '[data-encryption]', function () {
    var encryption = $(this).val();
    var parent = $(this).parent();
    var secret = parent.find('[data-secret]');
    if ('AES' === encryption) {
        secret.attr('type', 'text');
        secret.attr('placeholder', 'secret');
    } else {
        secret.removeAttr('placeholder');
        secret.attr('type', 'hidden');
    }
});

////////////////////////////////////////////////////////////////////////////////
//
// Entities
//
////////////////////////////////////////////////////////////////////////////////

function loadEntityFrom(entities) {
    if (entities) {
        var type = entities['type'];
        if ('x-www-form-urlencoded' === type) {
            $("#entity-menu button")[0].click();
            entities = JSON.parse(entities['data']);
            var entityTpl = $('#entity-template');
            for (var i = 0; i < entities.length; ++i) {
                var entity = entities[i];
                var entityDom = entityTpl.clone(true);
                entityDom.removeAttr('id');
                entityDom.removeAttr('class');
                var id = entities.length - i;
                if (id < 10) {
                    id = "0" + id;
                }
                $(entityDom.children('span')[1]).html(id);
                entityDom.find('[data-key]').val(entity['key']);
                entityDom.find('[data-value]').val(entity['value']);
                entityDom.find('[data-encryption]').val(entity['encryption']);
                if ('AES' === entity['encryption']) {
                    entityDom.find('[data-secret]').attr('type', 'text');
                }
                entityDom.find('[data-secret]').val(entity['secret']);
                if (entity['doc']) {
                    entityDom.find('[data-doc]').text(entity['doc']);
                }
                entityDom.find('[data-comments]').val(entity['comments']);//added by zonglin.li
                var node = $('#case-tree-data').tree('getSelectedNode');//added by zonglin.li
                entityDom = hideCommentsColumns(node, entityDom);//added by zonglin.li
                entityTpl.before(entityDom);
            }
        } else {
            $("#entity-menu button")[1].click();
            request_editor.set(JSON.parse(entities['data']));
        }
    }
}

$('#link-add-entity').on('click', function () {
    var entityTpl = $('#entity-template');
    var entityDom = entityTpl.clone(true);
    entityDom.removeAttr('id');
    entityDom.removeAttr('class');
    var entities = entityTpl.parent();
    var id = entities.children().length;
    if (id < 10) {
        id = "0" + id;
    }
    $(entityDom.children('span')[1]).html(id);
    var node = $('#case-tree-data').tree('getSelectedNode');//added by zonglin.li
    entityDom = hideCommentsColumns(node, entityDom);//added by zonglin.li
    entities.prepend(entityDom);
});

function getEntities() {
    var entities = {};
    entities['type'] = $('#entity-menu button[class*="active"]').text().trim();
    if ('x-www-form-urlencoded' === entities['type']) {
        var data = [];
        var entityList = $('#entity-x-www-form-urlencoded').children(':not(#entity-template)');
        for (var i = 0; i < entityList.length; ++i) {
            var entityDom = entityList[i];
            var key = $(entityDom).find('[data-key]').val();
            var value = $(entityDom).find('[data-value]').val();
            var encryption = $(entityDom).find('[data-encryption]').val();
            var secret = null;
            if ('AES' === encryption) {
                secret = $(entityDom).find('[data-secret]').val();
            }
            var doc = $(entityDom).find('[data-doc]').text();
            var comments = $(entityDom).find('[data-comments]').val();
            var entity = {};
            entity['key'] = key;
            entity['value'] = value;
            entity['encryption'] = encryption;
            entity['secret'] = secret;
            entity['doc'] = doc;
            entity['comments'] = comments;
            data.push(entity);
        }
        entities['data'] = JSON.stringify(data);
    } else {
        entities['type'] = "application/json";
        entities['data'] = JSON.stringify(request_editor.get());
    }
    return entities;
}

$('#entity-x-www-form-urlencoded').on('change', '[data-encryption]', function () {
    var encryption = $(this).val();
    var parent = $(this).parent();
    var secret = parent.find('[data-secret]');
    if ('AES' === encryption) {
        secret.attr('type', 'text');
        secret.attr('placeholder', 'secret');
    } else {
        secret.removeAttr('placeholder');
        secret.attr('type', 'hidden');
    }
});

////////////////////////////////////////////////////////////////////////////////
//
// Verifies
//
////////////////////////////////////////////////////////////////////////////////

function loadVerifyClassFrom(verifyClass) {
    if (!verifyClass) {
        verifyClass = "com.gome.test.api.verify.JsonVerify";
    }
    $('#input-verify-class-name').val(verifyClass);
    doUpdateVerifyMenu();
}

function loadVerifyStepsFrom(verifySteps) {
    if (!verifySteps) {
        verifySteps = [];
    }
    $('#verifySteps li:not(#verify-step-template)').remove();
    for (var i = 0; i < verifySteps.length; ++i) {
        // clone verify step template
        var verifyStepDom = $('#verify-step-template').clone(true);
        verifyStepDom.removeAttr('id');
        verifyStepDom.removeAttr('class');
        var id = verifySteps.length - i;
        if (id < 10) {
            id = "0" + id;
        }
        // parse verify step
        var verifyStep = verifySteps[i];
        if (verifyStep['doc']) {
            verifyStepDom.children('div.help').text(verifyStep['doc']);
        }
        var verifyMethod = verifyStep['keyword'];
        $(verifyStepDom.children('span')[1]).html(id);
        var methodDom = $(verifyStepDom.children('input')[0]);
        methodDom.val(verifyMethod);
        var verifyParam = verifyStep['args'];
        for (var j = 0; j < verifyParam.length; ++j) {
            var paramDom = $(verifyStepDom.children('input')[1]).clone(true);
            paramDom.val(verifyParam[j]);
            $(verifyStepDom.children('span')[2]).before(paramDom);
        }
        $(verifyStepDom.children('input')[1]).remove();
        $('#verify-step-template').before(verifyStepDom);
    }
}

$('#menu-verify').on('click', 'a', function () {
    var verifyMethod = $(this).text();
    var params = null;
    for (var i = 0; i < verify_descriptors.length; ++i) {
        if (verifyMethod === verify_descriptors[i]['method']) {
            params = verify_descriptors[i]['params'];
            break;
        }
    }
    var id = $('#verifySteps').children().length;
    if (id < 10) {
        id = "0" + id;
    }
    var verifyStepDom = $('#verify-step-template').clone(true);
    verifyStepDom.removeAttr('id');
    verifyStepDom.removeAttr('class');
    $(verifyStepDom.children('span')[1]).html(id);
    var methodDom = $(verifyStepDom.children('input')[0]);
    methodDom.val(verifyMethod);
    for (var i = 0; i < params.length; ++i) {
        var paramDom = $(verifyStepDom.children('input')[1]).clone(true);
        paramDom.attr('placeholder', params[i]);
        $(verifyStepDom.children('span')[2]).before(paramDom);
    }
    $(verifyStepDom.children('input')[1]).remove();
    $('#verifySteps').prepend(verifyStepDom);
});

function getVerifyClass() {
    return $('#input-verify-class-name').val();
}

function getVerifySteps() {
    var steps = [];
    var verifySteps = $('#verifySteps li:not(#verify-step-template)');
    for (var i = 0; i < verifySteps.length; ++i) {
        var verifyStep = $(verifySteps[i]);
        var step = {};
        step['doc'] = verifyStep.find('[data-doc]').text();
        step['keyword'] = verifyStep.find('[data-keyword]').val();
        var args = [];
        var m = verifyStep.find("[data-arg]");
        for (var j = 0; j < m.length; ++j) {
            args.push($(m[j]).val());
        }
        step['args'] = args;
        steps.push(step);
    }
    return steps;
}

function doUpdateVerifyMenu() {
    $('#menu-verify').children(':not(#verify-menu-template)').remove();
    var data = {};
    data['verifyClass'] = $('#input-verify-class-name').val();
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/menu/verify/query',
        data: data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                verify_descriptors = data;
                for (var i = 0; i < data.length; ++i) {
                    var item = data[i];
                    var verifyMenuDom = $('#verify-menu-template').clone(true);
                    verifyMenuDom.removeAttr('id');
                    verifyMenuDom.removeAttr('class');
                    verifyMenuDom.find('a').attr('data-content', item['description']);
                    verifyMenuDom.find('a').text(item['method']);
                    $('#menu-verify').append(verifyMenuDom);
                }
                $('#menu-verify').perfectScrollbar();
            } else {
                $('#status').text('加载断言方法列表失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

////////////////////////////////////////////////////////////////////////////////
//
// Editor
//
////////////////////////////////////////////////////////////////////////////////

$('body').on('click', 'input[name="editor-manner"]', function () {
    if ('json' === this.value) {
        $('#editor-container').empty();
        editor = new jsoneditor.JSONEditor(
            $('#editor-container')[0],
            {
                mode: 'code',
                modes: ['code', 'tree', 'view']
            },
            {});
        try {
            editor.set(JSON.parse(edit_obj.val()));
        } catch (e) {
            var arr = [];
            arr.push(edit_obj.val());
            editor.set(arr);
        }
    } else {
        $('#editor-container').empty();
        $('#editor-container').append('<div id="text-in" style="width:100%;height:100%"></div>');
        editor = ace.edit("text-in");
        editor.setValue(edit_obj.val());
        editor.focus();
    }
});

$('body').on('click', '#btn-save-text', function () {
    var editor_manner = $('input[name="editor-manner"]:checked').val();
    if ('json' === editor_manner) {
        edit_obj.val(JSON.stringify(editor.get()));
    } else {
        edit_obj.val(editor.getValue());
    }
});

$("#entity-menu button").click(function () {
    if ('x-www-form-urlencoded' === $(this).text().trim()) {
        $('#entity-option').removeClass('hide');
        $('#entity-x-www-form-urlencoded').children(':not(#entity-template)').remove();
        $('#entity-raw').children().remove();
        $('#entity-raw').addClass('hide');
    } else {
        $('#entity-option').addClass('hide');
        $('#entity-x-www-form-urlencoded').children(':not(#entity-template)').remove();
        $('#entity-raw').children().remove();
        $('#entity-raw').removeClass('hide');
        request_editor = new jsoneditor.JSONEditor(
            $('#entity-raw')[0],
            {
                mode: 'code',
                modes: ['code', 'tree', 'view']
            }, {});
        request_editor.focus();
    }
});

////////////////////////////////////////////////////////////////////////////////
//
// TearDown
//
////////////////////////////////////////////////////////////////////////////////

function loadTearDownClassFrom(tearDownClass) {
    if (!tearDownClass) {
        tearDownClass = "com.gome.test.api.environment.EnvManager";
    }
    $('#input-teardown-class-name').val(tearDownClass);
    doUpdateTearDownMenu();
}

function loadTearDownStepsFrom(tearDownSteps) {
    if (!tearDownSteps) {
        tearDownSteps = [];
    }
    $('#tearDownSteps li:not(#teardown-step-template)').remove();
    for (var i = 0; i < tearDownSteps.length; ++i) {
        // clone teardown step template
        var tearDownStepDom = $('#teardown-step-template').clone(true);
        tearDownStepDom.removeAttr('id');
        tearDownStepDom.removeAttr('class');
        var id = tearDownSteps.length - i;
        if (id < 10) {
            id = "0" + id;
        }
        // parse teardown step
        var tearDownStep = tearDownSteps[i];
        if (tearDownStep['doc']) {
            tearDownStepDom.children('div.help').text(tearDownStep['doc']);
        }
        var tearDownMethod = tearDownStep['keyword'];
        $(tearDownStepDom.children('span')[1]).html(id);
        var methodDom = $(tearDownStepDom.children('input')[0]);
        methodDom.val(tearDownMethod);
        var tearDownParam = tearDownStep['args'];
        for (var j = 0; j < tearDownParam.length; ++j) {
            var paramDom = $(tearDownStepDom.children('input')[1]).clone(true);
            paramDom.val(tearDownParam[j]);
            $(tearDownStepDom.children('span')[2]).before(paramDom);
        }
        $(tearDownStepDom.children('input')[1]).remove();
        $('#teardown-step-template').before(tearDownStepDom);
    }
}

$('#menu-teardown').on('click', 'a', function () {
    var tearDownMethod = $(this).text();
    var params = null;
    for (var i = 0; i < teardown_descriptors.length; ++i) {
        if (tearDownMethod === teardown_descriptors[i]['method']) {
            params = teardown_descriptors[i]['params'];
            break;
        }
    }
    var id = $('#tearDownSteps').children().length;
    if (id < 10) {
        id = "0" + id;
    }
    var tearDownStepDom = $('#teardown-step-template').clone(true);
    tearDownStepDom.removeAttr('id');
    tearDownStepDom.removeAttr('class');
    $(tearDownStepDom.children('span')[1]).html(id);
    var methodDom = $(tearDownStepDom.children('input')[0]);
    methodDom.val(tearDownMethod);
    for (var i = 0; i < params.length; ++i) {
        var paramDom = $(tearDownStepDom.children('input')[1]).clone(true);
        paramDom.attr('placeholder', params[i]);
        $(tearDownStepDom.children('span')[2]).before(paramDom);
    }
    $(tearDownStepDom.children('input')[1]).remove();
    $('#tearDownSteps').prepend(tearDownStepDom);
});

function getTearDownClass() {
    return $('#input-teardown-class-name').val();
}

function getTearDownSteps() {
    var steps = [];
    var tearDownSteps = $('#tearDownSteps li:not(#teardown-step-template)');
    for (var i = 0; i < tearDownSteps.length; ++i) {
        var tearDownStep = $(tearDownSteps[i]);
        var step = {};
        step['doc'] = tearDownStep.find('[data-doc]').text();
        step['keyword'] = tearDownStep.find('[data-keyword]').val();
        var args = [];
        var m = tearDownStep.find("[data-arg]");
        for (var j = 0; j < m.length; ++j) {
            args.push($(m[j]).val());
        }
        step['args'] = args;
        steps.push(step);
    }
    return steps;
}

function doUpdateTearDownMenu() {
    $('#menu-teardown').children(':not(#teardown-menu-template)').remove();
    var data = {};
    data['tearDownClass'] = $('#input-teardown-class-name').val();
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/menu/teardown/query',
        data: data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                teardown_descriptors = data;
                for (var i = 0; i < data.length; ++i) {
                    var item = data[i];
                    var tearDownMenuDom = $('#teardown-menu-template').clone(true);
                    tearDownMenuDom.removeAttr('id');
                    tearDownMenuDom.removeAttr('class');
                    tearDownMenuDom.find('a').attr('data-content', item['description']);
                    tearDownMenuDom.find('a').text(item['method']);
                    $('#menu-teardown').append(tearDownMenuDom);
                }
                $('#menu-teardown').perfectScrollbar();
            } else {
                $('#status').text('加载TearDown方法列表失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

$('#menu-delete-node').on('click', function () {
    $('#modal-delete-node').modal('show');
});


$('body').on('click', '#btn-delete-node', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var url = null;
        if ('case' === node.type) {
            url = 'workbench/case/delete';
        } else if ('ordercase' === node.type) {
            url = 'workbench/order/case/delete';
        } else {
            url = 'workbench/tree/node/delete';
        }
        if ('suite' === node.type) {
            if (1 === node.parent.children.length) {
                node = node.parent;
            }
        }
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            data: {'node': node.id},
            success: function (response) {
                if (!response['isError']) {
                    if (node.parent.parent) {
                        $('#case-tree-data').tree('selectNode', node.parent);
                    } else {
                        selectOther();
                    }
                    $('#case-tree-data').tree('removeNode', node);
                    $('#modal-loading').modal('hide');
                } else {
                    $('#modal-loading').modal('hide');
                    $('#status').text('删除用例失败：' + response['message']);
                    $('#modal-status').modal('show');
                }
            }
        });
    }
});


///////////////////////////////////////////////////////////////
//
//code for suites-for-dev and comments stuff    added by zonglin.li
//
///////////////////////////////////////////////////////////////

function delCommentsFromJson(jsonTree) {
    $.each(jsonTree, function (i) {
        var key = i;
        var value = jsonTree[i];
        if (typeof value === "object") {
            return delCommentsFromJson(value);
        } else if (regDelKeyWord.test(i)) {
            delete jsonTree[i];
        }
    });
    return jsonTree;
}

function delCommentsColumn(jsonTree) {
    delete jsonTree['comments'];
    return jsonTree;
}


function getPureCaseInfo() {

    var pureHeaders = getPureHeaders();
    var pureUrlParams = getPureUrlParams();
    var pureEntities = getPureEntities();
    var pureHttpRequest = getHttpRequest();
    var pureData = getCaseInfo();

    pureHttpRequest['headers'] = pureHeaders;
    pureHttpRequest['urlParams'] = pureUrlParams;
    pureHttpRequest['entities'] = pureEntities;
    pureData['httpRequest'] = pureHttpRequest;

    return pureData;
}

function getPureHeaders() {//get from front
    var headers = [];
    var headerList = $('#headers').children(':not(#header-template)');
    for (var i = 0; i < headerList.length; ++i) {
        var headerDom = headerList[i];
        var key = $(headerDom).find('[data-key]').val();
        var value = $(headerDom).find('[data-value]').val();
        $.trim(value);
        if (("{" === value.charAt(0)) && ("}" === value.charAt(value.length - 1))) {
            var valueJson = JSON.parse(value);
            var pureValueJson = delCommentsFromJson(delCommentsColumn(valueJson));
            value = JSON.stringify(pureValueJson);
        }
        var encryption = $(headerDom).find('[data-encryption]').val();
        var secret = null;
        if ('AES' === encryption) {
            secret = $(headerDom).find('[data-secret]').val();
        }
        var doc = $(headerDom).find('[data-doc]').text();
        var header = {};
        header['key'] = key;
        header['value'] = value;
        header['encryption'] = encryption;
        header['secret'] = secret;
        header['doc'] = doc;
        headers.push(header);
    }
    return headers;
}

function getPureUrlParams() {
    var urlParams = [];
    var urlParamList = $('#url-params').children(':not(#url-param-template)');
    for (var i = 0; i < urlParamList.length; ++i) {
        var urlParamDom = urlParamList[i];
        var key = $(urlParamDom).find('[data-key]').val();
        var value = $(urlParamDom).find('[data-value]').val();
        var pureValue = value.trim();
        if ("{" === value.charAt(0) && "}" === value.charAt(value.length - 1)) {
            var valueJson = JSON.parse(value);
            var pureValueJson = delCommentsFromJson(delCommentsColumn(valueJson));
            ;
            pureValue = JSON.stringify(pureValueJson);
        }
        var encryption = $(urlParamDom).find('[data-encryption]').val();
        var secret = null;
        if ('AES' === encryption) {
            secret = $(urlParamDom).find('[data-secret]').val();
        }
        var doc = $(urlParamDom).find('[data-doc]').text();
        var urlParam = {};
        urlParam['key'] = key;
        urlParam['value'] = pureValue;
        urlParam['encryption'] = encryption;
        urlParam['secret'] = secret;
        urlParam['doc'] = doc;
        urlParams.push(urlParam);
    }
    return urlParams;
}

function getPureEntities() {
    var entities = {};
    entities['type'] = $('#entity-menu button[class*="active"]').text().trim();
    if ('x-www-form-urlencoded' === entities['type']) {
        var data = [];
        var entityList = $('#entity-x-www-form-urlencoded').children(':not(#entity-template)');
        for (var i = 0; i < entityList.length; ++i) {
            var entityDom = entityList[i];
            var key = $(entityDom).find('[data-key]').val();
            var value = $(entityDom).find('[data-value]').val();
            var pureValue = value.trim();
            if ("{" === value.charAt(0) && "}" === value.charAt(value.length - 1)) {
                var valueJson = JSON.parse(value);
                var pureValueJson = delCommentsFromJson(delCommentsColumn(valueJson));
                ;
                pureValue = JSON.stringify(pureValueJson);
            }
            var encryption = $(entityDom).find('[data-encryption]').val();
            var secret = null;
            if ('AES' === encryption) {
                secret = $(entityDom).find('[data-secret]').val();
            }
            var doc = $(entityDom).find('[data-doc]').text();
            var entity = {};
            entity['key'] = key;
            entity['value'] = pureValue;
            entity['encryption'] = encryption;
            entity['secret'] = secret;
            entity['doc'] = doc;
            data.push(entity);
        }
        entities['data'] = JSON.stringify(data);
    } else {
        entities['type'] = "application/json";
        var rawJson = request_editor.get();
        var pureRawJson = delCommentsFromJson(rawJson);
        entities['data'] = JSON.stringify(pureRawJson);
    }
    return entities;
}

function loadCopyResponse() {
    var node = $('#case-tree-data').tree('getSelectedNode');
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/case/query/response',
        data: {'node': node.id},
        success: function (response) {
            if (response['isError']) {
                $('#status').text('获取本地响应失败:' + response['message']);
                $('#modal-status').modal('show');
            } else {
                var responseValue = response['data'];
                var responseCopyValue = JSON.parse(responseValue['response']);
                responseCopyEditor.set(responseCopyValue);
            }
        }
    });
//responseCopyEditor.set("test test test");
}

function hideCommentsColumns(node, itemDom) {
    var suiteName = node.parent.name.trim();
    if (suiteName.indexOf("testsuites") === 0 && suiteName.indexOf("testsuitesForDev") !== 0) {
        itemDom.find('[data-comments]').parent().hide();
    }
    if (suiteName.indexOf("p-suites-") === 0) {
        itemDom.find('[data-comments]').parent().hide();
    }
    return itemDom;
}

function showCopyResponse(node) {
    var suiteName = node.parent.name.trim();
    $('#responseSection').removeClass('hide');
    if (suiteName.indexOf("testsuitesForDev") === 0) {
        loadCopyResponse();
        $('#currentSpan').removeClass('span11');
        $('#currentSpan').addClass('span5');
        $('#currentSpan').removeClass('span11');
        $('#currentSpan').addClass('span5');
        $('#localResponse').removeClass('hide');

    } else {
        $('#currentSpan').removeClass('span5');
        $('#currentSpan').addClass('span11');
        $('#localResponse').addClass('hide');
    }
}

function showForRun(node) {
    $('#responseSection').removeClass('hide');
    $('#currentSpan').removeClass('span5');
    $('currentSpan').addClass('span11');
}