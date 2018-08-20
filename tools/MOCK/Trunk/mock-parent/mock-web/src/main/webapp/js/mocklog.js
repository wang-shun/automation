/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    $('#log_list').DataTable({
        "bLengthChange": true,
        "processing": false,
        ajax: {
            "url": "/log/searchAll",
            "type": "POST"
        },
        columns: [
            {data: [0]},
            {data: [2]},
            {data: [3]},
            {data: [4]},
            {data: [0]}
        ],
        columnDefs: [
            {
                targets: [4],
                data: [0],
                render: function (data, type, full) {
                    if(data) {
                        return "<a href='javascript:logDetail("+data+");'>详细</a>";
                    }
                }
            },
            {
                targets: [1],
                data: [1],
                render: function (data, type, full) {
                    var enable=0;
                    if(full[9])
                        enable=1;
                    if(data) {
                       if(full[9])
                       {
                           return data;
                       }else
                       {
                           return data +" (无效) ";
                       }
                    }else
                    {
                        return "服务被删除！";
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

});


$("#deleteClose").bind("click",function(){

    window.parent.frames["main"].location.href =window.parent.frames["main"].location.href;
})



function getData(id)
{
    var data=null;
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/log/detail',
        data:{"log_id": id },
        success: function(response) {
            data=response;
        }
    });
    return data;
}

function logDetail (id) {

    var resdata = getData(id);
    if (!resdata['isError']) {
        var data = resdata['data'];
        var logdata=data[0];
        var apidata=data[1];
        $('#detail_modal').modal('show');
        if(logdata!=null)
        {
            $('#detail_id').html(logdata.id);
            $('#detail_sessionid').html(logdata.sessionId);
            $('#detail_client_address').html(logdata.clientAddress);
            $('#detail_request_time').html(logdata.requestTime);
            $('#detail_response_time').html(logdata.requestTime);
            $('#detail_request_data').html(logdata.requestData);
            $('#detail_response_data').html(logdata.requestData);
            $('#detail_sequency').html(logdata.sequency);
        }
        if(apidata!=null)
        {
            var apiname=apidata.apiName + (apidata.enable==0? "（无效）":"");
            $('#detail_api_name').html(apiname);
        }
    } else {
        $('#status').text('获取Log失败，原因:' + resdata['message']);
        $('#modal-status').modal('show');
    }

}



function cleanPage()
{
    $('#port_modify_modal').modal('hide');
    $('#sending_modal').modal('hide');
}






