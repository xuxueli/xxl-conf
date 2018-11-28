$(function(){

    // appname change
    $('#appname').on('change', function(){
        //reload
        var appname = $('#appname').val();
        window.location.href = base_url + "/conf?appname=" + appname;
    });

    if (!hasPermission) {
        layer.open({
            icon: '2',
            content: '您没有该项目的配置权限,请联系管理员开通'
        });
        return;
    }

	// init date tables
	var confTable = $("#conf_list").dataTable({
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url: base_url + "/conf/pageList",
			type:"post",
			data : function ( d ) {
				var obj = {};
				obj.appname = $('#appname').val();
				obj.key = $('#key').val();
				obj.start = d.start;
				obj.length = d.length;
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'key', 'width': '20%', "visible" : true},
			{
				"data": 'value',
				'width': '30%',
				"visible" : true,
				"render": function ( data, type, row ) {

                    var temp = (row.value.length > 20)? row.value.substring(0, 20)+'...' : row.value;
                    return "<span title='"+ row.value +"'>"+ temp +"</span>";;

					/*if (row.value == row.zkValue) {

					} else {
						var cacheValue = '<table border=1 bordercolor="white" ' +
							'style="border-collapse:collapse;width: 100%;table-layout:fixed;word-wrap:break-word;" >\n' +
                        '        <tbody>\n' +
                        '            <tr>\n' +
                        '                <td style="width:20%;padding: 10px;" >DB</td>\n' +
                        '                <td style="width:80%;padding: 10px;" >' + row.value + '</td>\n' +
                        '            </tr>\n' +
						'			 <tr>\n' +
                        '                <td style="width:20%;padding: 10px;" >ZK</td>\n' +
                        '                <td style="width:20%;padding: 10px;" >' + row.zkValue + '</td>\n' +
                        '            </tr>\n' +
                        '        </tbody>\n' +
                        '    </table>';
                        tecCache['diff_' + row.key] = cacheValue;

						var html = "<span style='color: red'>数据未同步: <a href='javascript:;' class='tecTips' cacheKey='diff_"+ row.key +"' >查看</a></span>";
						return html;
					}*/
				}
			},
			{ "data": 'title', 'width': '30%', "visible" : true},
			{
				"data": '操作',
				'width': '20%' ,
				"render": function ( data, type, row ) {
					return function(){

                        confData[row.key] = row;

                        // log list
                        var logListBtn = '';
                        if (row.logList && row.logList.length>0) {
                            logListBtn = '<button class="btn btn-warning btn-xs tecTips" cacheKey="log_'+ row.key +'" type="button">变更历史</button>  ';

                            var cacheValue = ''+
                                '   <table border=1 bordercolor="white" ' +
                                '       style="border-collapse:collapse;width: 100%;table-layout:fixed;word-wrap:break-word;" >\n' +
                                '        <tbody>\n';

                                cacheValue +=
                                    '            <tr>\n' +
                                    '                <td style="width:27%;padding: 10px;" >操作时间</td>\n' +
                                    '                <td style="width:23%;padding: 10px;" >操作人</td>\n' +
                                    '                <td style="width:50%;padding: 10px;" >配置Value</td>\n' +
                                    '            </tr>\n';
                                for (var i in row.logList) {
                                    cacheValue +=
                                        '            <tr>\n' +
                                        '                <td style="width:27%;padding: 10px;" >' + moment(new Date(row.logList[i].addtime)).format("YYYY-MM-DD HH:mm:ss") + '</td>\n' +
                                        '                <td style="width:23%;padding: 10px;" >' + row.logList[i].optuser + '</td>\n' +
                                        '                <td style="width:50%;padding: 10px;" >' + row.logList[i].value + '</td>\n' +
                                        '            </tr>\n';
                                }

                                cacheValue += '' +
                                    '   </tbody>\n' +
                                    '</table>';

                            tecCache['log_' + row.key] = cacheValue;
                        }

						// html
						var html = '<p key="'+ row.key +'" >'+
							'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
                            logListBtn +
							'<button class="btn btn-danger btn-xs delete" type="button">删除</button>  '+
							'</p>';

						return html;
					};
				}
			}
		],
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	
	$("#searchBtn").click(function(){
		confTable.fnDraw();
	});

	// tecTips
    var tecCache = {};
	$("#conf_list").on('click', '.tecTips',function() {
		var cacheKey = $(this).attr("cacheKey");
        var cacheValue = tecCache[cacheKey];
		ComAlertTec.show(cacheValue);
	});


    var confData = {};

	// 删除
	$("#conf_list").on('click', '.delete',function() {

        var key = $(this).parent('p').attr("key");

        layer.confirm( "确定要删除配置：" + key , {
            icon: 3,
            title: '系统提示' ,
            btn: [ '确定', '取消' ]
        }, function(index){
            layer.close(index);

            $.post(
                base_url + "/conf/delete",
                {
                    "key" : key
                },
                function(data, status) {
                    if (data.code == 200) {
                        layer.open({
                            icon: '1',
                            content: '删除成功' ,
                            end: function(layero, index){
                                confTable.fnDraw();
                            }
                        });
                    } else {
                        layer.open({
                            icon: '2',
                            content: (data.msg||'删除失败')
                        });
                    }
                }
            );

        });

	});

    // jquery.validate 自定义校验
    jQuery.validator.addMethod("myValid01", function(value, element) {
        var length = value.length;
        var valid = /^[a-z][a-z0-9.]*$/;
        return this.optional(element) || valid.test(value);
    }, "限制以小写字母开头，由小写字母、数字和.组成");

	// 新增
	$("#add").click(function(){
		$('#addModal').modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
            key : {
                required : true ,
                rangelength:[4,100],
                myValid01: true
            },
            title : {
            	required : true
            }
        }, 
        messages : {
            key : {
                required : '请输入配置Key'  ,
                rangelength : "配置Key长度限制为4~100"
            },
            title : {
                required : '请输入配置描述'
			}
        }, 
		highlight : function(element) {  
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		$.post(base_url + "/conf/add", {
    			'appname' 	: $("#addModal .form input[name='appname']").val() ,
                'key' 		: ($("#addModal .form input[name='appname']").val() + '.' + $("#addModal .form input[name='key']").val() ),
                'title' 	: $("#addModal .form input[name='title']").val() ,
                'value' 	: $("#addModal .form textarea[name='value']").val() ,
			}, function(data, status) {
                if (data.code == 200) {
                    layer.open({
                        icon: '1',
                        content: '新增成功' ,
                        end: function(layero, index){
                            confTable.fnDraw();
                            $('#addModal').modal('hide');
                        }
                    });
                } else {
                    layer.open({
                        icon: '2',
                        content: (data.msg||'新增失败')
                    });
                }
    		});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset()
	});
	
	// 更新
	$("#conf_list").on('click', '.update',function() {

        var key = $(this).parent('p').attr("key");
        var row = confData[key];

        $("#updateModal .form input[name='env']").val( row.env );
		$("#updateModal .form input[name='key']").val( row.key );
		$("#updateModal .form input[name='appname']").val( row.appname );
        $("#updateModal .form input[name='title']").val( row.title );
		$("#updateModal .form textarea[name='value']").val( row.value );

		$('#updateModal').modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
            key : {
                required : true ,
                rangelength:[4,100],
                myValid01: true
            },
            title : {
                required : true
            }
        },
        messages : {
            key : {
                required : '请输入配置Key'  ,
                rangelength : "配置Key长度限制为4~100"
            },
            title : {
                required : '请输入配置描述'
            }
        },
        highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		$.post(base_url + "/conf/update", $("#updateModal .form").serialize(), function(data, status) {
                if (data.code == 200) {
                    layer.open({
                        icon: '1',
                        content: '更新成功' ,
                        end: function(layero, index){
                            confTable.fnDraw();
                            $('#updateModal').modal('hide');
                        }
                    });
                } else {
                    layer.open({
                        icon: '2',
                        content: (data.msg||'更新失败')
                    });
                }
    		});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset()
	});

	// 全量同步
    /*$('#syncConf').click(function () {
        layer.confirm( "确定要进行全量同步操作，将会检测该项目下的全部未同步配置项，使用DB中配置数据覆盖ZK中配置数据并推送更新？" , {
            icon: 3,
            title: '系统提示' ,
            btn: [ '确定', '取消' ]
        }, function(index){
            layer.close(index);

            $.post(
                base_url + "/conf/syncConf",
                {
                    "appname" : $('#appname').val()
                },
                function(data, status) {
                    if (data.code == 200) {
                        layer.open({
                            icon: '1',
                            content: (data.msg || '操作成功') ,
                            end: function(layero, index){
                                confTable.fnDraw();
                            }
                        });
                    } else {
                        layer.open({
                            icon: '2',
                            content: (data.msg||'操作失败')
                        });
                    }
                }
            );

        });
    });*/
	
});
