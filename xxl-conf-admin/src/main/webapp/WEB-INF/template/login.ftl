<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>xxl-cfg-admin</title> 

<!-- Bootstrap 3.3.5 -->
<link rel="stylesheet" href="${request.contextPath}/static/lte/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<!--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">-->
<link rel="stylesheet" href="${request.contextPath}/static/lte/others/font-awesome-4.3.0/css/font-awesome.min.css">
<!-- Ionicons -->
<!--<link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">-->
<link rel="stylesheet" href="${request.contextPath}/static/lte/others/ionicons-2.0.1/css/ionicons.min.css">
<!-- <link rel="stylesheet" href="${request.contextPath}/static/lte/dist/css/AdminLTE.min.css"> -->
<link rel="stylesheet" href="${request.contextPath}/static/lte/dist/css/AdminLTE.2.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/lte/plugins/iCheck/square/blue.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->

</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo"><a href="../../index2.html"><b>XXL</b>cfg</a></div>
		<div class="login-box-body">
			<p class="login-box-msg">分布式配置管理平台</p>
			<form id="loginForm">
				<div class="form-group has-feedback">
					<input type="text" class="form-control" name="userName" placeholder="请输入账号" minlength="6" maxlength="18" value="admin" >
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
				</div>
				<div class="form-group has-feedback">
					<input type="password" class="form-control" name="password" placeholder="请输入密码" minlength="6" maxlength="18" value="123456" >
		            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
				</div>
				<div class="row">
					<div class="col-xs-8">
						<div class="checkbox icheck">
		                	<label><input type="checkbox"> Remember Me</label>
		              	</div>
		            </div><!-- /.col -->
		            <div class="col-xs-4">
		              	<button type="submit" class="btn btn-primary btn-block btn-flat">登陆</button>
		            </div><!-- /.col -->
				</div>
			</form>
	        <a href="javascript:alert('活该');">忘记密码</a><br>

		</div><!-- /.login-box-body -->
	</div><!-- /.login-box -->
	
	<script>
		var base_url = '${request.contextPath}';
	</script>
	<!-- jQuery 2.1.4 -->
    <script src="${request.contextPath}/static/lte/plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="${request.contextPath}/static/lte/bootstrap/js/bootstrap.min.js"></script>
    <!-- iCheck -->
    <script src="${request.contextPath}/static/lte/plugins/iCheck/icheck.min.js"></script>
    
    <script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
    <#import "/common/common.macro.ftl" as netCommon>
    <@netCommon.comAlert/>
    <script src="${request.contextPath}/static/js/login.1.js"></script>
    
 </body>
</html>
