
var step_descriptors = null;
$(document).ready(function () {
    //绑定 flowContent
    bandFlowContent();
    //加载Steps相关的所有方法
    doUpdateStepsMenu();
    //显示Steps相关内容
    doShowSteps();
    $('.ui-sortable').sortable({
        update: function (event, ui) {
            var ul = ui.item.parent();
            reOrder(ul);
        }
    });

});

$("#input-step-class-name").bind('blur',function () {
    var oldvalue=$("#input-step-class-name").attr('oldvalue');
    var value=$("#input-step-class-name").val();
    if(value.length>0 && oldvalue != value)
    {
        doUpdateStepsMenu();
        doShowSteps();
        $("#input-step-class-name").attr("oldvalue",value);

    }
});

//加载Steps相关的所有方法
function doUpdateStepsMenu() {
    $('#menu-setup').children(':not(#setup-menu-template)').remove();
    var data = {};
    data['stepClass'] = $('#input-step-class-name').val();
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/api/workflow/query',
        data: data,
        success: function (response) {
            if (response['isError']) {
                $('#status').text('加载Step方法列表失败: ' + response['message']);
                $('#modal-status').modal('show');
            } else {
                data = response['data'];
                step_descriptors = data;
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


function doShowSteps() {

    if (step_descriptors == null)
        return;

    $('#setup_search').typeahead({
        //绑定数据
        source: function (query, process) {
            var results = _.map(step_descriptors, function (item) {
                return item["method"];
            });
            process(results);
        },
        //匹配
        matcher: function (item) {
            return ~item.toLowerCase().indexOf(this.query.toLowerCase())
        },
        //显示
        highlighter: function (item) {

            var product = _.find(step_descriptors, function (it) {
                return it["method"] == item;
            });
            return "<div><strong>" + product["method"] + "</strong></div><div style='white-space:normal; width:500px;'><small>" + product["description"] + "</small></div>";

        },

        //选中
        updater: function (item) {
            selectSteps(item);
            return "";
        },
        //显示行数
        items: 5
    });
}

$("#a_details").bind('click',function(){
    $('#api_detail_modal').modal('show');
})

$('#btn-save-api').bind('click', function () {

    var Steps = $('#setSteps li:not(#setup-step-template)');
    if(Steps.length ==0 )
    {
        $('#status').text('请选择工作步骤！');
        $('#modal-status').modal('show');
        return false;
    }
    var content_data = {};
    content_data['Steps']=getSetUpSteps(Steps);
    var flow_content=JSON.stringify(content_data);
    var api_id=parseInt($("#btn-save-api").attr("apiid"));
    var data={};
    data['api_id'] = api_id;
    data['flow_content'] = flow_content;

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/api/save/workflow',
        data:data,
        success: function (response) {
            if (!response['isError']) {
                data = response['data'];
                $('#status').text('保存workFlow成功！');
                $('#modal-status').modal('show');

            } else {
                $('#status').text('保存workFlow失败：' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });


})



function getSetUpSteps(Steps) {
    var steps = [];
    for (var i = 0; i < Steps.length; ++i) {
        var setStep = $(Steps[i]);
        var step = {};
        step['index']=i;
        step['keywork'] = setStep.find('[data-keyword]').val();
        var args = [];
        var m = setStep.find("[data-arg]");
        for (var j = 0; j < m.length; ++j) {
            args.push($(m[j]).val());
        }
        step['params'] = args;
        steps.push(step);
    }
    return steps;
}

//绑定显示方法
$('#menu-setup').on('click', 'a', function () {
    var stepMethod = $(this).text();
    var objJson = [];
    selectSteps(stepMethod);
});

function selectSteps(stepMethod)
{

    var params = null;
    for (var i = 0; i < step_descriptors.length; ++i) {
        if (stepMethod === step_descriptors[i]["method"]) {
            params = step_descriptors[i]['params'];
            break;
        }
    }
    var id = $('#setSteps').children().length;
    if (id < 10) {
        id = "0" + id;
    }
    var setUpStepDom = $('#setup-step-template').clone(true);
    setUpStepDom.removeAttr('id');
    setUpStepDom.removeAttr('class');
    $(setUpStepDom.children('span')[1]).html(id);
    var methodDom = $(setUpStepDom.children('input')[0]);
    methodDom.val(stepMethod);
    for (var i = 0; i < params.length; ++i) {
        var paramDiv = $(setUpStepDom.children('div')[0]).clone(true);
        var paramDom = $(paramDiv.children("input")[0]);
        paramDom.attr('placeholder', params[i]);
        $(setUpStepDom.children('div')[i]).before(paramDiv);

    }
    $(setUpStepDom.children('div')[params.length]).remove();
    $('#setup-step-template').before(setUpStepDom);
}

//绑定flowContent
function bandFlowContent()
{
    var content=$("#sp_hidden_flow").html();
    if(!content || content.length==0)
    {
        content='{}';
        return false;
    }
    var contentData = jQuery.parseJSON(content);
    if(contentData!=null)
    {
        loadStepsFrom(contentData);
    }
}

//加载原有的json
function loadStepsFrom(contentData)
{
    if (!contentData) {
        contentData = {};
    }
    if(!contentData.Steps)
        return false;
    $('#setSteps li:not(#setup-step-template)').remove();
    for (var i = 0; i < contentData.Steps.length; ++i) {

        var setUpStepDom = $('#setup-step-template').clone(true);
        setUpStepDom.removeAttr('id');
        setUpStepDom.removeAttr('class');
        var id = i+1;
        if (id < 10) {
            id = "0" + id;
        }
        // parse setup step
        var setStep = contentData.Steps[i];
        if (setStep['doc']) {
            setUpStepDom.children('div.help').text(setStep['doc']);
        }

        var setMethod = setStep.keywork;
        $(setUpStepDom.children('span')[1]).html(id);
        var methodDom = $(setUpStepDom.children('input')[0]);
        methodDom.val(setMethod);

        var setpParam = setStep.params;
        for (var j = 0; j < setpParam.length; ++j) {
            var paramDiv = $(setUpStepDom.children('div')[0]).clone(true);
            var paramDom = $(paramDiv.children("input")[0]);
            paramDom.val(setpParam[j]);
            $(setUpStepDom.children('div')[j]).before(paramDiv);

        }
        $(setUpStepDom.children('div')[setpParam.length]).remove();
        $('#setup-step-template').before(setUpStepDom);
    }
}





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


