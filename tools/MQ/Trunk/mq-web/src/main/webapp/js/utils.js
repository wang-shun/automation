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

$('body').on('click', 'input[name="editor-manner"]', function () {
    if ('json' === this.value) {
        $('#editor-container').empty();
        editor = new jsoneditor.JSONEditor(
            $('#editor-container')[0],
            {
                mode: 'code',
                modes: ['code', 'tree', 'view']
            },
            {});
        try {
            editor.set(JSON.parse(edit_obj.val()));
        } catch (e) {
            var arr = [];
            arr.push(edit_obj.val());
            editor.set(arr);
        }
    } else {
        $('#editor-container').empty();
        $('#editor-container').append('<div id="text-in" style="width:100%;height:100%"></div>');
        editor = ace.edit("text-in");
        editor.setValue(edit_obj.val());
        editor.focus();
    }
});

function openUrl(url)
{
    if(window.parent!=null)
    {
        window.parent.open(url,'_gtppage');
    }
    else
    {
        window.open(url,'_gtppage');
    }
    return ;
}