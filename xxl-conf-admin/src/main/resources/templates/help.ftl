<!DOCTYPE html>
<html>
<head>
  	<title>配置管理中心</title>
  	<#import "./common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["adminlte_settings"]?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "help" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>使用教程<small></small></h1>
			<!--
			<ol class="breadcrumb">
				<li><a><i class="fa fa-dashboard"></i>配置中心</a></li>
				<li class="active">使用教程</li>
			</ol>
			-->
		</section>

		<!-- Main content -->
		<section class="content">
			<div class="callout callout-info">
				<h4>简介：分布式配置管理平台XXL-CONF</h4>
				<br>
				<p>
                    <a target="_blank" href="https://github.com/xuxueli/xxl-conf">Github</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<iframe src="https://ghbtns.com/github-btn.html?user=xuxueli&repo=xxl-conf&type=star&count=true" frameborder="0" scrolling="0" width="170px" height="20px" style="margin-bottom:-5px;"></iframe>
					<br><br>
					<a target="_blank" href="http://www.xuxueli.com/xxl-conf/">官方文档</a>
                    <br><br>

				</p>
				<p></p>
            </div>
		</section>
		<!-- /.content -->
	</div>
	<!-- /.content-wrapper -->
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
</body>
</html>
