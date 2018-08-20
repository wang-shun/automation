var isGui = window.location.href.indexOf("gui.html") > -1;


//拖拽div排序展示
function reOrder(ul) {
    var lis = $(ul).children(':not(.hide)');
    for (var i = 0; i < lis.length; ++i) {
        var item = $(lis[i]);
        var id = i +1 ;
        if (id < 10) {
            id = "0" + id;
        }
        $(item.children('span')[1]).html(id);
    }
}


$('body').on('click', '.icon-remove', function () {
    var item = $(this).parent();
    var parent = item.parent();
    item.remove();
    reOrder(parent);
});

$('body').on('mouseenter', 'a[rel=popover]', function () {
    $(this).popover('show');
});

$('body').on('mouseleave', 'a[rel=popover]', function () {
    $(this).popover('hide');
});

$('body').on('click', '.icon-pencil', function () {
    $('#modal-editor').modal('show');
    edit_obj = $(this).parent().prev('input');
    try {
        JSON.parse(edit_obj.val());
        $('input[name="editor-manner"][value="json"]').click();
    } catch (e) {
        $('input[name="editor-manner"][value="text"]').click();
    }
});



$('body').on('mousedown', '[contenteditable="true"]', function () {
    this.focus();
});

$('body').on('mouseover', '[data-toggle="tooltip"]', function () {
    this.style.cursor = 'pointer';
    $(this).tooltip('show');
});

$('body').on('mouseover', '.icon-pencil', function () {
    this.style.cursor = 'pointer';
});

$('body').on('click', '#menu-add-node li.hide a', function (e) {
    e.preventDefault();
});

