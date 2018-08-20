

$(document).ready(function () {
    $('.ui-sortable').sortable({
        update: function (event, ui) {
            var ul = ui.item.parent();
            reOrder(ul);
        }
    });
    bandHostDrop();
    bandTemplateDrop();
    var apiid= $("#btn-save-api").attr('apiid');
    var apitype= $("#btn-save-api").attr("apitype");
    switch (apitype)
    {
        case "create":
            break;
        case "edit":
            getApi(apiid);
            break;
        case "view":
            getApi(apiid);
            $("#checkbox_api_enable").attr("disabled","disabled");
            $("#drop_api_host").attr("disabled","disabled");
            $("#btn-send-api").addClass('hidden');
            $("#btn-save-api").addClass('hidden');
            break;
    }
});


//绑定host
function bandHostDrop()
{
    var data=null;

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/host/getHostForDrop',
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];

                for (var i = 0; i < data.length; ++i) {
                    var item = data[i];
                    if(item.enable==1) {
                        $("#drop_api_host").append("<option value=" + item.id + ">" + item.serviceName+"【 domain: "+item.domain+" --  url :"+item.url+" 】" + "</option>");
                    }
                }
            } else {
                $('#status').text('加载断言方法列表失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

//绑定Template
function bandTemplateDrop()
{
    var data=null;

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/template/getTemplateForDrop',
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];

                for (var i = 0; i < data.length; ++i) {
                    var item = data[i];
                    if(item.enable==1) {
                        $("#drop_api_template").append("<option value=" + item.id + ">[ID:"+item.id+"]" + item.templateName + "</option>");
                    }
                }
            } else {
                $('#status').text('加载断言方法列表失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

//api 新增、修改按钮事件绑定
$('#btn-save-api').bind('click', function () {
    var apiidStr= $("#btn-send-api").attr('apiid');
    var url='/api/save';
    var api_id=0;
    if(apiidStr != null && apiidStr != undefined && apiidStr.length> 0)
    {
        url='/api/edit';
        api_id= parseInt(apiidStr);
    }
    var api_name=$("#txt_api_name").val();
    var host_id=$("#drop_api_host").val();
    var template_id=$("#drop_api_template").val();
    var key_words=$("#api_keyword").val();
    var intercept_param=$("#api_intercept_param").val();
    var descript=$("#api_descript").val();
    var enable=$("#checkbox_api_enable").is(":checked")

    if($.trim(api_name).length==0)
    {
        $('#status').text('请输入服务名称！ ');
        $('#modal-status').modal('show');
        return false;
    }
    if($.trim(key_words).length==0)
    {
        $('#status').text('请输入关键字！ ');
        $('#modal-status').modal('show');
        return false;
    }
    var data={};
    data['api_name'] = api_name;
    data['host_id'] = host_id;
    data['template_id'] = template_id;
    data['key_words']=key_words;
    data['descript']=descript;
    data['intercept_param']=intercept_param;
    data['enabel']=enable?"1":"0";
    data['api_id']=api_id;
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: url,
        data:data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                $('#status').text((api_id==0?'新增':'修改')+'api服务成功！');
                $('#modal-status').modal('show');

            } else {
                $('#status').text('保存api服务失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });

});

//验证服务名称是否已经存在
function checkApiName()
{
    var api_name= $("#txt_api_name").val().trim() ;
    var apiidStr= $("#btn-save-api").attr('apiid');
    if(api_name =='' )
    {
        return false ;
    }

    var apiid = (apiidStr.length==0)?0: parseInt(apiidStr);
    var data={};
    data["apiname"]=api_name;
    data["api_id"]=apiid;
    var oldname=$("#txt_api_name").attr("api_name");

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/api/checkName',
        data:data,
        success: function (response) {
            if (response['isError']) {
                $('#status').text('已经存在服务：'+api_name);
                $('#modal-status').modal('show');
                $("#txt_api_name").val(oldname);
            }
        }
    });
}

//放弃保存API按钮
$('#btn-send-api').bind('click', function () {
    cleanApi();
});

$('#txt_api_name').bind('blur', function () {
    checkApiName();
})

$('body').on('click', '#iconi_keyword', function () {
    $('#modal-editor').modal('show');
    edit_obj = $('#api_keyword');
    try {
        JSON.parse(edit_obj.val());
        $('input[name="editor-manner"][value="json"]').click();
    } catch (e) {
        $('input[name="editor-manner"][value="text"]').click();
    }
});
var edit_obj;
$('body').on('click', '#iconi_descript', function () {
    $('#modal-editor').modal('show');
    edit_obj = $('#api_descript');
    try {
        JSON.parse(edit_obj.val());
        $('input[name="editor-manner"][value="json"]').click();
    } catch (e) {
        $('input[name="editor-manner"][value="text"]').click();
    }
});

$('body').on('click', '#iconi_intercept_param', function () {
    $('#modal-editor').modal('show');
    edit_obj = $('#api_intercept_param');
    try {
        JSON.parse(edit_obj.val());
        $('input[name="editor-manner"][value="json"]').click();
    } catch (e) {
        $('input[name="editor-manner"][value="text"]').click();
    }
});

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

function getApi(id)
{

    var data={"api_id":id};
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/api/get',
        data:data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                $("#txt_api_name").val(data.apiName);
                $("#txt_api_name").attr("api_name",data.apiName);
                $("#drop_api_host").val(data.hostId);
                $("#drop_api_template").val(data.templateId);
                $("#api_keyword").val(data.keyWords);
                $("#api_intercept_param").val(data.interceptParam);
                $("#api_descript").val(data.descript);
                $('#checkbox_api_enable').prop("checked",data.enable==1?true:false);
                $("#btn-send-api").attr("apiid",id);

            } else {
                $('#status').text('获取api服务失败：' + response['message']);
                $('#modal-status').modal('show');

            }
        }
    });

}
