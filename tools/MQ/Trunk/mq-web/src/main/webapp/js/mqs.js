
var editor = null;
var edit_obj = null;
var isCmd=null;

$(document).ready(function () {

    isCmd=$("#hiddenValue").attr("mq_iscmd");
    getData();
    if(isCmd=='0')
    {
        $("#btnAdd").addClass('hidden');
        $("#btndelete").addClass('hidden');
    }

});



function getData()
{
    var obj=$("#hiddenValue");
    var qname=obj.attr("mq_qname");
    var hostname=obj.attr("mq_hostname");
    var qmgname=obj.attr("mq_qmgname");
    var channel=obj.attr("mq_channel");
    var ccsid=obj.attr("mq_ccsid");
    var port=obj.attr("mq_port");
    var pid=obj.attr("mq_pid");
    var title=obj.attr("title");

    $("#btndelete").addClass('hidden');
    if(qname.length==0|| qmgname.length ==0 || channel.length==0 || ccsid.length==0)
    {
        $("#btnAdd").addClass('hidden');
        $('#mq_list').addClass('hidden');
        $("#div_q_name").addClass('hidden');
        return;
    }

    if(isCmd=='1')
    {
        $("#btnAdd").removeClass('hidden');
    }

    $("#div_q_name").removeClass('hidden');
    $('#mq_list').removeClass('hidden');

    var mqType=obj.attr("mq_type");
    if(mqType!= undefined && mqType.length >0 )
    {
        $("#spq_type").html(title+'-');
    }




    var data={};
    data["mq_qname"]=qname;
    data["mq_hostname"]=hostname;
    data["mq_qmgname"]=qmgname;
    data["mq_channel"]=channel;
    data["mq_ccsid"]=ccsid;
    data["mq_port"]=port;
    data["mq_pid"]=pid;



    $.ajax({
        dataType: "json",
        type: "POST",
        async: false,
        url: '/mq/searchAll',
        data:data,
        success: function (data) {
            if (data['isError']) {
                $('#error_span').empty();
                $('#error_span').html(data['message']);
            } else {
                if(data['data'].length > 0 && isCmd=='1')
                {
                    $("#btndelete").removeClass('hidden');
                }
                data = data['data'];
                bandTable(data);
                //注释掉js绑定
                $("#filter_table_tbody2").removeClass("hidden");
            }
        }
    });
}

function bandTable(data){
var index=0;

    $('#mq_list').DataTable({
        "bLengthChange": true,
        "processing": false,
        "bDestroy":true,
        "searching":true,
        data: data,

        columns: [
            { data: [1] },
            { data: [2] },
            //{ data: [0] },
            { data: [0] }

        ],columnDefs: [

                {
                    targets: [2],
                    render: function (data, type, full) {
                        if(full[0]) {

                            return "<textarea id='textmess"+index+"'  style='height: 100%;width: 98%;' readonly='readonly'>"+data+"</textarea>"
                        } else {
                            return "";
                        }
                    }
                },
            {
                targets: [3],
                render: function (data, type, full) {
                    if(full[0] && isCmd=='1') {

                            var text= "<a href='javascript:edit("+index+")' >复制</a>";
                            index ++;
                            return text ;

                    } else {
                        return "";
                    }
                }
            }
    ]
        ,
        language: {
            lengthMenu: '&nbsp; 每页显示 <select class="form-control input-xsmall" style="width: 60px;">' + '<option value="5">5</option>' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="30">30</option>' + '<option value="40">40</option>' + '<option value="50">50</option>' + '</select>条记录',
            processing: "载入中",
            paginate: {
                previous: "上一页",
                next: "下一页",
                first: "第一页",
                last: "最后一页"
            },

            zeroRecords: "没有内容",
            info: "总共_PAGES_ 页，显示第_START_ 到第 _END_ ",
            infoEmpty: "0条记录",
            infoFiltered: ""
        }

    });

}

function deleteMessage()
{
    $('#modal-delete-status').modal('show');
    $('#status-delete').empty();
    $('#status-delete').html('请确认消费第一条消息！');
}

