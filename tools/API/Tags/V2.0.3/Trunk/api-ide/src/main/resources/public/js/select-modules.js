var selectModules = $('#select-modules');
selectModules.multiSelect({
    keepOrder: true,
    selectableHeader: '<input id="selectable-filter" type="text" class="search-input" autocomplete="off" placeholder="搜索...">',
    selectionHeader: '<input id="selection-filter" type="text" class="search-input" autocomplete="off" placeholder="搜索...">',
    selectableFooter: '<div class="select-footer">' +
    '<label>可选区域</label>' +
    '<div class="pull-right">' +
    '<a href="javascript:selectAll()" id="select-all">全选</a>' +
    '</div>' +
    '</div>',
    selectionFooter: '<div class="select-footer">' +
    '<label>已选区域</label>' +
    '<div class="pull-right">' +
    '<a href="javascript:deselectAll()" id="deselect-all">全不选</a>' +
    '</div>' +
    '</div>',
    afterInit: function () {
        var that = this,
            $selectableSearch = $('#selectable-filter'),
            $selectionSearch = $('#selection-filter'),
            selectableSearchString = '#' + that.$container.attr('id') + ' .ms-elem-selectable:not(.ms-selected)',
            selectionSearchString = '#' + that.$container.attr('id') + ' .ms-elem-selection.ms-selected';
        that.qs1 = $selectableSearch.quicksearch(selectableSearchString)
            .on('keydown', function (e) {
                if (e.which === 40) {
                    that.$selectableUl.focus();
                    return false;
                }
            });

        that.qs2 = $selectionSearch.quicksearch(selectionSearchString)
            .on('keydown', function (e) {
                if (40 === e.which) {
                    that.$selectionUl.focus();
                    return false;
                }
            });
    },
    afterSelect: function () {
        this.qs1.cache();
        this.qs2.cache();
    },
    afterDeselect: function () {
        this.qs1.cache();
        this.qs2.cache();
    }
});

function selectAll() {
    selectModules.multiSelect('select_all');
};

function deselectAll() {
    selectModules.multiSelect('deselect_all');
};
