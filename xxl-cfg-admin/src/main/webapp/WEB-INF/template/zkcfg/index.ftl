<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>xxl-cfg-admin</title> 

<#import "/common/common.macro.ftl" as netCommon>
<@netCommon.commonStyle />
<link rel="stylesheet" href="${request.contextPath}/static/lte/plugins/datatables/dataTables.bootstrap.css">

</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		
		<@netCommon.commonHeader />

		<@netCommon.commonLeft />

		<!-- Content Wrapper. Contains page content -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>配置管理 <small>分布式配置管理平台</small></h1>
				<ol class="breadcrumb">
					<li><a><i class="fa fa-dashboard"></i>配置中心</a></li>
					<li class="active">配置管理</li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">
			
				<!-- 单个配置:zk_cfg_version -->
              	<div class="box box-info">
                	<div class="box-body">
                		<div class="input-group">
                    		<span class="input-group-addon">键</span>
                    		<input type="text" class="form-control" id="znodeKey" placeholder="配置项：znodeKey" value="${znodeKey}" >
                    		<span class="input-group-btn">
	                      		<button class="btn btn-info btn-flat" id="query" type="button">查询</button>
	                    	</span>
                 		</div>
					</div>                	  	
				</div>
				
				<!-- 全部配置 -->
				<div class="box box-info">
					<div class="box-header with-border">
						<h3 class="box-title">全部配置：</h3>
						<button class="btn btn-primary btn-xs add" type="button" >新增</button>
					</div>
	                <div class="box-body">
	                  	<table id="all_config_datas" class="table table-bordered table-hover">
		                    <thead>
		                      	<tr>
			                        <th>键</th>
			                        <th>db值</th>
			                        <th>zk值</th>
			                        <th>描述</th>
			                        <th>操作</th>
		                      	</tr>
							</thead>
		                    <tbody>
		                    	<#if fileterData?exists>
		                    		<#list fileterData as item>
		                    			<tr>
					                        <td>${item.znodeKey}</td>
					                        <td <#if item.znodeValue != item.znodeValueReal>style="color:red;font: italic bold"</#if> >${item.znodeValue}</td>
					                        <td <#if item.znodeValue != item.znodeValueReal>style="color:red;font: italic bold"</#if> >${item.znodeValueReal}</td>
					                        <td>${item.znodeDesc}</td>
					                        <td>
					                        	<div class="input-group">
						                      		<button class="btn btn-primary btn-xs update" type="button" znodeKey="${item.znodeKey}" znodeValue="${item.znodeValue}" znodeDesc="${item.znodeDesc}" >更新</button>&nbsp;
						                      		<button class="btn btn-danger btn-xs delete" type="button" znodeKey="${item.znodeKey}">删除</button>
					                        	</div>
					                        </td>
				                      	</tr>
		                    		</#list>
		                    	</#if>
		                    </tbody>
		                    <tfoot>
		                      	<tr>
			                        <th>键</th>
			                        <th>db值</th>
			                        <th>zk值</th>
			                        <th>描述</th>
			                        <th>操作</th>
		                      	</tr>
		                    </tfoot>
	                  	</table>
					</div><!-- /.box-body -->
				</div><!-- /.box -->
				
			</section>
			<!-- /.content -->
			
		</div>
		<!-- /.content-wrapper -->

		<@netCommon.commonFooter />
		<@netCommon.commonControl />

	</div>
	<!-- ./wrapper -->
	
	<!-- 新增.模态框 -->
	<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
	            	<h4 class="modal-title" >新增配置</h4>
	         	</div>
	         	<div class="modal-body">
					<form class="form-horizontal form" role="form" >
						<div class="form-group">
							<label for="firstname" class="col-sm-2 control-label">键</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="znodeKey" placeholder="请输入zondeKey" minlength="4" maxlength="100" ></div>
						</div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">值</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="znodeValue" placeholder="请输入zondeValue" maxlength="100" ></div>
						</div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="znodeDesc" placeholder="请输入简介" maxlength="100" ></div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" class="btn btn-primary"  >保存</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
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
	            	<h4 class="modal-title" >更新配置</h4>
	         	</div>
	         	<div class="modal-body">
					<form class="form-horizontal form" role="form" >
						<div class="form-group">
							<label for="firstname" class="col-sm-2 control-label">键</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="znodeKey" placeholder="请输入zondeKey" minlength="4" maxlength="100" readonly ></div>
						</div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">值</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="znodeValue" placeholder="请输入zondeValue" maxlength="100" ></div>
						</div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="znodeDesc" placeholder="请输入简介" maxlength="100" ></div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" class="btn btn-primary"  >保存</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
							</div>
						</div>
					</form>
	         	</div>
			</div>
		</div>
	</div>

	<script>
		var base_url = '${request.contextPath}';
	</script>
	<@netCommon.commonScript/>
	<@netCommon.comAlert/>
    <script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
    <script src="${request.contextPath}/static/lte/plugins/datatables/jquery.dataTables.min.js"></script>
    <script src="${request.contextPath}/static/lte/plugins/datatables/dataTables.bootstrap.min.js"></script>
	<script src="${request.contextPath}/static/js/zkcfg.1.js"></script>
	
</body>
</html>
