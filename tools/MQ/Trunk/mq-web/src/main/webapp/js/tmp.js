$(document).ready(function () {
    loadTmpTable('');

    $('#log_list').dataTable().fnDestroy();

    $('#tmp_msg_btn').bind('click',function() {
        genMsg();
    });

    $('#tmp_log_btn').bind('click',function() {
        loadTmpLogTable();
    });

    $("#tmp_mqs_btn").bind('click',function(){
        goMqList();
    });


});


function loadTmpTable(logId) {
    $.ajax({
        dataType: "json",
        type: "GET",
        async: false,
        url: '/mq/tmp/data',
        data:
        {
            'tmpName' : $('#hiddenValue').attr('template'),
            'pid': $('#hiddenValue').attr('mq_pid'),
            'logId':logId
        },
        success: function (data) {
            if (data['isError']) {
                $('#error_span').empty();
                $('#error_span').html(data['message']);
            } else {
                bandTable(data['data']);
            }
        }
    });
}


function bandTable(data) {
    $('#tmp_list').DataTable({
        "bLengthChange": false,
        "bPaginate": false,
        "processing": false,
        "bDestroy":true,
        data: data,
        //"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0 ] }],
        //columnDefs:
        columns: [
            { data: ['name'] },
            { data: ['isempty'] },
            { data: ['path'] },
            { data: ['dtype'] },
            { data: ['dec'] },
            { data: ['ismust'] },
            { data: ['remark'] },
            { data: ['value'] }
        ],
        columnDefs: [

            //{
            //        orderable:false,//禁用排序
            //        targets:[0,1,2,3,4,5,6,7]   //指定的列
            //}
            //,

            {
                targets: [7],
                render: function (data, type, full) {
                    if ("date" == full['dtype'].toLowerCase() && "" == full['value'].trim()) {
                        return "<textarea  style='height: 25px;width: 100%;margin-top: 0px;margin-bottom: 0px;'>"+getNowDateTime()+"</textarea>";
                    } else {
                        return "<textarea  style='height: 25px;width: 100%;margin-top: 0px;margin-bottom: 0px;'>"+data+"</textarea>";
                    }

                }
            }
        ]
    });
}

function genMsg() {
    var pathList = [];
    var typeList = [];
    var valueList = [];
    var notEmpty = true;
    $('#tmp_list tbody tr').each(function () {
        var name = "";
        var path = "";
        var type = "";
        var isMust = "";
        var value = "";
        var isEmpty="";
        notEmpty = true;
        $(this).find('td').each(function(index) {
            if (0 == index) {
                name = $(this).html().trim();
            }else if (1 == index) {
                isEmpty = $(this).html().trim();
            }
            else if (2 == index) {
                path = $(this).html().trim();
            } else if (3 == index) {
                type = $(this).html().trim();
            } else if (5 == index) {
                isMust = $(this).html().trim();
            } else if (7 == index) {
                value = $(this).find('textarea').val().trim();
            }
        });
        if ("true" == isMust && "false" == isEmpty && "" == value) {//必填项判空检查
            $('#status').empty();
            $('#status').html("'" + name + "'，消息内容必填，请确认");
            $('#modal-status').modal('show');
            notEmpty = false;
            return false;
        } else if ("" != value.trim()) {//检查不为空的类型格式
            if ("int" == type.toLowerCase() && !isInt(value.trim())) {//内容类型检查
                $('#status').empty();
                $('#status').html("'" + name + "'，消息内容应为Int类型，请修改");
                $('#modal-status').modal('show');
                notEmpty = false;
                return false;
            } else if ("double" == type.toLowerCase() && !isDouble(value.trim())) {
                $('#status').empty();
                $('#status').html("'" + name + "'，消息内容应为Double类型，请修改");
                $('#modal-status').modal('show');
                notEmpty = false;
                return false;
            } else  {

            //else if ("date" == type.toLowerCase() && !isDate(value.trim())) {
            //        $('#status').empty();
            //        $('#status').html("'" + name + "'，消息内容应为Date(yyyy-MM-dd HH:mm:ss)类型，请修改");
            //        $('#modal-status').modal('show');
            //        notEmpty = false;
            //        return false;
            //    }


                pathList.push(path);
                typeList.push(type);
                valueList.push(value);
            }
        }else if("true"==isEmpty && "true"==isMust)
        {
            pathList.push(path);
            typeList.push(type);
            valueList.push(value);
        }
    });
    if(true == notEmpty) {
        $.ajax({
            dataType: "json",
            type: "POST",
            async: false,
            url: '/mq/tmp/genmsg',
            data:
            {
                'pathList' : pathList,
                'typeList': typeList,
                'valueList': valueList
            },
            success: function (data) {
                if (data['isError']) {
                    $('#error_span').empty();
                    $('#error_span').html(data['message']);
                } else {
                    $('#messageText').val(data['data']);
                    $('#mq_detail_modal').modal('show');
                }
            }
        });
    }
}

