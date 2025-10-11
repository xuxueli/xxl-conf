<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.min.css">
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
					<div class="col-xs-6">
						<div class="input-group">
							<span class="input-group-addon">AccessToken</span>
							<input type="text" class="form-control accessToken" autocomplete="on" >
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
						<h4 class="modal-title" >${I18n.system_opt_add}AccessToken</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">AccessToken<font color="black">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="accessToken" placeholder="${I18n.system_please_input}AccessToken" maxlength="50" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">生效状态<font color="red">*</font></label>
								<div class="col-sm-9">
									<select class="form-control" name="status" >
										<#list AccessTokenStatuEnum as item>
											<option value="${item.value}" >${item.desc}</option>
										</#list>
									</select>
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
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >${I18n.system_opt_edit}AccessToken</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">AccessToken<font color="black">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="accessToken" placeholder="${I18n.system_please_input}AccessToken" maxlength="50" readonly ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-3 control-label">生效状态<font color="red">*</font></label>
								<div class="col-sm-9">
									<select class="form-control" name="status" >
										<#list AccessTokenStatuEnum as item>
											<option value="${item.value}" >${item.desc}</option>
										</#list>
									</select>
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
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script>
	$(function() {

		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/accesstoken/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.accessToken = $('#data_filter .accessToken').val();
				obj.offset = params.offset;
				obj.pagesize = params.limit;
				return obj;
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
					title: 'AccessToken',
					field: 'accessToken',
					width: '40',
					widthUnit: '%',
					align: 'left'
				},{
					title: '生效状态',
					field: 'status',
					width: '20',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						var ret = value;
						$("#addModal .form select[name='status']").children("option").each(function() {
							if ($(this).val() === row.status+"") {
								ret = $(this).html();
							}
						});
						return ret;
					}
				}, {
					title: '创建时间',
					field: 'addTime',
					width: '30',
					widthUnit: '%',
					align: 'left'
				}
			]
		});

		/**
		 * init delete
		 */
		$.adminTable.initDelete({
			url: base_url + "/accesstoken/delete"
		});

		/**
		 * init add
		 */
		// add validator method
		jQuery.validator.addMethod("accessTokenValid", function(value, element) {
			var valid = /^[a-z][a-z0-9]*$/;
			return this.optional(element) || valid.test(value);
		}, '限制小写字母开头，由小写字母、数字组成' );
		$.adminTable.initAdd( {
			url: base_url + "/accesstoken/insert",
			rules : {
				accessToken : {
					required : true,
					rangelength:[4, 50],
					accessTokenValid: true
				}
			},
			messages : {
				accessToken : {
					required : I18n.system_please_input,
					rangelength: I18n.system_lengh_limit + "[4-50]"
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
			url: base_url + "/accesstoken/update",
			writeFormData: function(row) {

				// base data
				$("#updateModal .form input[name='id']").val( row.id );
				$("#updateModal .form select[name='status']").val( row.status );
				$("#updateModal .form input[name='accessToken']").val( row.accessToken );
			},
			rules : {
				accessToken : {
					required : false,
					rangelength:[4, 50],
					accessTokenValid: true
				}
			},
			messages : {
				accessToken : {
					required : I18n.system_please_input,
					rangelength: I18n.system_lengh_limit + "[4-50]"
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