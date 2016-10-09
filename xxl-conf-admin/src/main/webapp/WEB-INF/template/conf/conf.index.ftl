<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>分布式配置管理平台</title>

<#import "/common/common.macro.ftl" as netCommon>
<@netCommon.commonStyle />
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
	<div class="wrapper">
		
		<@netCommon.commonHeader />

		<@netCommon.commonLeft />

		<!-- Content Wrapper. Contains page content -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>配置管理 <small></small></h1>
				<#--<ol class="breadcrumb">
					<li><a><i class="fa fa-dashboard"></i>配置中心</a></li>
					<li class="active">配置管理</li>
				</ol>-->
			</section>

			<!-- Main content -->
			<section class="content">
			
                <div class="row">
                    <div class="col-xs-4">
                        <div class="input-group">
                            <span class="input-group-addon">分组</span>
                            <select class="form-control" id="nodeGroup" >
                                <option value="" >全部</option>
								<#list XxlConfNodeGroup as group>
									<option value="${group.groupName}" >${group.groupTitle}</option>
								</#list>
                            </select>
                        </div>
                    </div>
                    <div class="col-xs-4">
                        <div class="input-group">
                            <span class="input-group-addon">KEY</span>
                            <input type="text" class="form-control" id="nodeKey" value="${nodeKey}" autocomplete="on" >
                        </div>
                    </div>
                    <div class="col-xs-2">
                        <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                    </div>
                    <div class="col-xs-2">
                        <button class="btn btn-block btn-success" id="add" type="button">新增配置</button>
                    </div>
                </div>

				<!-- 全部配置 -->
				<div class="box box-info2">
	                <div class="box-body">
	                  	<table id="conf_list" class="table table-bordered table-hover">
		                    <thead>
		                      	<tr>
                                    <th>GROUP</th>
                                    <th>KEY</th>
			                        <th>GROUP_KEY</th>
			                        <th>VALUE</th>
			                        <th>VALUE(zk)</th>
			                        <th>描述</th>
			                        <th>操作</th>
		                      	</tr>
							</thead>
		                    <tbody>
		                    	<#if fileterData?exists>
		                    		<#list fileterData as item>
		                    			<tr>
					                        <td>${item.nodeKey}</td>
					                        <td <#if item.znodeValue != item.znodeValueReal>style="color:red;font: italic bold"</#if> >${item.znodeValue}</td>
					                        <td <#if item.znodeValue != item.znodeValueReal>style="color:red;font: italic bold"</#if> >${item.znodeValueReal}</td>
					                        <td>${item.znodeDesc}</td>
					                        <td>
					                        	<div class="input-group">
						                      		<button class="btn btn-primary btn-xs update" type="button" nodeKey="${item.nodeKey}" znodeValue="${item.znodeValue}" znodeDesc="${item.znodeDesc}" >更新</button>&nbsp;
						                      		<button class="btn btn-danger btn-xs delete" type="button" nodeKey="${item.nodeKey}">删除</button>
					                        	</div>
					                        </td>
				                      	</tr>
		                    		</#list>
		                    	</#if>
		                    </tbody>
	                  	</table>
					</div><!-- /.box-body -->
				</div><!-- /.box -->
				
			</section>
			<!-- /.content -->
			
		</div>
		<!-- /.content-wrapper -->

		<@netCommon.commonFooter />

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
                            <label for="firstname" class="col-sm-2 control-label">分组</label>
                            <div class="col-sm-4">
								<select class="form-control" name="nodeGroup" >
									<#list XxlConfNodeGroup as group>
                                        <option value="${group.groupName}" >${group.groupTitle}</option>
									</#list>
								</select>
                        	</div>
                        </div>
						<div class="form-group">
							<label for="firstname" class="col-sm-2 control-label">KEY</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="nodeKey" placeholder="请输入KEY" maxlength="100" ></div>
						</div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeDesc" placeholder="请输入描述" maxlength="100" ></div>
                        </div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">VALUE</label>
							<div class="col-sm-10">
                                <textarea class="textarea" name="nodeValue" maxlength="512" placeholder="请输入VALUE" style="width: 100%; height: 100px; font-size: 14px; line-height: 18px; border: 1px solid #dddddd; padding: 10px;"></textarea>
							</div>
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
                            <label for="firstname" class="col-sm-2 control-label">GROUP</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeGroup" placeholder="请输入KEY" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">KEY</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeKey" placeholder="请输入KEY" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeDesc" placeholder="请输入描述" maxlength="100" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">VALUE</label>
                            <div class="col-sm-10">
                                <textarea class="textarea" name="nodeValue" maxlength="512" placeholder="请输入VALUE" style="width: 100%; height: 100px; font-size: 14px; line-height: 18px; border: 1px solid #dddddd; padding: 10px;"></textarea>
                            </div>
                        </div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" class="btn btn-primary"  >更新</button>
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
    <script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
    <script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
    <script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
    <script src="${request.contextPath}/static/js/conf.1.js"></script>
	
</body>
</html>
