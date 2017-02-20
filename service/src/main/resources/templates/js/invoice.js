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