$(function(){

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
				obj.nodeKey = $('#nodeKey').val();
				obj.start = d.start;
				obj.length = d.length;
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'id', "bSortable": false, "visible" : false},
			{ "data": 'nodeKey', "visible" : true},
			{ "data": 'nodeValue', "visible" : true},
			{ "data": 'nodeValueReal', "visible" : true},
			{ "data": 'nodeDesc', "visible" : true},
			{ "data": '操作' ,
				"render": function ( data, type, row ) {
					return function(){
						// html
						var html = '<p id="'+ row.id +'" '+
							' key="'+ row.key +'" '+
							' intro="'+ row.intro +'" '+
							'>'+
							'<button class="btn btn-primary btn-xs cache_manage" type="button">缓存操作</button>  '+
							'<button class="btn btn-warning btn-xs cache_update" type="button">编辑</button>  '+
							'<button class="btn btn-danger btn-xs cache_delete" type="button">删除</button>  '+
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
	
	// 删除
	$(".delete").click(function(){
		var znodeKey = $(this).attr("znodeKey");
		if (!znodeKey) {
			ComAlert.show(2, "znodeKey不可为空");
			return;
		}
		ComConfirm.show("确定要删除：" + znodeKey, function(){
			$.post(base_url + "/zkcfg/delete", {znodeKey:znodeKey}, function(data, status) {
				if (data.code == "S") {
					ComAlert.show(1, "删除成功", function(){
						$("#query").click();
					});
				} else {
					ComAlert.show(2, data.msg);
				}
			});
		});
	});
	
	// 新增
	$("#add").click(function(){
		$('#addModal').modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	znodeKey : {  
        		required : true ,
                minlength: 4,
                maxlength: 100
            },  
            znodeValue : {  
            	required : false ,
                maxlength: 100
            },  
            znodeDesc : {  
            	required : false ,
                maxlength: 100
            }
        }, 
        messages : {  
        	znodeKey : {  
        		required :"请输入zondeKey."  ,
                minlength:"密码不应低于4位",
                maxlength:"密码不应超过100位"
            },  
            znodeValue : {
            	required :"请输入znodeValue."  ,
                maxlength:"密码不应超过100位"
            },  
            znodeDesc : {
                maxlength:"密码不应超过100位"
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
    		$.post(base_url + "/zkcfg/setData", $("#addModal .form").serialize(), function(data, status) {
    			if (data.code == "S") {
    				ComAlert.show(1, "新增配置成功", function(){
    					$("#query").click();
    				});
    			} else {
    				ComAlert.show(2, data.msg);
    			}
    		});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset()
	});
	
	// 更新
	$(".update").click(function(){
		$("#updateModal .form input[name='znodeKey']").val($(this).attr("znodeKey"));
		$("#updateModal .form input[name='znodeValue']").val($(this).attr("znodeValue"));
		$("#updateModal .form input[name='znodeDesc']").val($(this).attr("znodeDesc"));
		$('#updateModal').modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	znodeKey : {  
        		required : true ,
                minlength: 4,
                maxlength: 100
            },  
            znodeValue : {  
            	required : false ,
                maxlength: 100
            },  
            znodeDesc : {  
            	required : false ,
                maxlength: 100
            }
        }, 
        messages : {  
        	znodeKey : {  
        		required :"请输入zondeKey."  ,
                minlength:"密码不应低于4位",
                maxlength:"密码不应超过100位"
            },  
            znodeValue : {
            	required :"请输入znodeValue."  ,
                maxlength:"密码不应超过100位"
            },  
            znodeDesc : {
                maxlength:"密码不应超过100位"
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
    		$.post(base_url + "/zkcfg/setData", $("#updateModal .form").serialize(), function(data, status) {
    			if (data.code == "S") {
    				ComAlert.show(1, "更新配置成功", function(){
    					$("#query").click();
    				});
    			} else {
    				ComAlert.show(2, data.msg);
    			}
    		});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset()
	});
	
});