/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var isRenameHide = $('#menu-rename-node').hasClass('hide');
var isDeleteHide = $('#menu-delete-node').hasClass('hide');
var isNewHide = $('#new-node').hasClass('hide');

function loadDirTree() {
    var dirTree = $('#dir-tree-data');
    dirTree.bind('tree.select', function (event) {

    });

    dirTree.bind('tree.click', function (event) {
        event.preventDefault();
        var node = event.node;
        $('#dir-tree-data').tree('selectNode', node);
    });

    dirTree.bind('tree.open', function (event) {
        var node = event.node;
        $('#dir-tree-data').tree('selectNode', node);
    });

    dirTree.bind('tree.close', function (event) {
        var node = event.node;
        $('#dir-tree-data').tree('selectNode', node);
    });


//    双击时打开资源管理器或文件 
    dirTree.bind('tree.dblclick', function (event) {
        event.preventDefault();
        var node = event.node;
        var data = {};
        if ('folder' !== node.type) {
            $('#dir-tree-data').tree('selectNode', node);
            data['node'] = node.name;
            if ("undefined" === typeof (node.parent.id)) {
                data['parentPath'] = '';
            } else {
                data['parentPath'] = node.parent.id;
            }
            data['type'] = "file";
        } else {
            $('#dir-tree-data').tree('selectNode', node);
            data['node'] = node.id;
            data['parentPath'] = '';
            data['type'] = "folder";
        }
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: 'workbench/dirTree/node/open',
            data: data,
            success: function (response) {
                if (response['isError']) {
                    $('#modal-loading').modal('hide');
                    $('#status').text('打开文件失败：' + response['message']);
                    $('#modal-status').modal('show');
                } else {
                    $('#status').text('打开文件成功：' + response['message']);
                    $('#modal-loading').modal('hide');
                }
            }
        });
        clearSelection();
    });

    dirTree.bind('tree.contextmenu', function (event) {
        var node = event.node;
        alert(node.parent.id);
    });

    dirTree.tree({
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
                'url': 'workbench/dirTree/node/children',
                'data': data
            };
        },
        dataFilter: function (data) {
            if (data['isError']) {
                $('#status').text('加载目录树失败：' + data['message']);
                $('#modal-status').modal('show');
                return [];
            } else {
                return data['data'];
            }
        },
        autoEscape: false,
        onCreateLi: function (node, $li) {
            var e = '';
            if ('folder' === node.type) {
                e = '<span class="label label-success">打开</span><span>&nbsp;</span><span class="icon-folder-open"></span><span>&nbsp;</span>';
            } else if ('bat' === node.type || 'sh' === node.type) {
                e = '<span class="label label-info">运行</span><span>&nbsp;</span><span class="icon-file"></span><span>&nbsp;</span>';
            } else {
                e = '<span class="label label-success">打开</span><span>&nbsp;</span><span class="icon-file"></span><span>&nbsp;</span>';
            }
            $li.find('.jqtree-title').before(e);
        }
    });
    $('#main-panel-case').removeClass('active');
    $('#main-panel-workspace').addClass('active');
}

//清除选中文本
var clearSelection = function () {
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    }
    else if (window.getSelection) {
        var sel = window.getSelection();
        sel.removeAllRanges();
    }
};

$('#case-tree-tab').bind('click', function () {
    if (!isRenameHide) {
        $('#menu-rename-node').removeClass('hide');
    }
    if (!isDeleteHide) {
        $('#menu-delete-node').removeClass('hide');
    }
    if (!isNewHide) {
        $('#new-node').removeClass('hide');
    }
});

$('#dir-tree-tab').bind('click', function () {
    isRenameHide = $('#menu-rename-node').hasClass('hide');
    isDeleteHide = $('#menu-delete-node').hasClass('hide');
    isNewHide = $('#new-node').hasClass('hide');
    $('#menu-rename-node').addClass('hide');
    $('#menu-delete-node').addClass('hide');
    $('#new-node').addClass('hide');
});