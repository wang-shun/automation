/*
 * 新建一个模板suite：p-suites-tpl-${suitename}
 */
$('body').on('click', '#btn-new-p-suite-tempalte', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#p-testsuites-template').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/psuitetpl/add',
                data: {'node': node.id, 'suite': name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'p-suites-tpl-' + name,
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
/*
 * 新建一个suite：p-suites-${suitename}
 * 需要与模板suite成对出现
 */
$('body').on('click', '#btn-new-p-suite', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#p-testsuites').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/psuite/add',
                data: {'node': node.id, 'suite': name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'p-suites-' + name,
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
/*
 * 修改p-suites-tpl-${name}的名字
 * 
 */
$('body').on('click', '#btn-rename-psuitetpl', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    var oldSuiteName = node.name;
    oldSuiteName = oldSuiteName.substring(13);
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#oldPSuiteTplName').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/psuitetpl/update',
                data: {'node': node.id, 'suite': oldSuiteName, newsuite: name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'p-suites-tpl-' + name,
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

/*
 * 修改p-suites-${name}的名字
 * 
 */
$('body').on('click', '#btn-rename-psuite', function () {
    var node = $('#case-tree-data').tree('getSelectedNode');
    var oldSuiteName = node.name;
    oldSuiteName = oldSuiteName.substring(13);
    if (node) {
        $('#modal-loading').modal('show');
        var name = $('#oldPSuiteName').val().trim();
        if (name === '') {
            $('#modal-loading').modal('hide');
            $('#status').text('suite名称不能为空');
            $('#modal-status').modal('show');
        } else {
            $.ajax({
                type: 'POST',
                url: 'workbench/tree/node/psuite/update',
                data: {'node': node.id, 'suite': oldSuiteName, newsuite: name},
                success: function (response) {
                    if (!response['isError']) {
                        data = response['data'];
                        var newNode = {
                            label: 'p-suites-' + name,
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

////////////////////////////////////////////////////////////////////////////////
//
// Variables
//
////////////////////////////////////////////////////////////////////////////////

$('#btn-add-variable').on('click', doUpdateVariableMenu);

function doUpdateVariableMenu() {
    var id = $('#variables-writable').children().length;
    if (id < 10) {
        id = "0" + id;
    }
    var variableDom = $('#variables-writable-template').clone(true);
    variableDom.removeAttr('id');
    variableDom.removeAttr('class');
    $(variableDom.children('span')[1]).html(id);
    var methodDom = $(variableDom.children('input')[0]);
    methodDom.val("");
    $(variableDom.children('input')[1]).remove();
    $(variableDom.children(".input-append").addClass("hide"));
    $(variableDom.children(".icon-remove").removeClass("hide"));
    $('#variables-writable-template').before(variableDom);
}

function getWritableVariableMenu() {
    var varbriableliDom;
    var varbriablesliDom = $('#variables-writable li:not(#variables-writable-template)');
    var caseVariables = [];
    for (var i = 0; i < varbriablesliDom.length; ++i) {
        varbriableliDom = $(varbriablesliDom[i]);
        var caseVariable = {};
        caseVariable['key'] = $(varbriableliDom).find('[data-key]').val();
        caseVariable['value'] = $(varbriableliDom).find('[data-value]').val();
        caseVariables.push(caseVariable);
    }
    return caseVariables;
}
function loadCaseVariablesFrom(caseVariables, nodeId) {
    if (!caseVariables) {
        caseVariables = [];
    }
    //load to variables-writable section
    $('#variables-writable li:not(#variables-writable-template)').remove();
    var size = caseVariables.length;
    for (var i = 0; i < size; i++) {
        var caseVariable = caseVariables[i];
        var key = caseVariable['key'];
        var value = caseVariable['value'];
        var variableDom = $('#variables-writable-template').clone(true);
        variableDom.removeAttr('id');
        variableDom.removeAttr('class');
        if ((nodeId.match('p-suites-tpl')) !== null) {
            $(variableDom.children(".input-append").addClass("hide"));
            $(variableDom.children(".icon-remove").removeClass("hide"));
            $('#btn-add-variable').removeClass("hide");
        } else {
            $(variableDom.children(".input-append").removeClass("hide"));
            $(variableDom.children(".icon-remove").addClass("hide"));
            $('#btn-add-variable').addClass("hide");
        }
        var id = size - i;
        if (id < 10) {
            id = "0" + id;
        }
        $(variableDom.children('span')[1]).html(id);
        var methodDom = $(variableDom.find('[data-key]'));
        methodDom.val(key);
        var methodDom = $(variableDom.find('[data-value]'));
        methodDom.val(value);
        $('#variables-writable-template').before(variableDom);
    }
}

////////////////////////////////////////////////////////////////////////////////
//
// p-cases
//
////////////////////////////////////////////////////////////////////////////////

function doLoadPCases(nodeId) {
    var pcases;
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: 'workbench/case/p/query',
        data: {'node': nodeId},
        success: function (response) {
            if (response['isError']) {
                $('#status').text('加载p-cases列表失败: ' + response['message']);
                $('#modal-status').modal('show');
            } else {
                pcases = response['data'];
            }
        }
    });
    if (!pcases) {
        pcases = [];
    }
    $('#p-cases tr:not(#tr-template)').remove();
    var size = pcases.length;
    var header = [];
    var headerDom = '';
    headerDom = '<tr><th>#</th><th>caseId</th><th>caseName</th>';
    var id;
    for (var i = 0; i < size; i++) {
        var pcase = pcases[i];
        var caseId = pcase['id'];
        if (i < 10) {
            id = "0" + i;
        }
        var caseName = pcase['name'];
        var valuesDom = '';
        valuesDom = '<tr><td>' + id + '</td><td>' + caseId + '</td><td>' + caseName + '</td>';
        var caseVariables = pcase['caseVariables'];
        if (i === 0) {
            for (var j = 0; j < caseVariables.length; j++) {
                var key = caseVariables[j]['key'];
                var value = caseVariables[j]['value'];
                headerDom = headerDom + '<th>' + key + '</th>';
                header.push(key);
                valuesDom = valuesDom + '<td>' + value + '</td>';
            }
            headerDom = headerDom + '</tr>';
            valuesDom = valuesDom + '</tr>';
            $('#tr-template').before(headerDom);
            $('#tr-template').after(valuesDom);
        } else {
            for (var j = 0; j < caseVariables.length; j++) {
                var value = caseVariables[j]['value'];
                valuesDom = valuesDom + '<td>' + value + '</td>';
            }
            valuesDom = valuesDom + '</tr>';
            $('#tr-template').before(valuesDom);
        }
    }
}