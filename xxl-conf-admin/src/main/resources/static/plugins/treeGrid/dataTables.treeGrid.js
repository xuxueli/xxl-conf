/**
 * @summary     TreeGrid
 * @description TreeGrid extension for DataTable
 * @version     1.1.2
 * @file dataTables.treeGrid.js
 * 2020-04-16 更新日志
 * 1、解决dataTable reload() / draw() 时树形失效问题
 * 2、采用新的初始化方式，可以外部调用  expandAll() / collapseAll() 方法
 *  @example
 *      var table = $('#example').dataTable( { ... } );
 *      var tree = new $.fn.dataTable.treeGrid( table );
 *      tree.expandAll();
 * 3、更新后更容易对插件进行扩展，可以自定义自己需要实现的功能，参考expandAll() / collapseAll() 
 *   自己定义自己的方法，处理不同的需求
 */
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD
        define(['jquery', 'datatables.net'], function ($) {
            return factory($, window, document);
        });
    } else if (typeof exports === 'object') {
        // CommonJS
        module.exports = function (root, $) {
            if (!root) {
                root = window;
            }

            if (!$ || !$.fn.dataTable) {
                $ = require('datatables.net')(root, $).$;
            }

            return factory($, root, root.document);
        };
    } else {
        // Browser
        factory(jQuery, window, document);
    }
}(function ($, window, document) {
    'use strict';
    var DataTable = $.fn.dataTable;
    //定义全局 TR 子集 二维数组
    var treeGridRows = {};
    var globalInit;
    var TreeGrid = function (dt, init) {
        var that = this;
        if (!(this instanceof TreeGrid)) {
            alert('TreeGrid warning: TreeGrid must be initialised with the "new" keyword.');
            return;
        }

        if (init === undefined || init === true) {
            init = {};
        }
        globalInit = init;
        var dtSettings = new $.fn.dataTable.Api(dt).settings()[0];

        this.s = {
            dt: dtSettings
        };

        if (dtSettings._oTreeGrid) {
            throw 'TreeGrid already initialised on this table';
        }

        dtSettings._oTreeGrid = this;

        if (!dtSettings._bInitComplete) {
            dtSettings.oApi._fnCallbackReg(dtSettings, 'aoInitComplete', function () {
                that.fnConstruct(init);
            }, 'TreeGrid');
        } else {
            this.fnConstruct(init);
        }
    };

    $.extend(TreeGrid.prototype, {

        "fnConstruct": function (oInit) {
            var that = this;
            this.s = $.extend(true, this.s, TreeGrid.defaults, oInit);

            var settings = this.s.dt;
            var select = settings._select;
            var dataTable = $(settings.nTable).dataTable().api();
            var sLeft = this.s.left;
            var sExpandAll = this.s.expandAll;
            var expandIcon = $(this.s.expandIcon);
            var collapseIcon = $(this.s.collapseIcon);

            // Expand TreeGrid
            dataTable.on('click', 'td.treegrid-control', function (e) {
                if (!$(this).html()) {
                    return;
                }
                // record selected indexes
                var selectedIndexes = [];
                select && (selectedIndexes = dataTable.rows({selected: true}).indexes().toArray());
                var rows = dataTable.rows();
                var parentTr = getParentTr(e.target);
                var parentTrId = getTrId();
                $(parentTr).attr('id', parentTrId);
                var row = dataTable.row(parentTr);
                var index = row.index();
                var data = row.data();

                var td = $(dataTable.cell(getParentTd(e.target)).node());
                var paddingLeft = parseInt(td.css('padding-left'), 10);
                var layer = parseInt(td.find('span').css('margin-left') || 0, 10) / sLeft;
                var icon = collapseIcon.clone();
                icon.css('marginLeft', layer * sLeft + 'px');
                td.removeClass('treegrid-control').addClass('treegrid-control-open');
                td.html('').append(icon);

                if (data.children && data.children.length) {
                    var subRows = treeGridRows[parentTrId] = [];
                    var prevRow = row.node();
                    data.children.forEach(function (item) {
                        var newRow = dataTable.row.add(item);
                        var node = newRow.node();
                        var treegridTd = $(node).find('.treegrid-control');
                        var left = (layer + 1) * sLeft;
                        $(node).attr('parent-index', index);
                        treegridTd.find('span').css('marginLeft', left + 'px');
                        treegridTd.next().css('paddingLeft', paddingLeft + left + 'px');
                        $(node).insertAfter(prevRow);
                        prevRow = node;
                        subRows.push(node);
                    });

                    resetEvenOddClass(dataTable);
                    select && setTimeout(function () {
                        dataTable.rows(selectedIndexes).select();
                    }, 0);
                }
            });
            // Collapse TreeGrid
            dataTable.on('click', 'td.treegrid-control-open', function (e) {
                var selectedIndexes = [];
                select && (selectedIndexes = dataTable.rows({selected: true}).indexes().toArray());

                var parentTr = getParentTr(e.target);
                var parentTrId = $(parentTr).attr('id');
                var td = $(dataTable.cell(getParentTd(e.target)).node());
                var layer = parseInt(td.find('span').css('margin-left') || 0, 10) / sLeft;
                var icon = expandIcon.clone();
                icon.css('marginLeft', layer * sLeft + 'px');
                td.removeClass('treegrid-control-open').addClass('treegrid-control');
                td.html('').append(icon);

                resetTreeGridRows(parentTrId, dataTable);
                resetEvenOddClass(dataTable);

                select && setTimeout(function () {
                    dataTable.rows(selectedIndexes).select();
                }, 0);
            });
            //dataTable init 处理
            dataTable.on('init.dt', function () {
                console.log('Table initialisation complete: ' + new Date().getTime());
                //dataTable 初始化完成调用展开
                if(sExpandAll){
                    that.expandAll.call(that)
                }
            });

            //dataTable draw 处理
            dataTable.on('draw.dt.DTFC', function () {
                that._fnDraw.call(that);
            })

            var inProgress = false;
            // Check parents and children on select
            select && select.style === 'multi' && dataTable.on('select', function (e, dt, type, indexes) {
                if (inProgress) {
                    return;
                }
                inProgress = true;
                indexes.forEach(function (index) {
                    // Check parents
                    selectParent(dataTable, index);
                    // Check children
                    selectChildren(dataTable, index);
                });
                inProgress = false;
            });

            // Check parents and children on deselect
            select && select.style === 'multi' && dataTable.on('deselect', function (e, dt, type, indexes) {
                if (inProgress) {
                    return;
                }
                inProgress = true;
                indexes.forEach(function (index) {
                    // Check parents
                    deselectParent(dataTable, index);

                    // Check children
                    deselectChildren(dataTable, index);
                });
                inProgress = false;
            });
        },
        /**
         *  @returns {void}
         *  @example
         *      var table = $('#example').dataTable( { ... } );
         *      var tree = new $.fn.dataTable.treeGrid( table );
         *      tree.expandAll();
         */
        "expandAll": function () {
            console.log('expandAll: ' + new Date().getTime());
            var that = this;
            this.s = $.extend(true, this.s, TreeGrid.defaults, globalInit);
            var dataTable = $(this.s.dt.nTable).dataTable().api();
            var tds = $(dataTable.table().body()).find('td.treegrid-control');
            tds.each(function (index, td) {
                _expandAll(that, null, td);
            });
        },

        "collapseAll":function(){
            console.log('collapseAll: ' + new Date().getTime());
            var that = this;
            this.s = $.extend(true, this.s, TreeGrid.defaults, globalInit);
            var dataTable = $(this.s.dt.nTable).dataTable().api();
            var trs = $(dataTable.table().body()).find('tr');
            trs.each(function (index, tr) {
                if(typeof($(tr).attr("parent-index"))=="undefined"){
                    var trid = $(tr).attr('id');
                    resetTreeGridRows(trid, dataTable);
                }
            });
        },

        "_fnDraw": function () {
            console.log('_fnDraw: ' + new Date().getTime());
            var that = this;
            this.s = $.extend(true, this.s, TreeGrid.defaults, globalInit);
            var dataTable = $(this.s.dt.nTable).dataTable().api();
            /* Draw callback function */
            if (this.s.expandAll) {
                console.log('expandAll is True: ' + new Date().getTime());
                var tds = $(dataTable.table().body()).find('td.treegrid-control');
                tds.each(function (index, td) {
                    _expandAll(that, null, td);
                });
            }
            /* Event triggering */
            $(this).trigger('draw.dtfc', {
                "left": this.s.left
            });
        },
    });

    /**
     * 收缩展开处理
     * @param trId
     */
    function resetTreeGridRows (trId, dataTable) {
        var subRows = treeGridRows[trId];
        if (subRows && subRows.length) {
            subRows.forEach(function (node) {
                var subTrId = $(node).attr('id');
                if (treeGridRows[subTrId]) {
                    resetTreeGridRows(subTrId, dataTable);
                }
                dataTable.row($(node)).remove();
                $(node).remove();
            });
            delete treeGridRows[trId];
            $('#' + trId).find('.treegrid-control-open').each(function (i, td) {
                $(td).removeClass('treegrid-control-open').addClass('treegrid-control');
                $(td).html('').append($(globalInit.expandIcon).clone());
            });
        }
    };

    function resetEvenOddClass(dataTable) {
        var classes = ['odd', 'even'];
        $(dataTable.table().body()).find('tr').each(function (index, tr) {
            $(tr).removeClass('odd even').addClass(classes[index % 2]);
        });
    };

    function selectParent(dataTable, index) {
        var row = dataTable.row(index);
        var parentIndex = $(row.node()).attr('parent-index');
        if (parentIndex !== null) {
            parentIndex = +parentIndex;
            var selector = '[parent-index="' + parentIndex + '"]';
            var allChildRows = dataTable.rows(selector).nodes();
            var selectedChildRows = dataTable.rows(selector, {selected: true}).nodes();
            if (allChildRows.length === selectedChildRows.length) {
                var parentRow = dataTable.row(parentIndex, {selected: false});
                parentRow.select();
                if (parentRow.node()) {
                    selectParent(dataTable, parentIndex);
                }
            }
        }
    }

    function selectChildren(dataTable, index) {
        var rows = dataTable.rows('[parent-index="' + index + '"]', {selected: false});
        var childIndexes = rows.indexes().toArray();
        if (childIndexes.length) {
            rows.select();
            childIndexes.forEach(function (childIndex) {
                selectChildren(dataTable, childIndex);
            });
        }
    }

    function deselectParent(dataTable, index) {
        var row = dataTable.row(index);
        var parentIndex = $(row.node()).attr('parent-index');
        if (parentIndex !== null) {
            parentIndex = +parentIndex;
            var parentRow = dataTable.row(parentIndex, {selected: true});
            parentRow.deselect();
            if (parentRow.node()) {
                deselectParent(dataTable, parentIndex);
            }
        }
    }

    function deselectChildren(dataTable, index) {
        var rows = dataTable.rows('[parent-index="' + index + '"]', {selected: true});
        var childIndexes = rows.indexes().toArray();
        if (childIndexes.length) {
            rows.deselect();
            childIndexes.forEach(function (childIndex) {
                deselectChildren(dataTable, childIndex);
            });
        }
    }

    //默认展开方法
    function _expandAll(treeGrid, insertTr, tds) {
        var settings = treeGrid.s.dt;
        var select = settings._select;
        var dataTable = $(settings.nTable).dataTable().api();
        var sLeft = treeGrid.s.left;

        var expandIcon = $(treeGrid.s.expandIcon);
        var collapseIcon = $(treeGrid.s.collapseIcon);
        if (!tds) {
            return
        }
        // 迭代存在子集的表格行
        var parentTr = getParentTr(tds);
        var parentTrId = getTrId();

        var row = dataTable.row(parentTr);
        var index = row.index();
        var data = row.data();

        if (data.children && data.children.length) {

            $(parentTr).attr('id', parentTrId);
            // var td = $(dataTable.cell(getParentTd(tds)).node());
            var td = $(tds);
            var paddingLeft = parseInt(td.css('padding-left'), 10);
            var layer = parseInt(td.find('span').css('margin-left') || 0, 10) / sLeft;
            var icon = collapseIcon.clone();
            icon.css('marginLeft', layer * sLeft + 'px');
            td.removeClass('treegrid-control').addClass('treegrid-control-open');
            td.html('').append(icon);

            var subRows = treeGridRows[parentTrId] = [];
            var prevRow = row.node();
            if (!insertTr) {
                insertTr = prevRow;
            }

            //由于数据insertAfter插入问题，这里将JSON 倒序
            //data.children.reverse();
            var temp = JSON.parse(JSON.stringify(data.children));   // todo，need refactor
            temp.reverse().forEach(function (item) {
                var newRow = dataTable.row.add(item);
                var node = newRow.node();
                var treegridTd = $(node).find('.treegrid-control');
                var left = (layer + 1) * sLeft;
                $(node).attr('parent-index', index);
                treegridTd.find('span').css('marginLeft', left + 'px');
                treegridTd.next().css('paddingLeft', paddingLeft + left + 'px');
                // $(node).insertAfter(prevRow);
                $(node).insertAfter(insertTr);
                prevRow = node;
                subRows.push(node);
                //递归展开子集 当前插入行存在子集则递归
                var prevRowData = dataTable.row(prevRow).data();
                if (prevRowData.children && prevRowData.children.length) {
                    var prevTd = $(prevRow).find('td.treegrid-control');
                    _expandAll(treeGrid, $(node), prevTd);
                }

            });
            select && setTimeout(function () {
                dataTable.rows(selectedIndexes).select();
            }, 0);
        }
    };

    function getTrId() {
        return 'tr-' + Date.now();
    }

    function getParentTr(target) {
        return $(target).parents('tr')[0];
    }

    function getParentTd(target) {
        return target.tagName === 'TD' ? target : $(target).parents('td')[0];
    }

    TreeGrid.defaults = {
        "left": 12,
        "expandAll": false,
        "expandIcon": '<span>+</span>',
        "collapseIcon": '<span>-</span>',
        "fnDrawCallback": null
    };

    TreeGrid.version = '1.1.2';

    DataTable.Api.register('treeGrid()', function () {
        return this;
    });

    $(document).on('init.dt.treeGrid', function (e, settings) {
        if (e.namespace !== 'dt') {
            return;
        }

        var init = settings.oInit.treeGrid;
        var defaults = DataTable.defaults.treeGrid;

        if (init || defaults) {
            var opts = $.extend({}, init, defaults);

            if (init !== false) {
                var treeGrid = new TreeGrid(settings, opts);
            }
        }
    });

    $.fn.dataTable.TreeGrid = TreeGrid;
    $.fn.DataTable.TreeGrid = TreeGrid;

    return TreeGrid;
}));
