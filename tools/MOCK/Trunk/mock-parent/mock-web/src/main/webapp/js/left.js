var editor = null;
var edit_obj = null;
var regDelKeyWord = /^_comments*/i;
var responseEditor = null;
var responseCopyEditor = null;

$(document).ready(function () {

    loadTree();

});



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function selectData(node)
{
    var nodeparent=node.parent;

    var url=nodeparent.url  ;
    if(node.url==null || node.url.length ==0 )
        url=url+node.value;
    else
        url=url+node.url;

    if(null!=url &&''!= url)
    {
        window.parent.frames["main"].location.href =url;
    }

}

function selectJsData(node)
{

   var url=node.parent.url+'/search';

   var searchurl= window.parent.frames["main"].document.documentURI;
    if(searchurl.indexOf(url) > 0)
    {
        window.parent.frames["main"].portDetail(node.value);
    }else
    {
        url=url+'/'+node.value;
        window.parent.frames["main"].location.href =url;
    }

}

//全部功能页面处理
function showNode(name,value)
{
    cleanPage();
    switch (name)
    {
        case "API":
            showApi(value);
    }


}



function selectclasstree(node)
{
    if(node.url.length==0)
        return false;
    var url=node.url+'/search';
    if(null!=url &&''!= url)
    {
        window.parent.frames["main"].location.href =url;
    }
}





function loadTree() {
    var caseTree = $('#case-tree-data');
    caseTree.bind('tree.select', function (event) {
        if (event.node) {

            var node = event.node;
            if ("classtree" == node.type) {
                selectclasstree(node);
            } else if ("nodetree" == node.type) {
                selectData(node);
            }else if("jsnodetree"==node.type)
            {
                selectJsData(node);
            }
        }
        else {
            //selectOther();
        }

    });

    caseTree.bind('tree.click', function (event) {
        event.preventDefault();
        var node = event.node;
        $('#case-tree-data').tree('selectNode', node);
    });


    caseTree.bind('tree.open', function (event) {
        var node = event.node;
        $('#case-tree-data').tree('selectNode', node);
    });

    caseTree.bind('tree.close', function (event) {
        var node = event.node;
        if (node ) {
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
                data['pid'] = node.id;
                data['pvalue']=node.value;
            } else {
                data['pid'] = '0';
                data['pvalue']='';
            }
            return  {

                'url': '/leftTree/data',
                'data':  data
            };
        },
        dataFilter: function (data) {
            if (data['isError']) {
                $('#status').text('加载菜单失败：' + data['message']);
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
            var f='';
            if ('classtree' === node.type) {
                //e = '<span class="icon-folder-close"></span><span>&nbsp;</span>';

                var id=node.id.split('_')[0];

            } else if ('nodetree' === node.type) {
                e = '<span class="icon-file"></span><span>&nbsp;</span>';
            }
        },
        selectable: true
    });
    $('#main-panel-workspace').removeClass('active');
    $('#main-panel-case').addClass('active');
}