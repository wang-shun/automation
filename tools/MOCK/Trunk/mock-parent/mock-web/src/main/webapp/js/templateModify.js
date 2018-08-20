

$(document).ready(function () {
    $('.ui-sortable').sortable({
        update: function (event, ui) {
            var ul = ui.item.parent();
            reOrder(ul);
        }
    });
    var templateId= $("#btn-save-template").attr('templateId');
    var templateType= $("#btn-save-template").attr("templateType");
    switch (templateType)
    {
        case "create":
            break;
        case "edit":
            getTemplate(templateId);
            break;
        case "view":
            getTemplate(templateId);
            $("#checkbox_template_enable").attr("disabled","disabled");
            $("#btn-send-template").addClass('hidden');
            $("#btn-save-template").addClass('hidden');
            break;
    }
});


//新增、修改按钮事件绑定
$('#btn-save-template').bind('click', function () {
    var templateIdStr= $("#btn-send-template").attr('templateId');
    var url='/template/save';
    var template_id=0;
    if(templateIdStr != null && templateIdStr != undefined && templateIdStr.length> 0)
    {
        url='/template/edit';
        template_id= parseInt(templateIdStr);
    }
    var template_name=$("#txt_template_name").val();
    var template_content=$("#txt_template_content").val();
    var enable=$("#checkbox_template_enable").is(":checked")

    if($.trim(template_name).length==0)
    {
        $('#status').text('请输入模板名称！ ');
        $('#modal-status').modal('show');
        return false;
    }
    if($.trim(template_content).length==0)
    {
        $('#status').text('请输入模板内容！ ');
        $('#modal-status').modal('show');
        return false;
    }
    var data={};
    data['template_id'] = template_id;
    data['template_name'] = template_name;
    data['template_content']=template_content;
    data['enable']=enable?"1":"0";
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: url,
        data:data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                $('#status').text((template_id==0?'新增':'修改')+'模板成功！');
                $('#modal-status').modal('show');

            } else {
                $('#status').text('保存模板失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });

});

//放弃保存Template按钮
$('#btn-send-template').bind('click', function () {
	window.history.go(-1);
});

$('body').on('click', '#iconi_keyword', function () {
    $('#modal-editor').modal('show');
    edit_obj = $('#template_keyword');
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
    edit_obj = $('#template_descript');
    try {
        JSON.parse(edit_obj.val());
        $('input[name="editor-manner"][value="json"]').click();
    } catch (e) {
        $('input[name="editor-manner"][value="text"]').click();
    }
});

$('body').on('click', '#iconi_intercept_param', function () {
    $('#modal-editor').modal('show');
    edit_obj = $('#template_intercept_param');
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

function getTemplate(id)
{
    var data={"template_id":id};
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/template/get',
        data:data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                $("#txt_template_name").val(data.templateName);
                $("#txt_template_content").val(data.templateContent);
                $('#checkbox_template_enable').prop("checked",data.enable==1?true:false);
                $("#btn-send-template").attr("templateId",id);

            } else {
                $('#status').text('获取模板失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });

}
