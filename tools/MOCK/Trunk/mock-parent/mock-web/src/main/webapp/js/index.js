var editor = null;
var edit_obj = null;
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
    loadTree();
    bandHostDrop();
    //bandApiTable();
    //doUpdateSetUpMenu();
    //doUpdateVerifyMenu();
    //doUpdateTearDownMenu();
    //doShowSetUpSteps();
    //doShowVerify();
    //doTearDownSteps();
});

//拖拽div排序展示
function reOrder(ul) {
    var lis = $(ul).children(':not(.hide)');
    for (var i = 0; i < lis.length; ++i) {
        var item = $(lis[i]);
        var id = i +1 ;
        if (id < 10) {
            id = "0" + id;
        }
        $(item.children('span')[1]).html(id);
    }
}





//初始化API功能页面
function cleanApi()
{
    //$("#txt_api_name").val('');
    //$('#drop_api_host').get(0).selectedIndex=0;
    //$("#api_keyword").val('');
    //$("#api_descript").val('');
    //$('#checkbox_api_enable').prop("checked",true);
    //$("#btn-send-api").attr("api_id","");
    //$("#btn-send-api").removeClass('hidden');
    //$("#btn-save-api").removeClass('hidden');
    //$("#api-section").addClass('hidden');

    window.history.go(-1);
}

function cleanApiSerch()
{
    $("#api_table_div").removeClass('hidden');
}

function bandApiTable()
{
    $('#api_list').DataTable({
        "bLengthChange": true,
        "processing": false,
        ajax: {
            "url": "/api/all",
            "type": "POST"
        },
        columns: [
            {data: [0]},
            {data: [1]},
            {data: [2]},
            {data: [3]},
            {data: [4]},
            {data: [5]}
        ]

    });
}



//api功能页面处理
function showApi(value)
{
    cleanApi();
    var valueList=value.split('_');
    var id=valueList[1];
    if(valueList[0]=='show')//查看
    {
        //$("#btn-send-api").addClass('hidden');
        //$("#btn-save-api").addClass('hidden');
        //得到api对应的数据
        getApi(id);
        //$("#btn-send-api").attr("api_id",'');

    }else if(valueList[0]=='create')//新建
    {
        $("#api-section").removeClass('hidden');
    }else if(valueList[0]=='edit') {//修改
        showApi(id);
        $("#api-section").removeClass('hidden');
    }
}



function cleanPage()
{
    cleanApi();
    cleanApiSerch();
}


$('body').on('click', '.icon-th', function (e) {
    var help = $(this).parent().children('div.help');
    help.toggle();
    help.focus();
});

$('body').on('mouseover', '.icon-th', function () {
    this.style.cursor = 'pointer';
});



$('body').on('click', '.icon-remove', function () {
    var item = $(this).parent();
    var parent = item.parent();
    item.remove();
    reOrder(parent);
});

$('body').on('click', '.category-remove', function() {
    var item = $(this).parent();
    var parent = item.parent();
    item.remove();
});

$('#btn-headers').bind('click', function () {
    $(this).button('toggle');
});

$('#btn-url-params').bind('click', function () {
    $(this).button('toggle');
});

$('#btn-verifies').bind('click', function () {
    $(this).button('toggle');
    $("#verify_search").val('');
});

$('#btn-teardown').bind('click', function () {
    $(this).button('toggle');
    $("#dropdown_search").val('');
});

$('#btn-setup').bind('click', function () {
    $(this).button('toggle');
    $("#setup_search").val('');
});

$('#btn-category').bind('click', function () {
    $(this).button('toggle');

});

