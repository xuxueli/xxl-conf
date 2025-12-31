<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.min.css">
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/select2/select2.min.css">
	<style>
		/* select2 */
		.select2-container--default .select2-selection--single {
			border: 1px solid #d2d6de;
			border-radius: 0;
			height: 34px;
			padding: 6px 12px;
		}
		/* diff */
		del {
			display: block;
			text-decoration: none;
			color: #b30000;
			background: #fadad7;
		}
		ins {
			display: block;
			background: #eaf2c2;
			color: #406619;
			text-decoration: none;
		}
		.chunk-header {
			color: #8a008b;
			text-decoration: none;
		}
		textarea  {
			resize: vertical
		}

	</style>
	<!-- 1-style end -->

</head>
<body class="hold-transition" style="background-color: #ecf0f5;">
<div class="wrapper">
	<section class="content">

		<!-- 2-content start -->

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
					<div class="col-xs-1">
						<button class="btn btn-block btn-default resetBtn" >${I18n.system_reset}</button>
					</div>
				</div>
			</div>
		</div>

		<#-- 数据表格区域 -->
		<div class="row">
			<div class="col-xs-12">
				<div class="box">
					<div class="box-header pull-left" id="data_operation" >
						<button class="btn btn-sm btn-danger selectAny delete" type="button"><i class="fa fa-remove "></i>${I18n.system_opt_del}</button>
						｜
						<button class="btn btn-sm btn-default selectOnlyOne showDiff" type="button">查看配置Diff</button>
						<button class="btn btn-sm btn-primary selectOnlyOne rollback" type="button">配置回滚</button>
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

		<!-- Diff查看.模态框 -->
		<div class="modal fade" id="showDiffModal" tabindex="-1" role="dialog"  aria-hidden="true" >
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >查看配置Diff</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >

							<#-- 展示内容 -->
							<table style="width: 100%;height: 400px;" >
								 <tr>
									<td style="width: 33.3333%;" ><label>旧配置：</label></td>
									<td style="width: 33.3333%;" ><label>新配置：</label></td>
									<td style="width: 33.3333%;" ><label>配置Diff：</label></td>
								</tr>
								<tr>
									<td style="width: 33.3333%;" ><textarea id="oldData" readonly style="width: 100%;height: 100%;" ></textarea></td>
									<td style="width: 33.3333%;" ><textarea id="newData" readonly style="width: 100%;height: 100%;" ></textarea></td>
									<td style="width: 33.3333%;" ><div style="width: 100%;height: 100%;" ><pre id="diffResult" style="white-space: normal;"></pre></div></td>
								</tr>
							</table>

							<div class="form-group" style="text-align:center;border-top: 1px solid #e4e4e4;">
								<div style="margin-top: 10px;" >
									<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
								</div>
							</div>

						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 2-content end -->

	</section>
</div>

<!-- 3-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2.min.js"></script>
<script src="${request.contextPath}/static/plugins/jsdiff/diff.js"></script>
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script src="${request.contextPath}/static/biz/common/admin.util.js"></script>
<script>
	$(function() {

		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/confdatalog/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.dataId = $('#dataId').val();
				obj.offset = params.offset;
				obj.pagesize = params.limit;
				return obj;
			},
			resetHandler: function(data){
				// reset
				$('#data_filter input[type="text"]').val('');
				$('#data_filter select').each(function() {
					$(this).prop('selectedIndex', 0);
				});
				// init
				initFilter();
			},
			columns:[
				{
					checkbox: true,
					field: 'state',
					width: '5',
					widthUnit: '%',
					align: 'center',
					valign: 'middle'
				}, {
					title: '操作人',
					field: 'optUsername',
					width: '15',
					widthUnit: '%',
					align: 'left'
				}, {
					title: '操作时间',
					field: 'addTime',
					width: '25',
					widthUnit: '%',
					align: 'left'
				}, {
					title: '配置Value',
					field: 'value',
					width: '40',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						let result = !value ? '' : value.length > 10 ? value.substring(0, 10) : value;
						return "<span title='"+ value +"'>"+ result +"</span>";
					}
				}
			]
		});

		/**
		 * init delete
		 */
		$.adminTable.initDelete({
			url: base_url + "/confdatalog/delete"
		});

		/**
		 * rollback
		 */
		$("#data_operation").on('click', '.rollback',function() {
			// get select rows
			var rows = $.adminTable.selectRows();
			if (rows.length !== 1) {
				layer.msg(I18n.system_please_choose + I18n.system_one + I18n.system_data);
				return;
			}
			var row = rows[0];

			// do rollback
			layer.confirm( '确认回滚该配置?', {
				icon: 3,
				title: I18n.system_tips ,
				btn: [ I18n.system_ok, I18n.system_cancel ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + "/confdatalog/rollback" ,
					data : {
						"dataLogId" : row.id
					},
					dataType : "json",
					success : function(data){
						if (data.code === 200) {
							layer.msg( I18n.system_opt + I18n.system_success );
							// refresh table
							$('#data_filter .searchBtn').click();
						} else {
							layer.msg( data.msg || I18n.system_opt + I18n.system_fail );
						}
					},
					error: function(xhr, status, error) {
						// Handle error
						console.log("Error: " + error);
						layer.open({
							icon: '2',
							content: (I18n.system_opt + I18n.system_fail)
						});
					}
				});
			});
		});

		/**
		 * showDiff
		 */
		var oldData = document.getElementById('oldData');
		var newData = document.getElementById('newData');
		var diffResult = document.getElementById('diffResult');
		$("#data_operation").on('click', '.showDiff',function() {
			// get select rows
			var rows = $.adminTable.selectRows();
			if (rows.length !== 1) {
				layer.msg(I18n.system_please_choose + I18n.system_one + I18n.system_data);
				return;
			}
			var row = rows[0];

			// fill data
			oldData.value = row.oldValue;
			newData.value = row.value;


			newData.value = row.value;

			// process diff
			var fragment = document.createDocumentFragment();
			var diff = Diff['diffLines'](oldData.value, newData.value);

			for (var i=0; i < diff.length; i++) {

				if (diff[i].added && diff[i + 1] && diff[i + 1].removed) {
					var swap = diff[i];
					diff[i] = diff[i + 1];
					diff[i + 1] = swap;
				}

				var node;
				if (diff[i].removed) {
					node = document.createElement('del');
					node.appendChild(document.createTextNode(diff[i].value));
				} else if (diff[i].added) {
					node = document.createElement('ins');
					node.appendChild(document.createTextNode(diff[i].value));
				} else if (diff[i].chunkHeader) {
					node = document.createElement('span');
					node.setAttribute('class', 'chunk-header');
					node.appendChild(document.createTextNode(diff[i].value));
				} else {
					node = document.createTextNode(diff[i].value);
				}
				fragment.appendChild(node);
			}

			diffResult.textContent = '';
			diffResult.appendChild(fragment);

			// show
			$('#showDiffModal').modal({backdrop: true, keyboard: false}).modal('show');
		});

	});

</script>
<!-- 3-script end -->

</body>
</html>