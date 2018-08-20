/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {

        $('#template_list').DataTable({
            "bLengthChange": true,
            "processing": false,
            ajax: {
                "url": "/template/searchAll",
                "type": "POST",
                "param":{}
            },
            columns: [
                {data: [0]},
                {data: [1]},
                {data: [2]},
                {data: [3]},
                {data: [0]},
                {data: [0]}
            ],
            columnDefs: [
				{
				    targets: [3],
				    render: function (data, type, full) {
				        if(full[3]) {
				            return "有效";
				        } else {
				            return "无效";
				        }
				    }
				},{
                    targets: [4],
                    data: [0],
                    render: function (data, type, full) {

                        if(data) {
                            var templateName="\""+full[1]+"\"";
                            return "<a href='/template/templateEdit/"+data+"'>修改</a> | <a href='javascript:templateDetail("+data+");'>详细</a>";
                        }
                    }
                },
                {
                    targets: [5],
                    data: [0],
                    render: function (data, type, full) {

                        if(data) {
                            var templateName="\""+full[1]+"\"";
                            return " <a href='javascript:templateDel("+data+","+templateName+")'>删除</a>";
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

function templateDetail (id) {

    $('#template_sending_modal').modal('show');
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/template/detail',
        data:{"template_id": id },
        success: function(response) {
            $('#template_sending_modal').modal('hide');
            if (!response['isError']) {
                data = response['data'];
                $('#template_detail_modal').modal('show');
                $('#template_detail_id').html(data[0]);
                $('#template_detail_name').html(data[2]);
                var hostdata=data[1].split('_');
                $('#template_detail_template_content').html(hostdata[0]);
                $('#template_detail_enable').html(data[3]=="1"?"有效":"无效");
            } else {
                $('#status').text('获取Template失败，原因:' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}


function templateDel(id,name ){
    if(!confirm('确认删除：'+name+' 的模板？'))
    {
        return ;
    }

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/template/delete',
        data:{"template_id": id },
        success: function(response) {
            $('#template_sending_modal').modal('hide');
            if (!response['isError']) {

                $('#delete-modal-status').modal('show');

                //window.parent.frames["main"].location.href =window.parent.frames["main"].location.href;
            } else {
                $('#status').text('获取template失败，原因:' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

