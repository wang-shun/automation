$(document).ready(function () {
    dateShow();
    $('#log_begin_div').bind('click', function () {
        $('#log_timespan_begin_input').datetimepicker('show');
    });


    $('#log_end_div').bind('click', function () {
        $('#log_timespan_end_input').datetimepicker('show');
    });

    $("#log_generate_btn").bind('click',function(){
        getLogs();
    });

    getTemplates();

});

function getTemplates()
{
    var templatepart=$("#hiddenValue").attr('templatepart');

    $.ajax({
        dataType: "json",
        type: "GET",
        async: false,
        url: '/log/getTemplates',
        success: function (data) {
            if (data['isError']) {
                $('#modal-status').modal('show');
                $('#status').empty();
                $('#status').html(data['message']);
            } else {
                var tdata=data['data'];
                for(var i=0;i<tdata.length;i++)
                {
                    var value= tdata[i];
                    if(value=='' || value.indexOf("_"+templatepart+".xml")>0)
                    {

                        var text = value.length> 0 ? value.substring(0,value.lastIndexOf("_")): '';
                        $("#log_template").append("<option value='"+value+"'>"+text+"</option>");

                    }

                }

            }
        }
    });
}


function getLogs()
{
    $("#log_list").removeClass('hidden');
    $('#log_list').dataTable().fnDestroy();


    var begin=$("#log_timespan_begin_input").val();
    var end=$("#log_timespan_end_input").val();
    var template=$("#log_template").find('option:selected').val();

    var channel= $("#hiddenValue").attr('channel');
    var qmgName=$("#hiddenValue").attr('qmgName');
    var host=$("#hiddenValue").attr('host');
    var port=$("#hiddenValue").attr('port');

    if(begin.length==0 && end.length >0)
        begin=end;

    if(begin.length>0 && end.length>0 && begin > end)
    {
        $('#modal-status').modal('show');
        $('#status').empty();
        $('#status').html('请检查时间');
        return false;
    }

    $.ajax({
        dataType: "json",
        type: "GET",
        async: false,
        url: '/log/searchAll',
        data:{
            'begin':begin,
            'end':end,
            'template':template,
            'host':host,
            'qmgName':qmgName,
            'channel':channel,
            'port':port

        },
        success: function (data) {
            if (data['isError']) {
                $('#modal-status').modal('show');
                $('#status').empty();
                $('#status').html(data['message']);
            } else {
                getData(data['data']);
            }
        }
    });
}




function dateShow() {
    $('#log_timespan_begin_input').val('');
    $('#log_timespan_begin_input').datetimepicker({
        timepicker: false,
        format: 'Ymd'
    });

    $('#log_timespan_end_input').val('');
    $('#log_timespan_end_input').datetimepicker({
        timepicker: false,
        format: 'Ymd'
    });

}


function getData(data)
{

    var template='';
    $('#log_list').DataTable({
        "bLengthChange": true,
        "processing": false,
        "searching":true,
        data:data,
        "aaSorting": [
            [ 0, "desc" ]
        ],
        columns: [
            { data: ['createTime'] },
            { data: ['mq_host'] },
            { data: ['mq_QName'] },
            { data: ['template'] },
            { data: ['mq_useType'] },
            { data: ['mq_message'] }

        ],columnDefs: [
            {
                targets: [0],
                render: function (data, type, full) {
                    if(full["createTime"]) {
                        return "<a onclick='showLog("+full['id']+")'  >"+new Date(data).Format("yyyy-MM-dd hh:mm:ss")+"</a>" ;
                    } else {
                        return "";
                    }
                }
            },

            {
                targets: [3],
                render: function (data, type, full) {
                    if(full["template"]) {
                        template= data.substring(0,data.lastIndexOf('_'));
                        return template;
                    } else {
                        template='';
                        return "";
                    }
                }
            },

            {
                targets: [5],
                render: function (data, type, full) {
                    if(full["mq_message"]) {

                        return "<textarea utype='"+full['mq_useType'] +"' time='"+new Date(full['createTime']).Format("yyyy-MM-dd hh:mm:ss")+"' id='text"+full['id']+"' template='"+template+"' qname='"+full['mq_QName']+"' jndiname='"+full['mq_jndiName']+"' host='"+full['mq_host']+"' port='"+full['mq_port']+"' channel='"+full['mq_channel']+"' ip='"+full['user_IP']+"'  style='height: 100%;width: 98%;' readonly='readonly'>"+data+"</textarea>"
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

function showLog(id)
{
    var obj =$("#text"+id);

    var host=obj.attr("host");
    var template=obj.attr("template");
    var qname=obj.attr("qname");
    var jndiname=obj.attr("jndiname");
    var port=obj.attr("port");
    var channel=obj.attr("channel");
    var ip=obj.attr("ip");
    var time=obj.attr('time');
    var utype=obj.attr('utype');
    var mess= formatXML(obj.val());


    $("#spTime").html(time);
    $("#spHost").html(host);
    $("#spJndi").html(jndiname);
    $("#spqName").html(qname);
    $("#spIP").html(ip);
    $("#spTemplate").html(template);
    $("#messageText").html(mess);
    $("#spuseType").html(utype);


    $("#log_detail_modal").modal('show');

}

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