$(function() {

	// remove
	$('.remove').on('click', function(){
		var appname = $(this).attr('appname');

        layer.confirm( "确认删除该项目?" , {
            icon: 3,
            title: '系统提示' ,
            btn: [ '确定', '取消' ]
        }, function(index){
            layer.close(index);

            $.ajax({
                type : 'POST',
                url : base_url + '/project/remove',
                data : {"appname":appname},
                dataType : "json",
                success : function(data){
                    if (data.code == 200) {
                        layer.open({
                            icon: '1',
                            content: '删除成功' ,
                            end: function(layero, index){
                                window.location.reload();
                            }
                        });

                    } else {
                        layer.open({
                            icon: '2',
                            content: (data.msg||'删除失败')
                        });
                    }
                },
            });

        });

	});


    // jquery.validate 自定义校验
    jQuery.validator.addMethod("myValid01", function(value, element) {
        var length = value.length;
        var valid = /^[a-z][a-z0-9-]*$/;
        return this.optional(element) || valid.test(value);
    }, "限制以小写字母开头，由小写字母、数字和中划线组成");


	$('.add').on('click', function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
            appname : {
				required : true,
				rangelength:[4,100],
				myValid01 : true
			},
            title : {
				required : true,
				rangelength:[4, 100]
			}
		},
		messages : {
            appname : {
				required :"请输入AppName",
				rangelength:"AppName长度限制为4~100"
			},
            title : {
				required :"请输入项目名称",
				rangelength:"长度限制为4~100"
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
			$.post(base_url + "/project/save",  $("#addModal .form").serialize(), function(data, status) {
                if (data.code == 200) {
                    $('#addModal').modal('hide');

                    layer.open({
                        icon: '1',
                        content: '新增成功' ,
                        end: function(layero, index){
                            window.location.reload();
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
		$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
	});

	$('.update').on('click', function(){
		$("#updateModal .form input[name='appname']").val($(this).attr("appname"));
		$("#updateModal .form input[name='title']").val($(this).attr("title"));

		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
        rules : {
            appname : {
                required : true,
                rangelength:[4,100],
                myValid01 : true
            },
            title : {
                required : true,
                rangelength:[4, 100]
            }
        },
        messages : {
            appname : {
                required :"请输入AppName",
                rangelength:"AppName长度限制为4~100"
            },
            title : {
                required :"请输入项目名称",
                rangelength:"长度限制为4~100"
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
			$.post(base_url + "/project/update",  $("#updateModal .form").serialize(), function(data, status) {
                if (data.code == 200) {
                    $('#addModal').modal('hide');

                    layer.open({
                        icon: '1',
                        content: '更新成功' ,
                        end: function(layero, index){
                            window.location.reload();
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
		$("#updateModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#updateModal .form .form-group").removeClass("has-error");
	});

	
});
