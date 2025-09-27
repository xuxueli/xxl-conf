<!DOCTYPE html>
<html>
<head>
    <#-- import macro -->
    <#import "../common/common.macro.ftl" as netCommon>

    <!-- 1-style start -->
    <@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <!-- 1-style end -->

</head>
<body class="hold-transition" style="background-color: #ecf0f5;">
<div class="wrapper">
    <section class="content">

        <#-- 2-biz start -->

        <!-- 报表摘要 start -->
        <div class="row">
            <div class="col-md-4 col-sm-6 col-xs-12">
                <div class="info-box">
                    <span class="info-box-icon bg-aqua"><i class="fa fa-cloud"></i></span>
                    <div class="info-box-content">
                        <span class="info-box-text">服务数量</span>
                        <span class="info-box-number">***</span>
                    </div>
                </div>
            </div>
            <div class="col-md-4 col-sm-6 col-xs-12">
                <div class="info-box">
                    <span class="info-box-icon bg-red"><i class="fa fa-cubes"></i></span>
                    <div class="info-box-content">
                        <span class="info-box-text">注册节点数量</span>
                        <span class="info-box-number">***</span>
                    </div>
                </div>
            </div>
            <div class="col-md-4 col-sm-6 col-xs-12">
                <div class="info-box">
                    <span class="info-box-icon bg-green"><i class="ion ion-ios-gear-outline"></i></span>
                    <div class="info-box-content">
                        <span class="info-box-text">环境数量</span>
                        <span class="info-box-number">***</span>
                    </div>
                </div>
            </div>
        </div>
        <!-- 报表摘要 end --->

        <!-- 常用功能区域 start -->
        <div class="row">
            <div class="col-md-12">
                <!-- 常用功能 -->
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">常用功能</h3>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body">

                        <strong><i class="fa fa-cubes margin-r-5"></i>配置中心</strong>
                        <p class="text-muted">
                            提供配置管理以及动态推送能力，支持线上化变更及管理配置、历史记录回滚，以及实时与热更新主动推送等。
                        </p>

                        <hr>
                        <strong><i class="fa fa-cubes margin-r-5"></i>注册中心</strong>
                        <p class="text-muted">
                            提供服务节点注册及主动发现能力，支持服务节点注册、服务节点注销、服务节点心跳保活、服务节点健康监测等。
                        </p>

                        <hr>
                        <strong><i class="fa fa-cloud margin-r-5"></i>服务管理</strong>
                        <p class="text-muted">
                            提供服务定义、管理能力，支持通过服务维度聚合分析服务注册信息、节点健康信息等。
                        </p>

                        <hr>
                        <strong><i class="fa fa-cog margin-r-5"></i>环境管理</strong>
                        <p class="text-muted">
                            提供环境定义、管理能力，如测试环境、预发布环境和生产环境等，借助环境进行服务资源隔离。
                        </p>

                        <hr>
                        <strong><i class="fa fa-book margin-r-5"></i> 帮助中心</strong>
                        <p>提供内容丰富、干练易懂的操作文档，辅助快速上手项目。</p>

                    </div>
                    <!-- /.box-body -->
                </div>
            </div>

        </div>
        <!-- 个人信息区域 end -->

        <!-- 查看通知.模态框 start -->
        <div class="modal fade" id="showMessageModal" tabindex="-1" role="dialog"  aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" >查看通知</h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal form" role="form" >
                            <div class="form-group">
                                <label for="lastname" class="col-sm-2 control-label2">标题</label>
                                <div class="col-sm-8 title" ></div>
                            </div>
                            <div class="form-group">
                                <label for="lastname" class="col-sm-2 control-label2">操作时间</label>
                                <div class="col-sm-8 addTime" ></div>
                            </div>
                            <div class="form-group">
                                <label for="lastname" class="col-sm-2 control-label2">正文</label>
                                <div class="col-sm-8 content" style="overflow: hidden;" ></div>
                            </div>

                            <div class="form-group" style="text-align:center;border-top: 1px solid #e4e4e4;">
                                <div style="margin-top: 10px;" >
                                    <button type="button" class="btn btn-primary" data-dismiss="modal" >关闭</button>
                                </div>
                            </div>

                        </form>
                    </div>
                </div>
            </div>
        </div>
        <!-- 查看通知.模态框 end -->

        <#-- 2-biz end -->

    </section>
</div>

<!-- 3-script start -->
<@netCommon.commonScript />
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<!-- echarts -->
<script src="${request.contextPath}/static/plugins/echarts/echarts.common.min.js"></script>
<script>
$(function () {
    //
});
</script>
<!-- 3-script end -->

</body>
</html>