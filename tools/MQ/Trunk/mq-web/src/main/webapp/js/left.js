var editor = null;
var edit_obj = null;

$(document).ready(function () {

    loadTree();

});

function openData(node)
{
    var nodeparent=node.parent;
    var url = nodeparent.url+ node.url;
    var valuelist=node.value.split(',');
    for(var i=0;i<valuelist.length;i++)
    {
        url += '/'+valuelist[i];
    }



    if(null!=url &&''!= url)
    {
        window.parent.frames["main"].location.href =url+'/log';
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function selectData(node)
{

    var nodeparent=node.parent;
    var nodeparentF=nodeparent.parent;


    var title= nodeparentF.name+' - '+ nodeparent.name;

    var tvalue=node.typeValue;
    var url=nodeparent.url +node.url ;
    var isCmd= Boolean(node.isCmd)?"1":"0" ;
    var valueList=node.value.split(',');
    url+="/"+ title +'/'+nodeparent.id;
    for(var i=0;i< valueList.length;i++)
    {
        url += "/"+ valueList[i];
    }

    if(tvalue.indexOf('Template')==0)
    {
        tvalue=tvalue.replace("Template_","");
        url +='/'+node.name+'/'+tvalue+'/'+isCmd+'/tmp';
    }else{

        url +="/"+tvalue+'/'+isCmd+'/mq';
    }


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
            if ("nodetree" == node.type) {
                selectData(node);
            }else if('nodepage'== node.type)
            {
                openData(node);
            }
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
            if ('nodetree' === node.type) {
                var typeValue = node.typeValue;
                //e = '<span style="color: red">('+typeValue+')</span>';
            }
            $li.find('.jqtree-title').after(e);
        },
        selectable: true
    });
    $('#main-panel-workspace').removeClass('active');
    $('#main-panel-case').addClass('active');
}