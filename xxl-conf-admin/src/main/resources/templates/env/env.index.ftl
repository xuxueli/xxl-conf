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
	<@netCommon.commonLeft "env" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>环境管理</h1>
		</section>

		<!-- Main content -->
	    <section class="content">
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
                        <div class="box-header">
                            <h3 class="box-title">环境列表</h3>&nbsp;&nbsp;
                            <button class="btn btn-info btn-xs pull-left2 add" >+新增环境</button>
                        </div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
                                        <th>Env</th>
                                        <th>环境名称</th>
                                        <th>顺序</th>
                                        <th>操作</th>
					                </tr>
				                </thead>
                                <tbody>
								<#if list?exists && list?size gt 0>
								<#list list as item>
									<tr>
                                        <td width="30%" >${item.env}</td>
                                        <td width="40%" >${item.title}</td>
                                        <td width="10%" >${item.order}</td>
										<td width="20%" >
                                            <button class="btn btn-warning btn-xs update" env="${item.env}" title="${item.title}" order="${item.order}" >编辑</button>
                                            <button class="btn btn-danger btn-xs remove" env="${item.env}" >删除</button>
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
                    <h4 class="modal-title" >新增环境</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">Env<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="env" placeholder="请输入Env" maxlength="50" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">环境名称<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="请输入环境名称" maxlength="100" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">顺序<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="order" placeholder="请输入顺序" maxlength="2" ></div>
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
                    <h4 class="modal-title" >编辑环境</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">Env<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="env" placeholder="请输入Env" maxlength="50" readonly ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">环境名称<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="请输入环境名称" maxlength="100" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">顺序<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="order" placeholder="请输入顺序" maxlength="2" ></div>
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
<script src="${request.contextPath}/static/js/env.index.1.js"></script>
</body>
</html>
