<#macro commonStyle>

	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <!-- <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css"> -->
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/font-awesome-4.3.0/css/font-awesome.min.css">
    <!-- Ionicons -->
    <!-- <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css"> -->
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/ionicons-2.0.1/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE-local.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">
      
	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  
	<!-- scrollup -->
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/scrollup/image.css">
	<!-- pace -->
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/pace/themes/pace-theme-flash.css">
	<#-- toastr -->
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/toastr/toastr.css">

</#macro>

<#macro commonScript>
	<!-- jQuery 2.1.4 -->
	<script src="${request.contextPath}/static/adminlte/plugins/jQuery/jQuery-2.1.4.min.js"></script>
	<!-- Bootstrap 3.3.5 -->
	<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="${request.contextPath}/static/adminlte/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="${request.contextPath}/static/adminlte/dist/js/app.min.js"></script>

    <!-- scrollup -->
    <script src="${request.contextPath}/static/plugins/scrollup/jquery.scrollUp.min.js"></script>
    <!-- pace -->
    <script src="${request.contextPath}/static/plugins/pace/pace.min.js"></script>
	<#-- jquery.cookie -->
    <script src="${request.contextPath}/static/plugins/jquery/jquery.cookie.js"></script>
	<#-- toastr -->
	<script src="${request.contextPath}/static/plugins/toastr/toastr.js"></script>

	<script src="${request.contextPath}/static/js/xxl.alert.1.js"></script>
    <script src="${request.contextPath}/static/js/common.1.js"></script>

    <script>var base_url = '${request.contextPath}';</script>
</#macro>

<#macro commonHeader>
	<header class="main-header">
		<a href="${request.contextPath}/" class="logo">
			<span class="logo-mini"><b>X</b>XL</span>
			<span class="logo-lg"><b>分布式配置管理平台</b></span>
		</a>
		<nav class="navbar navbar-static-top" role="navigation">
			<a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button"><span class="sr-only">切换导航</span></a>
          	<div class="navbar-custom-menu">
				<ul class="nav navbar-nav">
					<li class="dropdown user user-menu">
	                    <a href=";" id="logoutBtn" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                      		<span class="hidden-xs">注销</span>
	                    </a>
					</li>
				</ul>
			</div>
		</nav>
	</header>
</#macro>

<#macro commonLeft>
	<!-- Left side column. contains the logo and sidebar -->
	<aside class="main-sidebar">
		<!-- sidebar: style can be found in sidebar.less -->
		<section class="sidebar">
			<!-- sidebar menu: : style can be found in sidebar.less -->
			<ul class="sidebar-menu">
				<li class="header">常用模块</li>
				<li class="nav-click" ><a href="${request.contextPath}/conf"><i class="fa fa-circle-o text-red"></i> <span>配置管理</span></a></li>
                <li class="nav-click" ><a href="${request.contextPath}/group"><i class="fa fa-circle-o text-red"></i> <span>分组管理</span></a></li>
				<li class="nav-click" ><a href="${request.contextPath}/help"><i class="fa fa-circle-o text-yellow"></i><span>使用教程</span></a></li>
			</ul>
		</section>
		<!-- /.sidebar -->
	</aside>
</#macro>

<#macro commonFooter >
	<footer class="main-footer">
		Powered by <b>XXL-CONF</b> 1.3
		<div class="pull-right hidden-xs">
			<strong>Copyright &copy; 2015-${.now?string('yyyy')} &nbsp;
                <a href="https://github.com/xuxueli/xxl-conf" target="_blank" >github</a>&nbsp;
                <a href="http://my.oschina.net/xuxueli/blog/734267" target="_blank" >oschina</a>
			</strong><!-- All rights reserved. -->
		</div>
	</footer>
</#macro>