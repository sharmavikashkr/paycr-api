$(document).ready(function() {
	$("#dashboardTab").click();
	$("#serverRespAlert").hide();
	$("#dismissServerRespAlertBtn").click(function() {
		$("#serverRespAlert").hide();
	});
	$("#createInvoiceBtn").click(function() {
		var invoice;
		var name = $("#con-name").val();
		var email = $("#con-email").val();
		var mobile = $("#con-mobile").val();
		var consumer = {
				"name" : name,
				"email" : email,
				"mobile" : mobile
		};
		
		var item0 = {
			"name" : $("#item-name0").val(),
			"rate" : $("#item-rate0").val(),
			"quantity" : $("#item-quantity0").val(),
			"price" : $("#item-price0").val()
		};
		var item1 = {
			"name" : $("#item-name1").val(),
			"rate" : $("#item-rate1").val(),
			"quantity" : $("#item-quantity1").val(),
			"price" : $("#item-price1").val()
		};
		var item2 = {
			"name" : $("#item-name2").val(),
			"rate" : $("#item-rate2").val(),
			"quantity" : $("#item-quantity2").val(),
			"price" : $("#item-price2").val()
		};
		
		var items = [item0, item1, item2];
		
		var billNo = $("#inv-billNo").val();
		var invoiceCode = $("#inv-code").val();
		var sendEmail = $("#inv-sendEmail").is(":checked");
		var sendSms = $("#inv-sendSms").is(":checked");
		var shipping = $("#inv-shipping").val();
		var discount = $("#inv-discount").val();
		var amount = $("#inv-final").val();
		var currency = 'INR';
		var expiresIn = $("#inv-expiresIn").val();
		var invoice = {
				"invoiceCode" : invoiceCode,
				"billNo" : billNo,
				"consumer" : consumer,
				"items" : items,
				"sendEmail" : sendEmail,
				"sendSms" : sendSms,
				"shipping" : shipping,
				"discount" : discount,
				"amount" : amount,
				"currency" : currency,
				"expiresIn" : expiresIn
		}
		
		$.ajax({
			url : '/invoice/new',
			data : JSON.stringify(invoice),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			success : function(data) {
				$("#createInvoice").modal('hide')
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").addClass('alert-success');
				$("#serverRespMsg").html(data);
				$("#serverRespStatus").html("SUCCESS!");
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
});