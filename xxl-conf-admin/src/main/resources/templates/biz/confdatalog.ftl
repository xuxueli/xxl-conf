<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>
	<#-- commonStyle -->

	<#-- biz start（1/5 style） -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/select2/dist/css/select2.min.css">
	<#-- biz end（1/5 end） -->

	<@netCommon.commonStyle />

</head>
<body class="hold-transition skin-blue sidebar-mini" >
<div class="wrapper">

	<!-- header -->
	<@netCommon.commonHeader />

	<!-- left -->
	<#-- biz start（2/5 left） -->
	<@netCommon.commonLeft "/confdata" />
	<#-- biz end（2/5 left） -->

	<!-- right start -->
	<div class="content-wrapper">

		<!-- content-header -->
		<section class="content-header">
			<#-- biz start（3/5 name） -->
			<h1>配置变更日志</h1>
			<#-- biz end（3/5 name） -->
		</section>

		<!-- content-main -->
		<section class="content">

			<#-- biz start（4/5 content） -->

			<#-- 查询区域 -->
			<div class="box" style="margin-bottom:9px;">
				<div class="box-body">
					<div class="row" id="data_filter" >
						<div class="col-xs-2">
							<div class="input-group">
								<span class="input-group-addon">Env</span>
								<input type="text" class="form-control" value="${confData.env}" disabled/>
							</div>
						</div>
						<div class="col-xs-3">
							<div class="input-group">
								<span class="input-group-addon">AppName</span>
								<input type="text" class="form-control" value="${confData.appname}" disabled/>
							</div>
						</div>
						<div class="col-xs-4">
							<div class="input-group">
								<span class="input-group-addon">配置Key</span>
								<input type="text" class="form-control" value="${confData.key}" disabled/>
							</div>
						</div>
						<div class="col-xs-1">
							<button class="btn btn-block btn-primary searchBtn" >${I18n.system_search}</button>
							<input type="hidden" id="dataId" value="${confData.id}">
						</div>
					</div>
				</div>
			</div>

			<#-- 数据表格区域 -->
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
						<div class="box-header" style="float: right" id="data_operation" >
							<#--<button class="btn btn-sm btn-info add" type="button"><i class="fa fa-plus" ></i>${I18n.system_opt_add}</button>
							<button class="btn btn-sm btn-warning selectOnlyOne update" type="button"><i class="fa fa-edit"></i>${I18n.system_opt_edit}</button>
							<button class="btn btn-sm btn-danger selectAny delete" type="button"><i class="fa fa-remove "></i>${I18n.system_opt_del}</button>-->
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

			<#-- biz end（4/5 content） -->

		</section>

	</div>
	<!-- right end -->

	<!-- footer -->
	<@netCommon.commonFooter />
</div>
<@netCommon.commonScript />

<#-- biz start（5/5 script） -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/dist/js/select2.full.min.js"></script>

<script src="${request.contextPath}/static/js/common/datatables.select.js"></script>
<script src="${request.contextPath}/static/js/biz/confdatalog.js"></script>
<#-- biz end（5/5 script） -->

</body>
</html>