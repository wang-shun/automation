/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {

        $('#api_list').DataTable({
            "bLengthChange": true,
            "processing": false,
            ajax: {
                "url": "/api/searchAll",
                "type": "POST",
                "param":{}
            },
            columns: [
                {data: [0]},
                {data: [3]},
                {data: [8]},
                {data: [6]},
                {data: [4]},
                {data: [0]},
                {data: [0]},
                {data: [0]},
                {data: [0]},
                {data: [0]}
            ],
            columnDefs: [
				{
				    targets: [2],
				    render: function (data, type, full) {
				        if (data) {
				            return "<a title='"+full[10]+"' style=‘cursor:hand’>"+data+"("+full[10]+")</a>";
				        }
				    }
				},
                {
                    targets: [3],
                    render: function (data, type, full) {
                        if(full[6]) {
                            return "有效";
                        } else {
                            return "无效";
                        }
                    }
                },
                {
                    targets: [5],
                    data: [0],
                    render: function (data, type, full) {

                        if(data) {
                            var apiname="\""+full[3]+"\"";
                            return " <a href='/api/workflow/"+ data +"'>WorkFlow</a>";
                        }
                    }
                },
                {
                    targets: [6],
                    data: [0],
                    render: function (data, type, full) {

                    	if(data) {
                            return "<span>"+full[5]+"</span>";
                        }
                    }
                },{
                    targets: [7],
                    data: [0],
                    render: function (data, type, full) {

                        if(data) {
                        	return " <a href='/template/templateEdit/"+ full[2] +"'>模板</a>";
                        }
                    }
                },
                {
                    targets: [8],
                    data: [0],
                    render: function (data, type, full) {

                        if(data) {
                            var apiname="\""+full[3]+"\"";
                            return "<a href='/api/apiEdit/"+data+"'>修改</a> | <a href='javascript:apiDetail("+data+");'>详细</a>";
                        }
                    }
                },
                {
                    targets: [9],
                    data: [0],
                    render: function (data, type, full) {

                        if(data) {
                            var apiname="\""+full[3]+"\"";
                            return " <a href='javascript:apiDel("+data+","+apiname+")'>删除</a>";
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

function apiDetail (id) {

    $('#api_sending_modal').modal('show');
    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/api/detail',
        data:{"api_id": id },
        success: function(response) {
            $('#api_sending_modal').modal('hide');
            if (!response['isError']) {
                data = response['data'];
                $('#api_detail_modal').modal('show');
                $('#api_detail_id').html(data[0]);
                $('#api_detail_name').html(data[3]);
                var hostdata=data[1].split('_');
                $('#api_detail_host_name').html(hostdata[0]);
                $('#api_detail_host_domain').html(hostdata[1]);
                $('#api_detail_host_url').html(hostdata[2]);
                $('#api_detail_keyWords').html(data[3]);
                $('#api_intercept_param').html(data[4]);
                $('#api_detail_descript').html(data[5]);
                $('#api_template_id').html(data[2]);
                $('#api_detail_enable').html(data[7]=="1"?"有效":"无效");
                $('#api_detail_flow_content').html(data[8] );

            } else {
                $('#status').text('获取api失败，原因:' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}


function apiDel(id,name ){
    if(!confirm('确认删除：'+name+' 的服务？'))
    {
        return ;
    }

    $.ajax({
        type: 'GET',
        async: false,
        dataType: 'json',
        url: '/api/delete',
        data:{"api_id": id },
        success: function(response) {
            $('#api_sending_modal').modal('hide');
            if (!response['isError']) {

                $('#delete-modal-status').modal('show');

                //window.parent.frames["main"].location.href =window.parent.frames["main"].location.href;
            } else {
                $('#status').text('获取api失败，原因:' + response['message']);
                $('#modal-status').modal('show');
            }
        }
    });
}