/**
 * 格式化
 */
$('body').on('click','#btn-format',function(){
    var Str=  $('#messageText').val();
    Str=formatXML(Str);
    $('#messageText').val(Str);
    return false;
});

/**
 * 加入队列
 */
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
    var template=obj.attr('template');
    var data={};
    data["mq_qname"]=qname;
    data["mq_hostname"]=hostname;
    data["mq_qmgname"]=qmgname;
    data["mq_channel"]=channel;
    data["mq_ccsid"]=ccsid;
    data["mq_port"]=port;
    data["mq_message"]=message;
    data["mq_pid"]=pid;
    data["template"]=template;

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
                //getData();
            }
        }
    });
}


function getNowDateTime() {
    var datetime = new Date();
    var year = datetime.getFullYear();
    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
    var hour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime.getHours();
    var minute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
    var second = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
    return year + "-" + month + "-" + date + "T" + hour + ":" + minute + ":" + second;
}

function isInt(s) {
    var regu = "^([0-9]*)$";
    var re = new RegExp(regu);
    if (s.search(re) != -1)
        return true;
    else
        return false;
}



function isDouble(s) {
    //var regu = "^([0-9]*[.0-9])$";
    var regu = /^[-\+]?\d+(\.\d+)?$/;
    return regu.test(s);
}

function isDate(s) {


    var regu = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\T+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])(\\+|\\-)([0][8]):([0][0])$";
    var re = new RegExp(regu);
    if (s.search(re) != -1)
        return true;
    else
        return false;
}


function loadTmpLogTable()
{
    $('#log_list').dataTable().fnDestroy();
    $.ajax({
        dataType: "json",
        type: "GET",
        async: false,
        url: '/mq/tmp/logdata',
        data:
        {
            'tmpName' : $('#hiddenValue').attr('template'),
            'pid': $('#hiddenValue').attr('mq_pid')
        },
        success: function (data) {
            if (data['isError']) {
                $('#error_span').empty();
                $('#error_span').html(data['message']);
            } else {

                bandLogTable(data['data']);
                $("#tmp_table_log_div").modal('show');
            }
        }
    });
}

function bandLogTable(data) {

        $('#log_list').DataTable({
            "bLengthChange": true,
            "processing": false,
            "bDestroy":false,
            "searching":false,
            data: data,
            "aaSorting": [
                [ 0, "desc" ]
            ],


            columns: [
                { data: ['createTime'] },
                { data: ['mq_message'] },
                { data: ['id'] }
            ],
            columnDefs: [
                {
                    targets: [0],
                    render: function (data, type, full) {
                        return new Date(data).Format("yyyy-MM-dd hh:mm:ss") ;
                    }
                }   ,
                {
                    targets: [1],
                    render: function (data, type, full) {
                        return "<textarea readonly='readonly' style='height: 125px;width: 300px;margin-top: 0px;margin-bottom: 0px;'>"+formatXML(data) +"</textarea>";
                    }
                }   ,
                {
                    targets: [2],
                    "bSortable": false,
                    render: function (data, type, full) {
                        return "<a onclick='conventLog("+data+")' class='btn btn-primary' >转模板</a>";
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

Date.prototype.Format = function(fmt)
{ //author: meizz
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
}

function conventLog(logid)
{
    loadTmpTable(logid);
    $("#tmp_table_log_div").modal('hide');
}

function goMqList()
{
    var obj=$("#hiddenValue");
    var qname=obj.attr("mq_qname");
    var hostname=obj.attr("mq_hostname");
    var qmgname=obj.attr("mq_qmgname");
    var channel=obj.attr("mq_channel");
    var ccsid=obj.attr("mq_ccsid");
    var port=obj.attr("mq_port");
    var pid=obj.attr("mq_pid");
    var qtype=obj.attr("mq_type");
    var qiscmd=obj.attr("mq_iscmd");
    var qtitle=obj.attr('title');

    var url="/mq/get/"+"/"+qtitle+"/"+pid+"/"+hostname+"/"+qmgname+"/"+channel+"/"+ccsid+"/"+port+"/"+qname+"/"+qtype+"/"+qiscmd+"/mq";

    window.parent.frames["main"].location.href =url;
}