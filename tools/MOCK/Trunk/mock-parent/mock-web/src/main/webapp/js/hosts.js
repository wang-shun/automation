/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
//ID,DOMAIN,PORT_ID,SERVICE_NAME,URL,HOST_TYPE,PROTOCOL_TYPE,ENABLE
    $('#host_list').DataTable({
        "bLengthChange": true,
        "processing": false,
        ajax: {
            "url": "/host/searchAll",
            "type": "POST"
        },
        columns: [
            {data: [0]},
            {data: [3]},
            {data: [1]},
            {data: [4]},
            {data: [8]},
            {data: [7]},
            {data: [0]}
        ],
        columnDefs: [

            {
                targets: [5],
                render: function (data, type, full) {
                    if(full[7]) {
                        return "有效";
                    } else {
                        return "无效";
                    }
                }
            },
            {
                targets: [6],
                data: [0],
                render: function (data, type, full) {

                    if(data) {
                        var hostname="\""+full[3]+"\"";
                        return "<a href='/host/hostEdit/"+data+"'>修改</a> | <a href='javascript:hostDetail("+data+");'>详细</a> | <a href='javascript:hostDel("+data+","+hostname+")'>删除</a>";
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

function hostDetail (id) {

    $('#host_sending_modal').modal('show');
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/host/detail',
        data:{"host_id": id },
        success: function(response) {
            $('#host_sending_modal').modal('hide');
            if (!response['isError']) {
                data = response['data'];
                $('#host_detail_modal').modal('show');
                $('#host_detail_id').html(data[0]);
                $('#host_detail_service_name').html(data[3]);
                $('#host_detail_domain').html(data[1]);
                $('#host_detail_port').html(data[2]);
                $('#host_detail_url').html(data[4]);
                $('#host_detail_hosttype').html(data[5]);
                $('#host_detail_protocol').html(data[6]);
                $('#host_detail_enable').html(data[7]=="1"?"有效":"无效");

            } else {
                $('#status').text('获取host失败，原因:' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}


function hostDel(id,name ){
    if(!confirm('确认删除：'+name+' 的Host？'))
    {
        return ;
    }


    $('#host_sending_modal').modal('show');

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/host/delete',
        data:{"host_id": id },
        success: function(response) {
            $('#host_sending_modal').modal('hide');
            if (!response['isError']) {

                $('#delete-modal-status').modal('show');

                //window.parent.frames["main"].location.href =window.parent.frames["main"].location.href;
            } else {
                $('#status').text('删除host失败，原因:' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

