/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
//ID,DOMAIN,PORT_ID,SERVICE_NAME,URL,HOST_TYPE,PROTOCOL_TYPE,ENABLE
    $('#port_list').DataTable({
        "bLengthChange": true,
        "processing": false,
        ajax: {
            "url": "/port/searchAll",
            "type": "POST"
        },
        columns: [
            {data: "id"},
            {data: "portNumber"},
            {data: "enable"},
            {data: "id"}
        ],
        columnDefs: [
            {
                targets: [2],
                render: function (data, type, full) {
                    if(full["enable"]) {
                        return "有效";
                    } else {
                        return "无效";
                    }
                }
            },
            {
                targets: [3],
                data: ["id"],
                render: function (data, type, full) {

                    if(data) {
                        var id=data;
                        var port_number="\""+full["portNumber"]+"\"";
                        return "<a href='javascript:portModify("+data+")'>修改</a> | <a href='javascript:portDetail("+data+");'>详细</a> | <a href='javascript:portDel("+data+","+port_number+")'>删除</a>";
                    }
                }
            }
        ],
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
    var viewid=$("#hidden").val();
    if(viewid.length>0)
    {
        portDetail(parseInt(viewid));
    }
});


$("#deleteClose").bind("click",function(){

    window.parent.frames["main"].location.href =window.parent.frames["main"].location.href;
})



$("#btn_port_modify").bind("click",function(){
    var port_id=  $("#btn_port_modify").attr("port_id");
    var portNumber=$("#port_modify_detail_number").val();
    var portType= $("#btn_port_modify").attr("port_type");
    var enable=$("#checkbox_port_enable").is(":checked");
    if(port_id.length==0 || portNumber.length==0)
    {
        $('#status').text('请填写端口号！');
        $('#modal-status').modal('show');
        return false;
    }

    if(isNaN(port_id)||isNaN(portNumber))
    {
        $('#status').text('端口号必须为数字！');
        $('#modal-status').modal('show');
        return false;
    }

    var url=portType=="create"?"/port/save":"/port/edit";
    var data={};
    data["port_id"]=parseInt(port_id);
    data["port_number"]=parseInt(portNumber);
    data["enable"]=enable?"1":"0";
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: url,
        data:data,
        success: function(response) {
            if (response['isError']) {
                $('#status').text('保存失败！原因：' + response["message"]);
                $('#modal-status').modal('show');
            }else
            {
                cleanPage();
                $("#result-status").html("保存 端口 ："+portNumber+" 成功！");
                $('#result_modal-status').modal('show');


            }
        }
    });

})

//加了return false 不验证了  在保存按钮内进行验证
$("#port_modify_detail_number").bind("blur",function(){
    return false;
    var port_id=  $("#btn_port_modify").attr("port_id");
    var portNumber=$("#port_modify_detail_number").val();
    if(port_id.length==0 || portNumber.length==0)
        return false;
    if(isNaN(port_id)||isNaN(portNumber))
    {
        $('#status').text('端口号必须为数字！');
        $('#modal-status').modal('show');
        return false;
    }

    var data={};
    data["port_id"]=parseInt(port_id);
    data["port_number"]=parseInt(portNumber);
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/port/checkNumber',
        data:data,
        success: function(response) {
            if (response['isError']) {
                $('#status').text('已经存在端口号：' + portNumber);
                $('#modal-status').modal('show');
                $('#port_modify_detail_number').val($("#port_modify_detail_number").attr("oldvalue"));
            }
        }
    });

});

function getPortData(id)
{
    var data=null;
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/port/detail',
        data:{"port_id": id },
        success: function(response) {
            data=response;
        }
    });
    return data;
}

function portDetail (id) {

    var resdata = getPortData(id);
    if (!resdata['isError']) {
        data = resdata['data'];
        $('#port_detail_modal').modal('show');
        $('#port_detail_id').html(data.id);
        $('#port_detail_number').html(data.portNumber);
        $('#port_detail_enable').html(data.enable == 1 ? "有效" : "无效");
    } else {
        $('#status').text('获取端口失败，原因:' + resdata['message']);
        $('#modal-status').modal('show');
    }

}

function portModify(id)
{
    $('#sending_modal').modal('show');
    $("#btn_port_modify").attr("port_id",id);
    $("#btn_port_modify").attr("port_type","edit");
    var resdata = getPortData(id);
    $('#sending_modal').modal('hide');
    if (!resdata['isError']) {
        data = resdata['data'];
        $('#port_modify_modal').modal('show');
        $('#port_modify_detail_number').val(data.portNumber);
        $('#checkbox_port_enable').prop("checked",data.enable==1?true:false);
        $('#port_modify_detail_number').attr("oldvalue",data.portNumber);
        $('#checkbox_port_enable').attr("oldvalue",data.enable);
    } else {
        $('#status').text('获取post失败，原因:' + resdata['message']);
        $('#modal-status').modal('show');
    }
}

function cleanPage()
{
    $('#port_modify_modal').modal('hide');
    $('#sending_modal').modal('hide');
}


function portAdd()
{
    cleanPage();

    $("#btn_port_modify").attr("port_id","0");
    $("#btn_port_modify").attr("port_type","create");

    $('#port_modify_detail_number').val('');
    $('#checkbox_port_enable').prop("checked",true);
   $("#port_modify_modal").modal('show');
}


function portDel(id,number ){
    if(!confirm('确认删除：'+number+' 的Port？'))
    {
        return ;
    }


    $('#sending_modal').modal('show');

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/port/delete',
        data:{"port_id": id },
        success: function(response) {
            $('#sending_modal').modal('hide');
            if (!response['isError']) {
                $("#result-status").html("删除port："+number+" 成功！");
                $('#result_modal-status').modal('show');
                //window.parent.frames["main"].location.href =window.parent.frames["main"].location.href;
            } else {
                $('#status').text('删除端口失败，原因:' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

