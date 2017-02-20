$(document).ready(function() {
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
				"currency" : currency
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
				window.location.reload();s
			}
		});
	});
});