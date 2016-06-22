var editor = new wysihtml.Editor($("[data-field='html-editor']").get(0), {
    parserRules: wysihtmlParserRules
    //showToolbarAfterInit: false
}),
composer = editor.composer;

function updateStates() {
    var value;

    if (composer.commands.state('bold')) {
        $('#boldbtn').addClass('active');
    } else {
        $('#boldbtn').removeClass('active');
    }

    if (composer.commands.state('italic')) {
        $('#italicbtn').addClass('active');
    } else {
        $('#italicbtn').removeClass('active');
    }

    if (composer.commands.state('mergeTableCells')) {
        $('#merge').addClass('active');
    } else {
        $('#merge').removeClass('active');
    }

    if (composer.commands.state('alignLeftStyle')) {
        $('#leftbtn').addClass('active');
    } else {
        $('#leftbtn').removeClass('active');
    }

    if (composer.commands.state('alignRightStyle')) {
        $('#rightbtn').addClass('active');
    } else {
        $('#rightbtn').removeClass('active');
    }

    if (composer.commands.state('alignCenterStyle')) {
        $('#centerbtn').addClass('active');
    } else {
        $('#centerbtn').removeClass('active');
    }

    if (composer.commands.state('foreColorStyle')) {
        $('#colorbtn').addClass('active');
        value = composer.commands.stateValue('foreColorStyle');
        $('.color-btn.active').removeClass('active');
        if (value) {
            $('.color-btn[data-value="' + value + '"]').addClass('active');
        }

        $('#colorBtns').show();

    } else {
        $('#colorbtn').removeClass('active');
        $('#colorBtns').hide();
    }

    if (composer.commands.state('bgColorStyle')) {
        $('#bgcolorbtn').addClass('active');
        value = composer.commands.stateValue('bgColorStyle');
        $('.bg-color-btn.active').removeClass('active');
        if (value) {
            $('.bg-color-btn[data-value="' + value + '"]').addClass('active');
        }

        $('#bgColorBtns').show();

    } else {
        $('#bgcolorbtn').removeClass('active');
        $('#bgColorBtns').hide();
    }

    // font size state
    if (composer.commands.state('fontSizeStyle')) {
        $('#fontSize').val(composer.commands.stateValue('fontSizeStyle'));
        $('#fontSizeRemove').addClass('active');
    } else {
        $('#fontSize').val('');
        $('#fontSizeRemove').removeClass('active');
    }

}

// Listen to events and update menu
editor.on("interaction", function () {
    updateStates();
});

editor.on("tableselect", function () {
    $('#tableBtns').show();
});

editor.on("tableunselect", function () {
    $('#tableBtns').hide();
});

// Prevent buttons from deselecting selected text before action is performed
$(".editor-btn").mousedown(function (event) {
    var b = composer.selection.getBookmark();
    event.preventDefault();

    // ie8 bug fix. mousedown does not prevent loosing focus
    setTimeout(function () {
        composer.selection.setBookmark(b);
    }, 0);

});

// commands binding
$('#boldbtn').click(function (event) {
    event.preventDefault();
    composer.commands.exec('bold');
    updateStates();
});

$('#italicbtn').click(function (event) {
    event.preventDefault();
    composer.commands.exec('italic');
    updateStates();
});

$('#leftbtn').click(function (event) {
    event.preventDefault();
    composer.commands.exec('alignLeftStyle');
    updateStates();
});

$('#rightbtn').click(function (event) {
    event.preventDefault();
    composer.commands.exec('alignRightStyle');
    updateStates();
});

$('#centerbtn').click(function (event) {
    event.preventDefault();
    composer.commands.exec('alignCenterStyle');
    updateStates();
});

//table
$('#addRowBelow').click(function (event) {
    event.preventDefault();
    composer.commands.exec("addTableCells", "below");
});

$('#addRowAbove').click(function (event) {
    event.preventDefault();
    composer.commands.exec("addTableCells", "above");
});

$('#addRowBefore').click(function (event) {
    event.preventDefault();
    composer.commands.exec("addTableCells", "before");
});

$('#addRowAfter').click(function (event) {
    event.preventDefault();
    composer.commands.exec("addTableCells", "after");
});

$('#removeRow').click(function (event) {
    event.preventDefault();
    composer.commands.exec("deleteTableCells", "row");
});

$('#removeCol').click(function (event) {
    event.preventDefault();
    composer.commands.exec("deleteTableCells", "column");
});

$('#merge').click(function (event) {
    event.preventDefault();
    composer.commands.exec("mergeTableCells");
});

// color by style
$('#colorbtn').click(function (event) {
    event.preventDefault();
    $('#colorBtns').show();
});

$('.color-btn').mousedown(function (event) {
    event.preventDefault();
    event.stopPropagation();
    var val = $(this).data("value");
    composer.commands.exec("foreColorStyle", val);
    updateStates();
});

$('#bgcolorbtn').click(function (event) {
    event.preventDefault();
    $('#bgColorBtns').show();
});

$('.bg-color-btn').mousedown(function (event) {
    event.preventDefault();
    event.stopPropagation();
    var val = $(this).data("value");
    composer.commands.exec("bgColorStyle", val);
    updateStates();
});

// font size actions
var selBookmark = null;
$('#fontSize').mousedown(function () {
    if (selBookmark == null) {
        selBookmark = composer.selection.getBookmark();
    }
});

$('#fontSize').change(function () {
    if (selBookmark) {
        setTimeout(function () {
            composer.selection.setBookmark(selBookmark);
            composer.commands.exec("fontSizeStyle", $('#fontSize').val());
            selBookmark = null;
        }, 0);
    }
});

$('#fontSize').keydown(function (event) {
    if (event.which == 13) {
        event.preventDefault();
        $('#fontSize').trigger('blur');
    }
});

$('#fontSizeSmaller').click(function (event) {
    event.preventDefault();
    selBookmark = composer.selection.getBookmark();
    var val = $('#fontSize').val(),
            nr;
    if (val && !(/^\s*$/).test(val)) {
        nr = parseInt(val, 10);
        if (nr > 1) {
            $('#fontSize').val(val.replace(/\d+/, nr - 1)).trigger('change');
            selBookmark = null;
        }
    }
});

$('#fontSizeBigger').click(function (event) {
    event.preventDefault();
    selBookmark = composer.selection.getBookmark();
    var val = $('#fontSize').val(),
            nr;
    if (val && !(/^\s*$/).test(val)) {
        nr = parseInt(val, 10);
        $('#fontSize').val(val.replace(/\d+/, nr + 1)).trigger('change');
        selBookmark = null;
    }
});

// executing with same value removes size
$('#fontSizeRemove').click(function (event) {
    event.preventDefault();
    var val = composer.commands.stateValue('fontSizeStyle');
    if (val) {
        composer.commands.exec("fontSizeStyle", val);
        selBookmark = null;
    }
});