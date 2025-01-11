(function($) {

    $.extend({
        dataTableSelect: {
            // bind change event
            init: function() {
                /*options = options || {};*/
                initEvent();
            },
            // Select: refresh operation status
            selectStatusInit: function() {
                return selectStatusInit();
            },
            // Select: find ids
            selectIdsFind: function() {
                return selectIdsFind();
            },
        }
    });

    // ---------- ---------- ---------- customer select for datatables ---------- ---------- ----------
    // bind change event
    function initEvent(){
        // Select：All
        $('#data_list').on('change', 'thead #checkAll', function() {
            var isChecked = $(this).prop('checked');
            $('#data_list tbody  input.checkItem').each(function(){
                $(this).prop('checked', isChecked);
            });
            selectStatusEffctOpt();
        });
        // Select：Item (all select will fresh '#checkAll')
        $('#data_list').on('change', 'tbody input.checkItem', function() {
            var newStatus = $('#data_list tbody input.checkItem').length>0
                && $('#data_list tbody input.checkItem').length === $('#data_list tbody input.checkItem:checked').length;
            $('#checkAll').prop('checked', newStatus);
            selectStatusEffctOpt();
        });
    }

    // Select: status init
    function selectStatusInit(){
        $('#checkAll').prop('checked', false);
        $('#data_list tbody input.checkItem').each(function(){
            $(this).prop('checked', false);
        });
        selectStatusEffctOpt();
    }
    // Select: find ids
    function selectIdsFind(){
        var checkIds = [];
        $('#data_list tbody input.checkItem').each(function(){
            if ($(this).prop('checked')) {
                checkIds.push( $(this).attr('data-id') );
            }
        });
        return checkIds;
    }
    // Select: refresh operation status
    function selectStatusEffctOpt(){
        var selectLen = selectIdsFind().length;
        if (selectLen > 0) {
            $("#data_operation .selectAny").removeClass('disabled');
        } else {
            $("#data_operation .selectAny").addClass('disabled');
        }
        if (selectLen === 1) {
            $("#data_operation .selectOnlyOne").removeClass('disabled');
        } else {
            $("#data_operation .selectOnlyOne").addClass('disabled');
        }

    }

})(jQuery);