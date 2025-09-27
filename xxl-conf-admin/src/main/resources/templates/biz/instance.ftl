<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.min.css">
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/select2/select2.min.css">
	<style>
		/* select2 */
		.select2-container--default .select2-selection--single {
			border: 1px solid #d2d6de;
			border-radius: 0;
			height: 34px;
			padding: 6px 12px;
		}
	</style>
	<!-- 1-style end -->

</head>
<body class="hold-transition" style="background-color: #ecf0f5;">
<div class="wrapper">
	<section class="content">

		<!-- 2-content start -->

		<#-- 查询区域 -->
		<div class="box" style="margin-bottom:9px;">
			<div class="box-body">
				<div class="row" id="data_filter" >
					<div class="col-xs-3">
						<div class="input-group">
							<span class="input-group-addon">Env</span>
							<input type="text" class="form-control env" autocomplete="on" readonly >
						</div>
					</div>
					<div class="col-xs-5">
						<div class="input-group">
							<span class="input-group-addon">AppName</span>
							<input type="text" class="form-control appname" autocomplete="on" >
						</div>
					</div>
					<div class="col-xs-1">
						<button class="btn btn-block btn-primary searchBtn" >${I18n.system_search}</button>
					</div>
					<div class="col-xs-1">
						<button class="btn btn-block btn-default resetBtn" >${I18n.system_reset}</button>
					</div>
				</div>
			</div>
		</div>

		<#-- 数据表格区域 -->
		<div class="row">
			<div class="col-xs-12">
				<div class="box">
					<div class="box-header pull-left" id="data_operation" >
						<button class="btn btn-sm btn-info add" type="button"><i class="fa fa-plus" ></i>${I18n.system_opt_add}</button>
						<button class="btn btn-sm btn-warning selectOnlyOne update" type="button"><i class="fa fa-edit"></i>${I18n.system_opt_edit}</button>
						<button class="btn btn-sm btn-danger selectAny delete" type="button"><i class="fa fa-remove "></i>${I18n.system_opt_del}</button>
					</div>
					<div class="box-body" >
						<table id="data_list" class="table table-bordered table-striped" width="100%" >
							<thead></thead>
							<tbody></tbody>
							<tfoot></tfoot>
						</table>
					</div>
				</div>
			</div>
		</div>

		<!-- 新增.模态框 -->
		<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >${I18n.system_opt_add}注册节点</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">Env<font color="red">*</font></label>
								<div class="col-sm-6">
									<input type="text" class="form-control" name="env" readonly="readonly" >
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">AppName<font color="red">*</font></label>
								<div class="col-sm-9">
									<select class="form-control " style="width: 100%;" name="appname" >
										<#list applicationList as item>
											<option value="${item.appname}" >${item.appname}</option>
										</#list>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">注册Ip<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="ip" placeholder="${I18n.system_please_input}Ip" maxlength="46" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">注册端口<font color="red">*</font></label>
								<div class="col-sm-6"><input type="text" class="form-control" name="port" placeholder="${I18n.system_please_input}port" maxlength="10" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">注册模式<font color="red">*</font></label>
								<div class="col-sm-6">
									<select class="form-control" name="registerModel" >
										<#list InstanceRegisterModelEnum as item>
											<option value="${item.value}" >${item.desc}</option>
										</#list>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">扩展信息<font color="black">*</font></label>
								<div class="col-sm-9"><textarea type="text" class="form-control" name="extendInfo" placeholder="${I18n.system_please_input}" maxlength="500" ></textarea></div>
							</div>

							<div class="form-group" style="text-align:center;border-top: 1px solid #e4e4e4;">
								<div style="margin-top: 10px;" >
									<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
									<button type="button" class="btn btn-default" data-dismiss="modal">${I18n.system_cancel}</button>
								</div>
							</div>

						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 更新.模态框 -->
		<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >${I18n.system_opt_edit}注册节点</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">Env<font color="red">*</font></label>
								<div class="col-sm-6">
									<input type="text" class="form-control" name="env" readonly="readonly" >
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">AppName<font color="red">*</font></label>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="appname" readonly="readonly" >
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">注册Ip<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="ip" placeholder="${I18n.system_please_input}Ip" maxlength="46" readonly ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">注册端口<font color="red">*</font></label>
								<div class="col-sm-6"><input type="text" class="form-control" name="port" placeholder="${I18n.system_please_input}port" maxlength="10" readonly ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">注册模式<font color="red">*</font></label>
								<div class="col-sm-6">
									<select class="form-control" name="registerModel" >
										<#list InstanceRegisterModelEnum as item>
											<option value="${item.value}" >${item.desc}</option>
										</#list>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">扩展信息<font color="black">*</font></label>
								<div class="col-sm-9"><textarea type="text" class="form-control" name="extendInfo" placeholder="${I18n.system_please_input}" maxlength="500" ></textarea></div>
							</div>


							<div class="form-group" style="text-align:center;border-top: 1px solid #e4e4e4;">
								<div style="margin-top: 10px;" >
									<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
									<button type="button" class="btn btn-default" data-dismiss="modal">${I18n.system_cancel}</button>
									<input type="hidden" name="id" >
								</div>
							</div>

						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 2-content end -->

	</section>