function btnDelete()
{
    var obj=$("#hiddenValue");
    var qname=obj.attr("mq_qname");
    var hostname=obj.attr("mq_hostname");
    var qmgname=obj.attr("mq_qmgname");
    var channel=obj.attr("mq_channel");
    var ccsid=obj.attr("mq_ccsid");
    var port=obj.attr("mq_port");
    var pid=obj.attr("mq_pid");
    var data={};
    data["mq_qname"]=qname;
    data["mq_hostname"]=hostname;
    data["mq_qmgname"]=qmgname;
    data["mq_channel"]=channel;
    data["mq_ccsid"]=ccsid;
    data["mq_port"]=port;
    data["mq_pid"]=pid;

    $.ajax({
        dataType: "json",
        type: "POST",
        async: false,
        url: '/mq/use',
        data:data,
        success: function (data) {
            if (data['isError']) {
                $('#modal-status').modal('show');
                $('#status').empty();
                $('#status').html(data['message']);
            } else {
                $('#modal-delete-status').modal('hide');
                getData();
                $("#mq_detail_modal_mess").modal('show');
                var Str=formatXML(data['message']);
                $("#usemessageText").val(Str);
            }
        }
    });
}

function getMessage()
{
    var message= $.trim($("#messageText").val()) ;
    if(message.length==0)
    {
        $('#modal-status').modal('show');
        $('#status').html('请输入消息内容');
        return '';
    }
    return message ;
}

function addMessage()
{
    var message=getMessage() ;
    if(message.length==0)
    {
        return false;
    }

    var obj=$("#hiddenValue");
    var qname=obj.attr("mq_qname");
    var hostname=obj.attr("mq_hostname");
    var qmgname=obj.attr("mq_qmgname");
    var channel=obj.attr("mq_channel");
    var ccsid=obj.attr("mq_ccsid");
    var port=obj.attr("mq_port");
    var pid=obj.attr("mq_pid");
    var data={};
    data["mq_qname"]=qname;
    data["mq_hostname"]=hostname;
    data["mq_qmgname"]=qmgname;
    data["mq_channel"]=channel;
    data["mq_ccsid"]=ccsid;
    data["mq_port"]=port;
    data["mq_message"]=message;
    data["mq_pid"]=pid;
    data["template"]='';

    $.ajax({
        dataType: "json",
        type: "POST",
        async: false,
        url: '/mq/add',
        data:data,
        success: function (data) {
            if (data['isError']) {
                $('#modal-status').modal('show');
                $('#status').empty();
                $('#status').html(data['message']);
            } else {
                $('#mq_detail_modal').modal('hide');
                getData();
            }
        }
    });
}

function edit(data)
{
    $('#mq_detail_modal').modal('show');


    $("#messageText").val('') ;
    if(data!=null)
    {
       $("#messageText").val($("#textmess"+data).val()) ;
    }
}


$('body').on('click', '.icon-pencil', function () {
    $('#modal-editor').modal('show');
    edit_obj = $('#messageText');
    try {
        JSON.parse(edit_obj.val());
        $('input[name="editor-manner"][value="json"]').click();
    } catch (e) {
        $('input[name="editor-manner"][value="text"]').click();
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

$('body').on('click', '#btn-format-text', function () {
   var Str= editor.getValue();
    $.ajax({
        dataType: "json",
        type: "POST",
        async: false,
        url: '/mq/xmlformat',
        data:{"mess":Str},
        success: function (data) {
            if (!data['isError']) {
                Str= data["data"];
            }
        }
    });
    editor.setValue(Str);
    return false;
});

$('body').on('click','#btn-format',function(){
    var Str=  $('#messageText').val();
    Str=formatXML(Str);
    $('#messageText').val(Str);
    return false;
});




$('body').on('click','#btn-save-q',function(){
    var message=getMessage() ;
    if(message.length>0)
    {
        $('#modal-add-status').modal('show');
    }

});


//格式化xml
function formatXML(mess)
{
    $.ajax({
        dataType: "json",
        type: "POST",
        async: false,
        url: '/mq/xmlformat',
        data:{"mess":mess},
        success: function (data) {
            if (!data['isError']) {
                mess= data["data"];
            }
        }
    });
    return mess;
}
