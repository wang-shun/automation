/**
 * Created by Administrator on 2015/6/3.
 */
var treeData = null;
var treeNode=null;

$(document).ready(function () {

    loadHelpTree();

   // window.parent.frames["main"].location.href = 'http://www.baidu.com';
})


function loadHelpTree() {
    var caseTree = $('#helper-tree-data');
    caseTree.bind('tree.select', function (event) {
        if (event.node) {
            var node = event.node;

            if ("classtree" == node.type) {
                selectclasstree(node);
            } else if ("methodtree" == node.type) {
                selectData(node);
            }
        } else {
            selectOther();
        }
    });

    caseTree.bind('tree.click', function (event) {
        event.preventDefault();
        var node = event.node;
        $('#helper-tree-data').tree('selectNode', node);


    });

    caseTree.bind('tree.move', function (event) {
        event.preventDefault();
        return false;
    });
    caseTree.bind('tree.open', function (event) {
        var node = event.node;
        $('#case-tree-data').tree('selectNode', node);
    });

    caseTree.bind('tree.close', function (event) {
        var node = event.node;
        if (node ) { //&& 'methodtree' !== node.type
            node.load_on_demand = true;
        }
        $('#case-tree-data').tree('selectNode', node);
    });

    caseTree.bind('tree.dblclick', function (event) {
        var node = event.node;

        //$('#helper-tree-data').tree('openNode', node);
        $('#helper-tree-data').tree('toggle', node);
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
            if(treeNode!=null)
            {
                return {'data':treeNode}
            }
            return  {

                'url': '/leftTree/data',
                'data':  data
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
        autoEscape: false,
        onCreateLi: function (node, $li) {
            var e = '';
            var f='';
            if ('classtree' === node.type) {
                //e = '<span class="icon-folder-close"></span><span>&nbsp;</span>';

                var id=node.id.split('_')[0];

            } else if ('methodtree' === node.type) {
                e = '<span class="icon-file"></span><span>&nbsp;</span>';
            }

            //$li.find('.jqtree-title').before("("+node.id+")");
        },
        selectable: true

    });



    function selectData(node)
    {
        var url='';
        var nodeType='';
        var nodeP=node;
        while(nodeType!='classtree')
        {
            nodeP = nodeP.parent;
            nodeType=nodeP.type;
            url=nodeP.url;
        }

        url= url!=''?(url+"/search/"+node.id) :node.url ;


        if(null!=url &&''!= url)
        {
            window.parent.frames["main"].location.href =url;
        }
    }



    function selectclasstree(node)
    {
        if(null!=node.url &&''!= node.url)
        {
            window.parent.frames["main"].location.href =node.url;
        }



    }

}