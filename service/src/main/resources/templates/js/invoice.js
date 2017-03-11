$(document).ready(function() {
	$("#dashboardTabLink").click();
	$("#dismissServerRespAlertBtn").click(function() {
		$("#serverRespAlert").hide();
	});
	$("#createInvoiceBtn").click(function() {
		if(!$("#createInvoiceForm")[0].checkValidity()) {
			return false;
		}
		var name = $("#con-name").val();
		var email = $("#con-email").val();
		var mobile = $("#con-mobile").val();
		var consumer = {
				"name" : name,
				"email" : email,
				"mobile" : mobile
		};
		var items = [];
		$("[id = itemRow]").each(function() {
			var name = $(this).find("#item-name").val();
			var rate = $(this).find("#item-rate").val();
			var quantity = $(this).find("#item-quantity").val();
			var price = $(this).find("#item-price").val();
			var item = {
				"name" : name,
				"rate" : rate,
				"quantity" : quantity,
				"price" : price
			}
			items.push(item);
		});
		var customParams = [];
		$("[name = inv-customParam]").each(function() {
			var name = $(this).attr('ref');
			var value = $(this).val();
			var provider = $(this).attr('provider');
			var customParam = {
				"paramName" : name,
				"paramValue" : value,
				"provider" : provider
			}
			customParams.push(customParam);
		});
		
		var invoiceCode = "";
		var sendEmail = $("#inv-sendEmail").is(":checked");
		var sendSms = $("#inv-sendSms").is(":checked");
		var shipping = $("#inv-shipping").val();
		var discount = $("#inv-discount").val();
		var amount = $("#inv-final").val();
		var currency = 'INR';
		var expiresIn = $("#inv-expiresIn").val();
		var invoice = {
				"invoiceCode" : invoiceCode,
				"consumer" : consumer,
				"items" : items,
				"sendEmail" : sendEmail,
				"sendSms" : sendSms,
				"shipping" : shipping,
				"discount" : discount,
				"payAmount" : amount,
				"currency" : currency,
				"expiresIn" : expiresIn,
				"customParams" : customParams
		}
		
		$.ajax({
			url : '/invoice/new',
			data : JSON.stringify(invoice),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			success : function(data) {
				window.location.reload();
			},
			error : function(data) {
				$("#createInvoice").modal('hide')
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").addClass('alert-danger');
				$("#serverRespMsg").html(data.responseText);
				$("#serverRespStatus").html("FAILURE!");
			}
		});
	});
	$("[id = enquireBtn]").click(function() {
		var invoiceCode = $(this).attr('ref');
		$.ajax({
			url : '/invoice/enquire/'+invoiceCode,
			type : 'GET',
			async : false,
			success : function(data) {
				window.location.reload();
			},
			error : function(data) {
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").addClass('alert-danger');
				$("#serverRespMsg").html("Invoice not found");
				$("#serverRespStatus").html("FAILURE!");
			}
		});
	});
	$("[id = expireBtn]").click(function() {
		var invoiceCode = $(this).attr('ref');
		$.ajax({
			url : '/invoice/expire/'+invoiceCode,
			type : 'GET',
			async : false,
			success : function(data) {
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").addClass('alert-success');
				$("#serverRespMsg").html(data);
				$("#serverRespStatus").html("SUCCESS!");
			},
			error : function(data) {
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").addClass('alert-danger');
				$("#serverRespMsg").html(data.responseText);
				$("#serverRespStatus").html("FAILURE!");
			}
		});
	});
	$("[id = notifyBtn]").click(function() {
		var invoiceCode = $(this).attr('ref');
		$.ajax({
			url : '/invoice/notify/'+invoiceCode,
			type : 'GET',
			async : false,
			success : function(data) {
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").addClass('alert-success');
				$("#serverRespMsg").html(data);
				$("#serverRespStatus").html("SUCCESS!");
			},
			error : function(data) {
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").addClass('alert-danger');
				$("#serverRespMsg").html(data.responseText);
				$("#serverRespStatus").html("FAILURE!");
			}
		});
	});
	$("#inv-shipping, #inv-discount, [id = itemBody]").on("change", "#item-rate, #item-quantity, #inv-shipping, #inv-discount", function() {
		updatePrice();
	});
	function updatePrice() {
		$("[id = itemRow]").each(function() {
			if($(this).find("#item-rate").val().trim() == '') {
				$(this).find("#item-rate").val('0');
			}
			if($(this).find("#item-quantity").val().trim() == '') {
				$(this).find("#item-quantity").val('0');
			}
			var rate = parseFloat($(this).find("#item-rate").val());
			var quantity = parseFloat($(this).find("#item-quantity").val());
			$(this).find("#item-price").val(rate * quantity);
		});
		var total = 0;
		$("[id = item-price]").each(function() {
			total = total + parseFloat($(this).val());
		});
		$("#inv-total").val(total);
		if($("#inv-shipping").val().trim() == '') {
			$("#inv-shipping").val('0');
		}
		if($("#inv-discount").val().trim() == '') {
			$("#inv-discount").val('0');
		}
		var shipping = parseFloat($("#inv-shipping").val());
		var discount = parseFloat($("#inv-discount").val());
		$("#inv-final").val(total + shipping - discount);
	}
	$("[id = itemBody]").on("click", "#addTr", function() {
		if($("[id = itemRow]").length < 5) {
			var $newTr = $(this).parent().parent().parent().clone();
			$newTr.find('input').val('');
			$newTr.insertAfter($(this).parent().parent().parent());
		}
	});
	$("[id = itemBody]").on("click", "#delTr", function() {
		if($("[id = itemRow]").length > 1) {
			$(this).parent().parent().parent().remove();
			updatePrice();
		}
	});
});