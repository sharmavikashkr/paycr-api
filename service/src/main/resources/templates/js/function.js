/*---------Function.js----------*/
$(document).ready(function() {
	$("[name = oldInvoiceEmail").each(function() {
		$(this).html($(this).html().replace(/\n/g, "<br>"));
	});
	$("#emailPreviewTab1,#smsPreviewTab2").on("click", function(e) {
		var Id = $(this).attr("Id");
		var index = Id.substr(Id.length - 1);
		$(".naviLi").removeClass("selectedTab");
		$(this).addClass("selectedTab");
		$(".wrapperCommondiv").hide();
		$("#wrapperContaindiv" + index).show();
	});
	$("#btnEditSettingInvoice").on("click", function(e) {
		$("#jqibox").css("display", "block");
	});
	$("#settingCancelbtn").on("click", function(e) {
		$("#jqibox").css("display", "none");
	});
	$("#successCancelbtn").on("click", function(e) {
		$("#successbox").css("display", "none");
		location.reload();
	});
	$('#bulkInvoice').click(function() {
		if ($('#bulkInvoice').is(':checked')) {
			$("#jqiboxBulk").css("display", "block");
		} else {
			$("#jqiboxBulk").css("display", "none");
		}
	});
	$("#jqicloseBulk").on("click", function(e) {
		$("#jqiboxBulk").css("display", "none");
		$('#bulkInvoice').prop('checked', false);
	});
	$(".rectangle").on("click", function(e) {
		$(".rectangle").removeClass("selectedRectangle");
		$(this).addClass("selectedRectangle");
	});
	$("#alternateMobileNumSection").on("click", function(e) {
		$("#invoiceAlternateMobile").css("display", "block");
		$("#alternateMobileNumSection").css("display", "none");
	});
});
// Email validation
$('#invoiceEmail').focusout(function() {
	if($("#emailInvoice").is(':checked')) {
		var sEmail = $('#invoiceEmail').val();
		if (isValidEmail(sEmail)) {
			$('#invoiceEmail').removeClass("error");
			$("#EmailErrorTipsy").hide();
		} else {
			$('#invoiceEmail').addClass("error");
			$("#EmailErrorTipsy").show();
			$(".tipsy-emailid-text").text("Invalid email address");
		}
	}
});
$('#invoiceEmail').focusin(function() {
	$('#invoiceEmail').removeClass("error");
	$("#EmailErrorTipsy").hide();
});
// Mobile validation
$('#invoiceMobile').focusout(function() {
	if($("#smsInvoice").is(':checked')) {
		var sMobile = $('#invoiceMobile').val();
		if (isValidMobile(sMobile)) {
			$('#invoiceMobile').removeClass("error");
			$("#MobileErrorTipsy").hide();
		} else {
			$('#invoiceMobile').addClass("error");
			$("#MobileErrorTipsy").show();
			$(".tipsy-mobile-text").text("Invalid mobile number");
		}
	}
});
$('#invoiceMobile').focusin(function() {
	$('#invoiceMobile').removeClass("error");
	$("#MobileErrorTipsy").hide();
});
// Alternate Mobile validation
$('#invoiceAlternateMobile').focusout(function() {
	var sMobile = $('#invoiceAlternateMobile').val();
	if (isValidMobile(sMobile)) {
		$('#invoiceAlternateMobile').removeClass("error");
		$("#AlternateMobileErrorTipsy").hide();
	} else {
		$('#invoiceAlternateMobile').addClass("error");
		$("#AlternateMobileErrorTipsy").show();
		$(".tipsy-altermobile-text").text("Invalid mobile number");
	}
	if ($.trim(sMobile).length == 0) {
		$('#invoiceAlternateMobile').removeClass("error");
		$("#AlternateMobileErrorTipsy").hide();
	}
});
$('#invoiceAlternateMobile').focusin(function() {
	$('#invoiceAlternateMobile').removeClass("error");
	$("#AlternateMobileErrorTipsy").hide();
});
// Name validation
$('#invoiceName').focusout(function() {
	if($("#emailInvoice").is(':checked') | $("#smsInvoice").is(':checked')) {
		var invoiceName = $('#invoiceName').val();
		if (isValidString(invoiceName)) {
			$('#invoiceName').removeClass("error");
			$("#NameErrorTipsy").hide();
			$("#invoiceNamePreview").html(invoiceName);
		}
		if ($.trim(invoiceName).length == 0) {
			$('#invoiceName').addClass("error");
			$("#NameErrorTipsy").show();
			$(".tipsy-name-text").text("Please enter Name");
			$("#invoiceNamePreview").html("[name]");
		}
	}
});
$('#invoiceName').focusin(function() {
	$('#invoiceName').removeClass("error");
	$("#NameErrorTipsy").hide();
});
// Amount Validation
$('#invoiceAmount').focusout(function() {
	var invoiceAmount = $('#invoiceAmount').val();
	if (isValidString(invoiceAmount)) {
		$('#invoiceAmount').removeClass("error");
		$("#AmountErrorTipsy").hide();
		$("#invoiceAmountPreview").text(invoiceAmount);
	}
	if ($.trim(invoiceAmount).length == 0) {
		$('#invoiceAmount').addClass("error");
		$("#AmountErrorTipsy").show();
		$(".tipsy-amount-text").text("Please enter amount");
		$("#invoiceAmountPreview").text("[amount]");
	}
});
$('#invoiceAmount').focusin(function() {
	$('#invoiceAmount').removeClass("error");
	$("#AmountErrorTipsy").hide();
});
// Max Pay validation
$('#invoiceMaxPay').focusout(function() {
	var num = $('#invoiceMaxPay').val();
	if (isValidNumber(num)) {
		$('#invoiceMaxPay').removeClass("error");
		$("#MaxPayErrorTipsy").hide();
	} else {
		$('#invoiceMaxPay').addClass("error");
		$("#MaxPayErrorTipsy").show();
		$(".tipsy-maxpay-text").text("Invalid max pay");
	}
});
$('#invoiceMaxPay').focusin(function() {
	$('#invoiceMaxPay').removeClass("error");
	$("#MaxPayErrorTipsy").hide();
});
// Validity Validation
$('#invoiceValidity').focusout(function() {
	var num = $('#invoiceValidity').val();
	if (isValidNumber(num)) {
		$('#invoiceValidity').removeClass("error");
		$("#ValidityErrorTipsy").hide();
	} else {
		$('#invoiceValidity').addClass("error");
		$("#ValidityErrorTipsy").show();
		$(".tipsy-validity-text").text("Invalid validity");
	}
});
$('#invoiceValidity').focusin(function() {
	$('#invoiceValidity').removeClass("error");
	$("#ValidityErrorTipsy").hide();
});
$('#invoiceName').bind("change", function () {
	var invoiceName = $('#invoiceName').val();
	if(invoiceName.length !== 0)
		$("[name = invoiceNamePreview]").text(invoiceName.split(" ")[0]);
	else {
		$("[name = invoiceNamePreview]").text("[name]");
	}
});
$('#invoiceAmount').bind("change", function () {
	var invoiceAmount = $('#invoiceAmount').val();
	if(invoiceAmount.length !== 0)
		$("[name = invoiceAmountPreview]").text(invoiceAmount);
	else {
		$("[name = invoiceAmountPreview]").text("[amount]");
	}
});
$('#invoiceCurrency').bind("change", function () {
	var invoiceCurrency = $('#invoiceCurrency').val();
	$("[name = invoiceCurrencyPreview]").text(invoiceCurrency);
});
$('[name = invoiceTitle]').click(function () {
	var pos = this.id.split("-")[1];
	var contentId = 'msgInvoice-'+pos;
	if($('#'+contentId).css("display") === "none") {
		$('[name = invoiceContent]').css("display", "none");
		$('#'+contentId).css("display", "block");
	}
	else {
		$('#'+contentId).css("display", "none");
	}
});
$('[name = customParam]').focusout(function () {
	var paramVal = $(this).val();
	var valid;
	if(this.id === 'Email') {
		valid = isValidEmail(paramVal);
	} else if(this.id === 'Mobile') {
		valid = isValidMobile(paramVal);
	} else {
		valid = isValidCustomParam(paramVal);
	}
	if(valid) {
		$(this).removeClass("error");
	} else {
		$(this).addClass("error");
	}
});
$('[name = customParam]').focusin(function() {
	$(this).removeClass("error");
});
$( "#searchOldInvoices" ).keyup(function() {
    var value = $(this).val();
    searchUl(value);
});
$( "#searchOldInvoices" ).on("search", function() {
    var value = $(this).val();
    searchUl(value);
});
function searchUl(value) {
	$("#oldInvoices > li").each(function() {
        if ($(this).text().search(value) > -1) {
            $(this).show();
        }
        else {
            $(this).hide();
        }
        if(value === "") {
        	$(this).show();
        }
    });
}
// EmailId validation function
function isValidEmail(email) {
	var filter = /^(("[\w-+\s]+")|([\w-+]+(?:\.[\w-+]+)*)|("[\w-+\s]+")([\w-+]+(?:\.[\w-+]+)*))(@((?:[\w-+]+\.)*\w[\w-+]{0,66})\.([a-zA-Z]{2,6}(?:\.[a-zA-Z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][\d]\.|1[\d]{2}\.|[\d]{1,2}\.))((25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\.){2}(25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\]?$)/;
	return filter.test(email);
}
// MobileNum validation function
function isValidMobile(val) {
	var mobileRegex = /^[7-9]{1}[0-9]{9}$/;
	return mobileRegex.test(val);
}
function isValidNumber(val) {
	var mobileRegex = /^[1-9][0-9]{0,4}$/;
	return mobileRegex.test(val);
}
// Name validation function
function isValidString(val) {
	if (val.trim() == "")
		return true;
	if (/^[a-zA-Z ]*$/.test(val) == false)
		return true;
	return false;
}
//Custom Param validation function
function isValidCustomParam(val) {
	var paramRegex = /^.{1,}$/;
	return paramRegex.test(val);
}
// keypress function
$(function() {
	$("input[id$='invoiceAlternateMobile'],input[id$='invoiceMobile'],input[id$='invoiceMaxPay'],input[id$='invoiceValidity']")
	.keypress(function(e) {
		if (e.which != 8 && e.which != 0
				&& (e.which < 48 || e.which > 57)) {
			return false;
		}
	});
});
// keypress function
$(function() {
	$("input[id$='invoiceName']").on("keypress",function(event) {
		var onlyAlphabet = /[A-Za-z ]/g;
		var key = String.fromCharCode(event.which);
		if (event.keyCode == 8 || event.keyCode == 37
				|| event.keyCode == 39 || onlyAlphabet.test(key)) {
			return true;
		}
		return false;
	});
});
// Num & alpha function
$('#invoiceCode').keypress(function(e) {
	var regex = new RegExp("^[a-zA-Z0-9]+$");
	var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
	if (regex.test(str)) {
		return true;
	}
	e.preventDefault();
	return false;
});
// amount function
$("#invoiceAmount").on("keypress",function(event) {
	$(this).val($(this).val().replace(/[^0-9\.]/g, ''));
	if ((event.which != 46 || $(this).val().indexOf('.') != -1)
			&& (event.which < 48 || event.which > 57)) {
		event.preventDefault();
	}
});
// invoice num print
$("#invoiceCode").focusout(function() {
	var invoiceCode = $("#invoiceCode").val();
	$("#invoiceCodePreview").text(invoiceCode);
});
$("#invoiceAmount").focusout(function() {
	var invoiceCode = $("#invoiceCode").val();
	$("#invoiceCodePreview").text(invoiceCode);
});
