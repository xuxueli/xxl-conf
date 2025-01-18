$(function() {

	// input iCheck
	$('input').iCheck({
		checkboxClass: 'icheckbox_square-blue',
		radioClass: 'iradio_square-blue',
	});

	// ---------- ---------- ---------- main table  ---------- ---------- ----------
	// init date tables
	$.dataTableSelect.init();
	var mainDataTable = $("#data_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/user/pageList",
			type:"post",
			// request data
	        data : function ( d ) {
	        	var obj = {};
                obj.username = $('#data_filter .username').val();
                obj.status = $('#data_filter .status').val();
	        	obj.start = d.start;
	        	obj.length = d.length;
                return obj;
            },
			// response data filter
			dataFilter: function (originData) {
				var originJson = $.parseJSON(originData);
				return JSON.stringify({
					recordsTotal: originJson.data.totalCount,
					recordsFiltered: originJson.data.totalCount,
					data: originJson.data.pageData
				});
			}
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,																		// scroll x，close self-adaption
		//"dom": '<"top" t><"bottom" <"col-sm-3" i><"col-sm-3 right" l><"col-sm-6" p> >',		// dataTable "DOM layout"：https://datatables.club/example/diy.html
		"drawCallback": function( settings ) {
			$.dataTableSelect.selectStatusInit();
		},
	    "columns": [
					{
						"title": '<input align="center" type="checkbox" id="checkAll" >',
						"data": 'id',
						"visible" : true,
						"width":'5%',
						"render": function ( data, type, row ) {
							tableData['key'+row.id] = row;
							return '<input align="center" type="checkbox" class="checkItem" data-id="'+ row.id +'"  >';
						}
					},
	                {
						"title": I18n.user_username,
	                	"data": 'username',
						"width":'30%'
					},
	                {
						"title": I18n.user_password,
						"data": 'password',
                        "width":'20%',
                        "render": function ( data, type, row ) {
                            return '*********';
                        }
					},
					{
						"title": '真实姓名',
						"data": 'realName',
						"width":'25%'
					},
					{
						"title": '启用状态',
						"data": 'status',
						"visible" : true,
						"width":'20%',
                        "render": function ( data, type, row ) {
							var result = "";
							$('#data_filter .status option').each(function(){
								if ( data.toString() === $(this).val() ) {
									result = $(this).text();
								}
							});
							return result;
                        }
					}
	            ],
		"language" : {
			"sProcessing" : I18n.dataTable_sProcessing ,
			"sLengthMenu" : I18n.dataTable_sLengthMenu ,
			"sZeroRecords" : I18n.dataTable_sZeroRecords ,
			"sInfo" : I18n.dataTable_sInfo ,
			"sInfoEmpty" : I18n.dataTable_sInfoEmpty ,
			"sInfoFiltered" : I18n.dataTable_sInfoFiltered ,
			"sInfoPostFix" : "",
			"sSearch" : I18n.dataTable_sSearch ,
			"sUrl" : "",
			"sEmptyTable" : I18n.dataTable_sEmptyTable ,
			"sLoadingRecords" : I18n.dataTable_sLoadingRecords ,
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : I18n.dataTable_sFirst ,
				"sPrevious" : I18n.dataTable_sPrevious ,
				"sNext" : I18n.dataTable_sNext ,
				"sLast" : I18n.dataTable_sLast
			},
			"oAria" : {
				"sSortAscending" : I18n.dataTable_sSortAscending ,
				"sSortDescending" : I18n.dataTable_sSortDescending
			}
		}
	});

    // table data
    var tableData = {};

	// search btn
	$('#data_filter .searchBtn').on('click', function(){
        mainDataTable.fnDraw();
	});

	// ---------- ---------- ---------- delete operation ---------- ---------- ----------
	// delete
	$("#data_operation").on('click', '.delete',function() {

		// find select ids
		var selectIds = $.dataTableSelect.selectIdsFind();
		if (selectIds.length <= 0) {
			layer.msg(I18n.system_please_choose + I18n.system_data);
			return;
		}

		// do delete
		layer.confirm( I18n.system_ok + I18n.system_opt_del + '?', {
			icon: 3,
			title: I18n.system_tips ,
            btn: [ I18n.system_ok, I18n.system_cancel ]
		}, function(index){
			layer.close(index);

			$.ajax({
				type : 'POST',
				url : base_url + "/user/delete",
				data : {
					"ids" : selectIds
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
                        layer.msg( I18n.system_opt_del + I18n.system_success );
						mainDataTable.fnDraw(false);	// false，refresh current page；true，all refresh
					} else {
                        layer.msg( data.msg || I18n.system_opt_del + I18n.system_fail );
					}
				},
				error: function(xhr, status, error) {
					// Handle error
					console.log("Error: " + error);
					layer.open({
						icon: '2',
						content: (I18n.system_opt_del + I18n.system_fail)
					});
				}
			});
		});
	});

	// ---------- ---------- ---------- add operation ---------- ---------- ----------
	// add validator method
	jQuery.validator.addMethod("usernameValid", function(value, element) {
		var length = value.length;
		var valid = /^[a-z][a-z0-9]*$/;
		return this.optional(element) || valid.test(value);
	}, I18n.user_username_valid );
	// add
	$("#data_operation .add").click(function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
            username : {
				required : true,
                rangelength:[4, 20],
				usernameValid: true
			},
            password : {
                required : true,
                rangelength:[4, 20]
            },
			realName : {
				required : true,
				rangelength:[2, 20]
			}
        }, 
        messages : {
            username : {
            	required : I18n.system_please_input + I18n.user_username,
                rangelength: I18n.system_lengh_limit + "[4-20]"
            },
            password : {
                required : I18n.system_please_input + I18n.user_password,
                rangelength: I18n.system_lengh_limit + "[4-20]"
            },
			realName : {
				required : I18n.system_please_input + I18n.user_real_name,
				rangelength: I18n.system_lengh_limit + "[2-20]"
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

			// request
			var paramData = {
				"username": $("#addModal .form input[name=username]").val(),
                "password": $("#addModal .form input[name=password]").val(),
                "status": $("#addModal .form select[name=status]").val(),
				"realName": $("#addModal .form input[name=realName]").val(),
				"role": $('#addModal .form input[name="role"]:checked').val()
			};

			// post
        	$.post(base_url + "/user/add", paramData, function(data, status) {
    			if (data.code == "200") {
					$('#addModal').modal('hide');

                    layer.msg( I18n.system_opt_add + I18n.system_success );
                    mainDataTable.fnDraw();
    			} else {
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: (data.msg || I18n.system_opt_add + I18n.system_fail ),
						icon: '2'
					});
    			}
    		});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		addModalValidate.resetForm();

		$("#addModal .form")[0].reset();
		$("#addModal .form .form-group").removeClass("has-error");
	});

	// ---------- ---------- ---------- update operation ---------- ---------- ----------
	$("#data_operation .update").click(function(){

		// find select ids
		var selectIds = $.dataTableSelect.selectIdsFind();
		if (selectIds.length != 1) {
			layer.msg(I18n.system_please_choose + I18n.system_one + I18n.system_data);
			return;
		}
		var row = tableData[ 'key' + selectIds[0] ];

		// base data
		$("#updateModal .form input[name='id']").val( row.id );
		$("#updateModal .form input[name='username']").val( row.username );
		$("#updateModal .form input[name='password']").val( '' );
		$("#updateModal .form select[name='status']").val( row.status );
		$("#updateModal .form input[name='realName']").val( row.realName );
		$('#updateModal .form input[name="role"][value="'+ row.role +'"]').prop('checked', true).iCheck('update');

		// show
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
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
		rules : {
			realName : {
				required : true,
				rangelength:[2, 20]
			}
		},
		messages : {
			realName : {
				required : I18n.system_please_input + I18n.user_real_name,
				rangelength: I18n.system_lengh_limit + "[2-20]"
			}
		},
        submitHandler : function(form) {

			/*var role = $('#updateModal .form input[name="role"]:checked').map(function() {
				return this.value;
			}).get();*/

			// request
            var paramData = {
                "id": $("#updateModal .form input[name=id]").val(),
                "username": $("#updateModal .form input[name=username]").val(),
                "password": $("#updateModal .form input[name=password]").val(),
				"status": $("#updateModal .form select[name=status]").val(),
				"realName": $("#updateModal .form input[name=realName]").val(),
				"role": $('#updateModal .form input[name="role"]:checked').val()
            };

            $.post(base_url + "/user/update", paramData, function(data, status) {
                if (data.code == "200") {
                    $('#updateModal').modal('hide');

                    layer.msg( I18n.system_opt_edit + I18n.system_success );
					mainDataTable.fnDraw(false);
                } else {
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (data.msg || I18n.system_opt_edit + I18n.system_fail ),
                        icon: '2'
                    });
                }
            });
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		// reset checkbox
		$('#updateModal .form input[name="role"]').prop('checked', false).iCheck('update');

		// reset
		updateModalValidate.resetForm();

		$("#updateModal .form")[0].reset();
        $("#updateModal .form .form-group").removeClass("has-error");
	});

	// ---------- ---------- ---------- grant permission ---------- ---------- ----------
	$("#data_operation .grantPermission").click(function(){

		// find select ids
		var selectIds = $.dataTableSelect.selectIdsFind();
		if (selectIds.length != 1) {
			layer.msg(I18n.system_please_choose + I18n.system_one + I18n.system_data);
			return;
		}
		var row = tableData[ 'key' + selectIds[0] ];

		// fill data
		$("#grantPermissionModal .form input[name='username']").val(row.username)
		var permissionDataChoose;
		if (row.permission) {
			permissionDataChoose = $(row.permission.split(","));
		}
		$("#grantPermissionModal .form input[name='application']").each(function () {
			if ( $.inArray($(this).val(), permissionDataChoose) > -1 ) {
				$(this).prop("checked",true).iCheck('update');
			} else {
				$(this).prop("checked",false).iCheck('update');
			}
		});

		// show
		$('#grantPermissionModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	$('#grantPermissionModal .ok').click(function () {

		// find select application arr
		var username = $("#grantPermissionModal .form input[name='username']").val();
		var selectedApplications = [];
		$('#grantPermissionModal .form input[name="application"]:checked').each(function () {
			selectedApplications.push($(this).val());
		});

		// post
		$.post(base_url + "/user/grantPermission",
			{
				username: username,
				permission: selectedApplications.join(",")
			},
			function(data, status) {
			if (data.code == 200) {
				layer.open({
					icon: '1',
					content: '操作成功' ,
					end: function(layero, index){
						$('#grantPermissionModal').modal('hide');
					}
				});
			} else {
				layer.open({
					icon: '2',
					content: (data.msg||'操作失败')
				});
			}
		});
	});
	$("#grantPermissionModal").on('hide.bs.modal', function () {
		$("#grantPermissionModal .form")[0].reset();
		$("#grantPermissionModal .form .form-group").removeClass("has-error");
	});


});
