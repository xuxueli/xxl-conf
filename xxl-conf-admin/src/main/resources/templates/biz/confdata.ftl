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
					<div class="col-xs-2">
						<div class="input-group">
							<span class="input-group-addon">Env</span>
							<input type="text" class="form-control env" autocomplete="on" readonly >
						</div>
					</div>
					<div class="col-xs-3">
						<div class="input-group">
							<span class="input-group-addon">AppName</span>
							<select class="form-control " style="width: 100%;" name="appname" >
								<#list applicationList as item>
									<option value="${item.appname}" >${item.appname} (${item.name})</option>
								</#list>
							</select>
						</div>
					</div>
					<div class="col-xs-5">
						<div class="input-group">
							<span class="input-group-addon">配置Key</span>
							<input type="text" class="form-control key" autocomplete="on" >
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
						<button class="btn btn-sm btn-primary selectOnlyOne confDataLog" type="button"><i class="fa fa-remove ">变更记录</i></button>
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
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >${I18n.system_opt_add}配置数据</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">Env<font color="red">*</font></label>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="env" readonly >
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
								<div class="col-sm-9">
									<select class="form-control " style="width: 100%;" name="appname" >
										<#list applicationList as item>
											<option value="${item.appname}" >${item.appname}</option>
										</#list>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">配置Key<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="key" placeholder="${I18n.system_please_input}配置key" maxlength="200" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">配置描述<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="desc" placeholder="${I18n.system_please_input}配置描述" maxlength="100" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">配置Value<font color="black">*</font></label>
								<div class="col-sm-9">
									<textarea type="text" class="form-control" name="value" id="addConfDataIDE" maxlength="3000" style="height: 150px;" ></textarea>
								</div>
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
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >${I18n.system_opt_edit}配置数据</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">Env<font color="red">*</font></label>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="env" readonly >
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="appname" readonly >
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">配置Key<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="key" placeholder="${I18n.system_please_input}配置key" maxlength="200" readonly ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">配置描述<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="desc" placeholder="${I18n.system_please_input}配置描述" maxlength="100" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">配置Value<font color="black">*</font></label>
								<div class="col-sm-9">
									<textarea type="text" class="form-control" name="value" placeholder="${I18n.system_please_input}" maxlength="3000" style="height: 150px;" ></textarea>
								</div>
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
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2.min.js"></script>
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script src="${request.contextPath}/static/biz/common/admin.util.js"></script>
<script>
	$(function() {

		// ---------------------- select2 ----------------------

		$("#addModal .form select[name='appname']").select2();
		$("#updateModal .form select[name='appname']").select2();
		$("#data_filter select[name='appname']").select2();

		// ---------------------- main table ----------------------

		// init filter : env
		function initFilter(){
			let currentEnv = window.parent.findEnv();
			$("#data_filter .env").val( currentEnv );
		}
		initFilter();

		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/confdata/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.env = $('#data_filter .env').val();
				obj.appname = $("#data_filter select[name='appname']").val();
				obj.key = $('#data_filter .key').val();
				obj.start = params.offset;
				obj.length = params.limit;
				return obj;
			},
			resetHandler: function(data){
				// reset
				$('#data_filter input[type="text"]').val('');
				$('#data_filter select').each(function() {
					$(this).prop('selectedIndex', 0);
				});
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
					title: '配置Key',
					field: 'key',
					width: '20',
					widthUnit: '%',
					align: 'left'
				},{
					title: '配置Value',
					field: 'value',
					width: '20',
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
				},{
					title: '配置说明',
					field: 'desc',
					width: '20',
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
			url: base_url + "/confdata/delete"
		});

		/**
		 * init add
		 */
		// add validator method
		jQuery.validator.addMethod("confKeyValid", function(value, element) {
			var valid = /^[a-z][a-z0-9.]*$/;
			return this.optional(element) || valid.test(value);
		}, '限制小写字母开头，由小写字母、数字和点组成' );
		$.adminTable.initAdd( {
			url: base_url + "/confdata/insert",
			writeFormData: function(row) {

				// base data
				let currentEnv = window.parent.findEnv();
				$("#addModal .form input[name='env']").val( currentEnv );

				// set appname (select2)
				let appname = $("#data_filter select[name='appname']").val();
				$("#addModal .form select[name='appname']").val(appname).trigger("change");

			},
			rules : {
				desc : {
					required : true
				},
				key : {
					required : true,
					confKeyValid:true
				}
			},
			messages : {
				desc : {
					required : I18n.system_please_input
				},
				key : {
					required : I18n.system_please_input
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
			url: base_url + "/confdata/update",
			writeFormData: function(row) {

				// base data
				$("#updateModal .form input[name='id']").val( row.id );
				$("#updateModal .form input[name='env']").val( row.env );
				$("#updateModal .form input[name='appname']").val( row.appname );
				$("#updateModal .form input[name='key']").val( row.key );
				$("#updateModal .form textarea[name='value']").val( row.value );
				$("#updateModal .form input[name='desc']").val( row.desc );
			},
			rules : {
				desc : {
					required : true
				},
				key : {
					required : true,
					confKeyValid:true
				}
			},
			messages : {
				desc : {
					required : I18n.system_please_input
				},
				key : {
					required : I18n.system_please_input
				}
			},
			readFormData: function() {
				// request
				return $("#updateModal .form").serializeArray();
			}
		});

		// ---------- ---------- ---------- confDataLog ---------- ---------- ----------

		$("#data_operation .confDataLog").click(function(){
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');
			if (rows.length !== 1) {
				layer.msg(I18n.system_please_choose + I18n.system_one + I18n.system_data);
				return;
			}
			var row = rows[0];

			// open tab
			let url = base_url + '/confdatalog?dataId=' + row.id;
			openTab(url, '配置变更日志', false);

		});

	});

</script>
<!-- 3-script end -->

</body>
</html>