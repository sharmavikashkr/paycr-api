/**
 * Created by PriyankaP on 4/6/2016.
 */
$(document).ready(function() {
    var oldinvoice = $("#oldInvoice");
    var newInvoice = $("#createInvoice");
    var recdiv = $(".rectangleDiv");
    var addemailidinput = $("#emailIdenvoicing");
    var emamilinputLabel = $("#addEmail");
    var NameEnvoicing = $("#NameEnvoicing");
    var NameEnvoicingLabel = $("#userName");
    var InvoicNumenvoicing = $("#InvoicNumenvoicing");
    var InvoicNumenvoicingLabel = $("#invoiceNumValReplace");
    var creatEmailInvoiceCheck = $("#creatEmailInvoiceCheck");
    var creatSmsInvoiceCheck = $("#creatSmsInvoiceCheck");
    var emailIdenvoicing = $("#emailIdenvoicing");
    var mobileNumEnvoicing=$("#mobileNumEnvoicing");
    $(creatEmailInvoiceCheck).change(function() {
        addrequireAttr(creatEmailInvoiceCheck, emailIdenvoicing);
    });
    $(creatSmsInvoiceCheck).change(function() {
        addrequireAttr(creatSmsInvoiceCheck, mobileNumEnvoicing);
    });


    function addrequireAttr(checkBox, input) {
        if (checkBox.prop('checked')) {
            $(input).attr("required", "required");
        } else {
            $(input).removeAttr("required");
        }
        if (!(creatEmailInvoiceCheck.prop('checked') && creatSmsInvoiceCheck.prop('checked')) ) {
            $(emailIdenvoicing).attr("required", "required");
        }
    }
    //    $('#scrollbar1').tinyscrollbar();
    //    $('#scrollbar1').jScrollPane();
    $(oldinvoice).on("click", function() {
        $(recdiv).addClass("hide");
    });
    $(newInvoice).on("click", function() {
        $(recdiv).removeClass("hide");
    });
    $(addemailidinput).keyup(function() {
        onkeyupGetvalue(addemailidinput, emamilinputLabel);
    });
    $(NameEnvoicing).keyup(function() {
        onkeyupGetvalue(NameEnvoicing, NameEnvoicingLabel);
    });
    $(InvoicNumenvoicing).keyup(function() {
        onkeyupGetvalue(InvoicNumenvoicing, InvoicNumenvoicingLabel);
    });
    $(function() {
        var settings = {
            showArrows: true
        };
        var pane = $('.scroll-pane')
        pane.jScrollPane(settings);
        var api = pane.data('jsp');
        var i = 1;
        // Every second add some new content...
        setInterval(function() {
            api.reinitialise();
        }, 1000);
    });

    function onkeyupGetvalue(input, label) {
        var newValue = $(input).val();
        $(label).text(newValue);
        if (newValue == "") {
            $(label).text("");
        }
    }
    $('.toggle').click(function(e) {
        $('.toggle').css("display", "block");
        $(this).css("display", "none");
        e.preventDefault();
        var $this = $(this);
        if ($this.next().hasClass('show')) {
            $this.next().removeClass('show');
            $this.next().slideUp(350);
        } else {
            $this.parent().parent().find('li .inner').removeClass('show');
            $this.parent().parent().find('li .inner').slideUp(350);
            $this.next().toggleClass('show');
            $this.next().slideToggle(350);
        }
    });
});