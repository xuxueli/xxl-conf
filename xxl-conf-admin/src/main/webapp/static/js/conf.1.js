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
				obj.nodeGroup = $('#nodeGroup').val();
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
			{ "data": 'nodeGroup', "visible" : false},
			{ "data": 'nodeKey', "visible" : false},
			{ "data": 'groupKey', "visible" : true},
			{
				"data": 'nodeValue',
				"visible" : true,
				"render": function ( data, type, row ) {
					if (row.nodeValue == row.nodeValueReal) {
						var temp = (row.nodeValue.length > 20)? row.nodeValue.substring(0, 20)+'...' : row.nodeValue;
						return "<span title='"+ row.nodeValue +"'>"+ temp +"</span>";;
					} else {
						var tips = "Mysql:<hr>"+ row.nodeValue +"<br><br>ZK:<hr>"+ row.nodeValueReal +"</span>";
						var html = "<span style='color: red'>数据未同步: <a href='javascript:;' class='tecTips' tips='"+ tips +"'>查看</a></span>";
						return html;
					}
				}
			},
			{ "data": 'nodeValueReal', "visible" : false},
			{ "data": 'nodeDesc', "visible" : true},
			{ "data": '操作' ,
				"render": function ( data, type, row ) {
					return function(){
						// html
						var html = '<p id="'+ row.id +'" '+
							' nodeGroup="'+ row.nodeGroup +'" '+
							' nodeKey="'+ row.nodeKey +'" '+
							' nodeValue="'+ row.nodeValue +'" '+
							' nodeValueReal="'+ row.nodeValueReal +'" '+
							' nodeDesc="'+ row.nodeDesc +'" '+
							'>'+
							'<textarea name="nodeValue" style="display:none;" >'+ row.nodeValue +'</textarea>  '+
							'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
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

	$("#conf_list").on('click', '.tecTips',function() {
		var tips = $(this).attr("tips");
		ComAlertTec.show(tips);
	});
	
	// 删除
	$("#conf_list").on('click', '.delete',function() {
		var nodeGroup = $(this).parent('p').attr("nodeGroup");
		var nodeKey = $(this).parent('p').attr("nodeKey");
		ComConfirm.show("确定要删除配置：" + nodeKey, function(){
			$.post(
				base_url + "/conf/delete",
				{
					"nodeGroup" : nodeGroup,
					"nodeKey" : nodeKey
				},
				function(data, status) {
					if (data.code == "200") {
						ComAlert.show(1, "删除成功", function(){
							confTable.fnDraw();
						});
					} else {
						ComAlert.show(2, data.msg);
					}
				}
			);
		});
	});

    // jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
    jQuery.validator.addMethod("myValid01", function(value, element) {
        var length = value.length;
        var valid = /^[a-z][a-z0-9.]*$/;
        return this.optional(element) || valid.test(value);
    }, "KEY只能由小写字母、数字和.组成,须以小写字母开头");

	// 新增
	$("#add").click(function(){
		$('#addModal').modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	nodeKey : {
        		required : true ,
                minlength: 4,
                maxlength: 100,
                myValid01: true
            },  
            nodeValue : {
            	required : false
            },
            nodeDesc : {
            	required : false
            }
        }, 
        messages : {
        	nodeKey : {
        		required :'请输入"KEY".'  ,
                minlength:'"KEY"不应低于4位',
                maxlength:'"KEY"不应超过100位'
            },  
            nodeValue : {	},
            nodeDesc : {	}
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
    		$.post(base_url + "/conf/add", $("#addModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				ComAlert.show(1, "新增配置成功", function(){
						confTable.fnDraw();
						$('#addModal').modal('hide');
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
	$("#conf_list").on('click', '.update',function() {

		$("#updateModal .form input[name='nodeGroup']").val( $(this).parent('p').attr("nodeGroup") );
		$("#updateModal .form input[name='nodeKey']").val( $(this).parent('p').attr("nodeKey") );
		//$("#updateModal .form input[name='nodeValueReal']").val( $(this).parent('p').attr("nodeValueReal") );
		//$("#updateModal .form textarea[name='nodeValue']").val( $(this).parent('p').attr("nodeValue") );
		$("#updateModal .form textarea[name='nodeValue']").val( $(this).parent('p').find("textarea[name='nodeValue']").val() );
		$("#updateModal .form input[name='nodeDesc']").val( $(this).parent('p').attr("nodeDesc") );

		$('#updateModal').modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
		rules : {
			nodeKey : {
				required : true ,
				minlength: 4,
				maxlength: 100
			},
			nodeValue : {
				required : false
			},
			nodeDesc : {
				required : false
			}
		},
		messages : {
			nodeKey : {
				required :'请输入"KEY".'  ,
				minlength:'"KEY"不应低于4位',
				maxlength:'"KEY"不应超过100位'
			},
			nodeValue : {	},
			nodeDesc : {	}
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
    			if (data.code == "200") {
    				ComAlert.show(1, "更新配置成功", function(){
						confTable.fnDraw();
						$('#updateModal').modal('hide');
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