</div>

<!-- 3-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2.min.js"></script>
<script>
	$(function() {

		// ---------------------- select2 ----------------------
		
		$("#addModal .form select[name='appname']").select2()
		$("#updateModal .form select[name='appname']").select2()

		// ---------------------- main table ----------------------

		// init filter : env
		function initFilter(){
			// valid
			if (!(typeof window.parent.findEnv === 'function')) {
				layer.msg('Env not found.');
				return;
			}

			// filter init
			let currentEnv = window.parent.findEnv();
			$("#data_filter .env").val( currentEnv );
		}
		initFilter();
		
		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/instance/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.appname = $('#data_filter .appname').val();
				obj.env = $('#data_filter .env').val();
				obj.start = params.offset;
				obj.length = params.limit;
				return obj;
			},
			resetHandler: function(data){
				// reset
				$('#data_filter input[type="text"]').val('');
				// init
				initFilter();
			},
			columns:[
				{
					checkbox: true,
					field: 'state',
					width: '5',
					widthUnit: '%',
					align: 'center',
					valign: 'middle'
				}, {
					title: 'Env',
					field: 'env',
					width: '10',
					widthUnit: '%',
					align: 'left'
				}, {
					title: 'AppName',
					field: 'appname',
					width: '15',
					widthUnit: '%',
					align: 'left'
				}, {
					title: 'IP:PORT',
					field: 'ip',
					width: '20',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						return row.ip + ":" + row.port
					}
				},{
					title: '注册模式',
					field: 'registerModel',
					width: '10',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						var ret = value;
						$("#addModal .form select[name='registerModel']").children("option").each(function() {
							if ($(this).val() === row.registerModel+"") {
								ret = $(this).html();
							}
						});
						return ret;
					}
				},{
					title: '最后注册心跳时间',
					field: 'registerHeartbeat',
					width: '15',
					widthUnit: '%'
				},{
					title: '扩展信息',
					field: 'extendInfo',
					width: '15',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						if (!value) {
							return value;
						}
						var result = value.length<10
								?value
								:value.substring(0, 10) + '...';
						return "<span title='"+ value +"'>"+ result +"</span>";
					}
				}
			]
		});

		/**
		 * init delete
		 */
		$.adminTable.initDelete({
			url: base_url + "/instance/delete"
		});

		/**
		 * init add
		 */
		$.adminTable.initAdd( {
			url: base_url + "/instance/insert",
			writeFormData: function(row) {

				// base data
				let currentEnv = window.parent.findEnv();
				$("#addModal .form input[name='env']").val( currentEnv );
			},
			rules : {
				ip : {
					required : true,
					rangelength:[9, 46]
				},
				port : {
					required : true,
					range:[1, 65535]
				}
			},
			messages : {
				ip : {
					required : I18n.system_please_input,
					rangelength: I18n.system_lengh_limit + "[9-46]"
				},
				port : {
					required : I18n.system_please_input,
					rangelength: I18n.system_num_range + "[1-65535]"
				}
			},
			readFormData: function() {
				// request
				return $("#addModal .form").serializeArray();
			}
		});

		/**
		 * init update
		 */
		$.adminTable.initUpdate( {
			url: base_url + "/instance/update",
			writeFormData: function(row) {

				// base data
				$("#updateModal .form input[name='id']").val( row.id );
				$("#updateModal .form input[name='env']").val( row.env );
				$("#updateModal .form input[name='appname']").val( row.appname );
				$("#updateModal .form input[name='ip']").val( row.ip );
				$("#updateModal .form input[name='port']").val( row.port );
				$("#updateModal .form select[name='registerModel']").val( row.registerModel );
				$("#updateModal .form textarea[name='extendInfo']").val( row.extendInfo );
			},
			rules : {
				ip : {
					required : true,
					rangelength:[9, 46]
				},
				port : {
					required : true,
					range:[1, 65535]
				}
			},
			messages : {
				ip : {
					required : I18n.system_please_input,
					rangelength: I18n.system_lengh_limit + "[9-46]"
				},
				port : {
					required : I18n.system_please_input,
					rangelength: I18n.system_num_range + "[1-65535]"
				}
			},
			readFormData: function() {
				// request
				return $("#updateModal .form").serializeArray();
			}
		});

	});

</script>
<!-- 3-script end -->

</body>
</html>