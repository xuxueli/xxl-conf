$(function(){
	
	$('#all_config_datas').DataTable({
	    "paging": true,
	    "lengthChange": false,
	    "searching": false,
	    "ordering": true,
	    "info": true,
	    "autoWidth": false,
	    "language": {
	        "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
	        "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
	        "sEmptyTable": "表中数据为空",
	        "oPaginate": {
	            "sFirst": "首页",
	            "sPrevious": "上页",
	            "sNext": "下页",
	            "sLast": "末页"
	        }
	    }
	});
	
	$("#query").click(function(){
		var znodeKey = $("#znodeKey").val();
		window.location.href = base_url + "/zkcfg?znodeKey=" + znodeKey;
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
	$(".add").click(function(){
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