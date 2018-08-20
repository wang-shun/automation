

$(document).ready(function () {
    $('.ui-sortable').sortable({
        update: function (event, ui) {
            var ul = ui.item.parent();
            reOrder(ul);
        }
    });
    bandDrop();
    bandPage();
});

function bandPage()
{
    var hostType= $("#btn-save").attr("hosttype");
    $("#txt_host_domain").val($("#txt_host_domain").attr("oldvalue"));
    $("#txt_host_name").val($("#txt_host_name").attr("oldvalue"));
    $("#txt_host_url").val($("#txt_host_url").attr("oldvalue"));
    $("#drop_host_port").val($("#drop_host_port").attr("oldvalue"));
    $("#drop_host_hosttype").val($("#drop_host_hosttype").attr("oldvalue"));
    $("#drop_host_protocoltype").val($("#drop_host_protocoltype").attr("oldvalue"));
    $('#checkbox_host_enable').prop("checked",$("#checkbox_host_enable").attr("oldvalue")==1?true:false);
    if(hostType=='view')
    {
        $("#drop_host_hosttype").attr("disabled","disabled");
        $("#drop_host_protocoltype").attr("disabled","disabled");
        $("#checkbox_host_enable").attr("disabled","disabled");
        $("#drop_host_port").attr("disabled","disabled");
        $("#btn-send").addClass('hidden');
        $("#btn-save").addClass('hidden');
    }else if(hostType=='create')
    {
        $('#checkbox_host_enable').prop("checked",true);
    }
}

//绑定port
function bandPortDrop()
{
    var data=null;
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/port/getPortForDrop',
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                for (var i = 0; i < data.length; ++i) {
                    if(i==0)
                        $("#drop_host_port").append("<option value=''>--请选择--</option>");
                    var item = data[i];
                    if(item.enable==1) {
                        $("#drop_host_port").append("<option value=" + item.id + ">" + item.portNumber + "</option>");
                    }
                }
            } else {
                $('#status').text('获取post失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}
//绑定protocol_type
function bandProtocolDrop()
{
    var data=null;
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/host/getProtocolTypeForDrop',
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                for (var i = 0; i < data.length; ++i) {
                    if(i==0)
                        $("#drop_host_protocoltype").append("<option value=''>--请选择--</option>");
                    var item = data[i];
                        $("#drop_host_protocoltype").append("<option value=" + item[0] + ">" + item[1] + "</option>");
             }
            } else {
                $('#status').text('获取hostType失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}
//绑定host_type
function bandhostDrop()
{
    var data=null;
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/host/gethostTypeForDrop',
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                for (var i = 0; i < data.length; ++i) {
                    if(i==0)
                        $("#drop_host_hosttype").append("<option value=''>--请选择--</option>");
                    var item = data[i];
                        $("#drop_host_hosttype").append("<option value=" + item[0] + ">" + item[1] + "</option>");

                }
            } else {
                $('#status').text('获取hostType失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

//绑定下拉列表
function bandDrop()
{
    //绑定port
    bandPortDrop();
    //绑定host_type
    bandhostDrop();
    //绑定protocol_type
    bandProtocolDrop();

}


$('#txt_host_domain').bind('blur', function () {
    checkHostName();
})

$('#txt_host_url').bind('blur', function () {
    checkHostName();
})

$('#txt_host_name').bind('blur', function () {
    checkHostName();
})

// 新增、修改按钮事件绑定
$('#btn-save').bind('click', function () {
    var hostType= $("#btn-save").attr("hosttype");
    var host_idStr= $("#btn-save").attr('hostid');
    var url='/host/save';
    var host_id=0;
    if(hostType=='edit')
    {
        url='/host/edit';
        host_id= parseInt(host_idStr);
    }
    var host_name= $("#txt_host_name").val().trim() ;
    var host_domain=$("#txt_host_domain").val().trim() ;
    var host_url=$("#txt_host_url").val().trim() ;
    var host_port=$("#drop_host_port").val();
    var host_hostType=$("#drop_host_hosttype").val();
    var host_protocoltype=$("#drop_host_protocoltype").val();
    var enable=$("#checkbox_host_enable").is(":checked")

    if($.trim(host_name).length==0)
    {
        $('#status').text('请输入服务名称！ ');
        $('#modal-status').modal('show');
        return false;
    }
    if($.trim(host_domain).length==0)
    {
        $('#status').text('请输入域名！ ');
        $('#modal-status').modal('show');
        return false;
    }
    if($.trim(host_url).length==0)
    {
        $('#status').text('请输入url！ ');
        $('#modal-status').modal('show');
        return false;
    }
    if(host_hostType==''|| host_port==''|| host_protocoltype=='')
    {
        $('#status').text('请输入选择全部内容 ');
        $('#modal-status').modal('show');
        return false;
    }
    var data={};
    data['host_name'] = host_name;
    data['host_domain'] = host_domain;
    data['host_url']=host_url;
    data['host_port']=host_port;
    data['enabel']=enable?"1":"0";
    data['host_hostType']=host_hostType;
    data['host_protocoltype']=host_protocoltype;
    data['host_id']=host_id;

   if(!checkHostName())
        return false;


    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: url,
        data:data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                $('#status').text((host_id==0?'新增':'修改')+'服务成功！');
                $('#modal-status').modal('show');

            } else {
                $('#status').text('保存服务失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });

});

//验证服务名称是否已经存在
function checkHostName()
{
    var result=false;
    var host_name= $("#txt_host_name").val().trim() ;
    var host_domain=$("#txt_host_domain").val().trim() ;
    var host_url=$("#txt_host_url").val().trim() ;
    var host_id= $("#btn-save").attr('hostid');
    if( host_name =='' || host_domain=='' ||host_url=='' || host_id=='')
    {
        return false ;
    }

    var hostid = (host_id.length==0)?0: parseInt(host_id);
    var data={};
    data["host_name"]=host_name;
    data["host_domain"]=host_domain;
    data["host_url"]=host_url;
    data["host_id"]=hostid;
    var oldname=$("#txt_host_name").attr("oldvalue");
    var olddomain=$("#txt_host_domain").attr("oldvalue");
    var oldurl=$("#txt_host_url").attr("oldvalue");
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/host/checkName',
        data:data,
        success: function (response) {
            if (response['isError']) {
                data=response['data'];

                if(data!=null)
                {
                    switch (data)
                    {
                        case 2:
                            $('#status').text('已经存在服务：'+host_name);
                            $("#txt_host_name").val($("#txt_host_name").attr("oldvalue"));
                            break;
                        case 3:
                            $('#status').text('已经存在域名：'+host_domain);
                            $("#txt_host_domain").val($("#txt_host_domain").attr("oldvalue"));
                            break;
                        case 4:
                            $('#status').text('已经存在Url：'+host_url);
                            $("#txt_host_url").val($("#txt_host_url").attr("oldvalue"));
                            break;
                        case 1:
                            $('#status').text('已经存在服务：'+host_name+' 域名：'+host_domain+' url：'+host_url);
                            $("#txt_host_domain").val($("#txt_host_domain").attr("oldvalue"));
                            $("#txt_host_name").val($("#txt_host_name").attr("oldvalue"));
                            $("#txt_host_url").val($("#txt_host_url").attr("oldvalue"));
                            break;
                    }
                }
                $('#modal-status').modal('show');
            }else
                result=true;
        }
    });

    return result;
}

//放弃保存API按钮
$('#btn-send').bind('click', function () {
    cleanPage();
});



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
                $("#api_keyword").val(data.keyWords);
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
