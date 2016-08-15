<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>xxl-cfg-admin</title> 

<#import "/common/common.macro.ftl" as netCommon>
<@netCommon.commonStyle />

</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		
		<@netCommon.commonHeader />

		<@netCommon.commonLeft />

		<!-- Content Wrapper. Contains page content -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>使用说明 <small>分布式配置管理平台</small></h1>
				<ol class="breadcrumb">
					<li><a><i class="fa fa-dashboard"></i>配置中心</a></li>
					<li class="active">使用说明</li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">
				<div class="callout callout-info">
					<h4>简介：xxl-cfg-admin</h4>
					<p>分布式配置管理平台：一套完整的基于zookeeper的分布式配置统一解决方案.</p>
					<p></p>
	            </div>
	            
	            <div class="callout callout-info">
					<h4>主要目标：</h4>
					<p>1、简化部署：同一个上线包，无须改动配置，即可在 多个环境中(研发RD/测试QA/线上PRODUCTION) 上线.</p>
					<p>2、动态部署：更改配置，无需重新打包或重启，即可 实时生效.</p>
					<p>3、统一管理：提供web平台，统一管理 多个环境(RD/QA/PRODUCTION)、多个产品 的所有配置.</p>
	            </div>
			</section>
			<!-- /.content -->
		</div>
		<!-- /.content-wrapper -->

		<@netCommon.commonFooter />
		<@netCommon.commonControl />

	</div>
	<!-- ./wrapper -->

	<script>
		var base_url = '${request.contextPath}';
	</script>
	<@netCommon.commonScript/>
	<@netCommon.comAlert/>
	
</body>
</html>
