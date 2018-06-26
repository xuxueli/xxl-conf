<!DOCTYPE html>
<html>
<head>
  	<title>配置管理中心</title>
  	<#import "../common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["adminlte_settings"]?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "project" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>项目管理</h1>
		</section>

		<!-- Main content -->
	    <section class="content">
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
                        <div class="box-header">
                            <h3 class="box-title">项目列表</h3>&nbsp;&nbsp;
                            <button class="btn btn-info btn-xs pull-left2 add" >+新增项目</button>
                        </div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
                                        <th>AppName</th>
                                        <th>项目名称</th>
					                </tr>
				                </thead>
                                <tbody>
								<#if list?exists && list?size gt 0>
								<#list list as project>
									<tr>
                                        <td width="40%" >${project.appname}</td>
                                        <td width="40%" >${project.title}</td>
										<td width="20%" >
                                            <button class="btn btn-warning btn-xs update" appname="${project.appname}" title="${project.title}" >编辑</button>
                                            <button class="btn btn-danger btn-xs remove" appname="${project.appname}" >删除</button>
										</td>
									</tr>
								</#list>
								</#if>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>

    <!-- 新增.模态框 -->
    <div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
        <div class="modal-dialog ">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" >新增项目</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="appname" placeholder="请输入AppName" maxlength="100" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">项目名称<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="请输入项目名称" maxlength="100" ></div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
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
        <div class="modal-dialog ">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" >编辑项目</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="appname" placeholder="请输入AppName" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">项目名称<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="请输入项目名称" maxlength="100" ></div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
                                <button type="submit" class="btn btn-primary"  >保存</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/js/project.index.1.js"></script>
</body>
</html>
