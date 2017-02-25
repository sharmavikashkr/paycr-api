$(document).ready(function() {
	$("#merchantsTabLink").click();
	$("#dismissServerRespAlertBtn").click(function() {
		$("#serverRespAlert").hide();
	});
	$("#createMerchantBtn").click(function() {
		var name = $("#mer-name").val();
		var email = $("#mer-email").val();
		var mobile = $("#mer-mobile").val();
		var admin = $("#mer-admin").val();
		var pricingId = $("#mer-price").val()
		
		var merchant = {
				"name" : name,
				"email" : email,
				"mobile" : mobile,
				"adminName" : admin,
				"pricingId" : pricingId,
		}
		
		$.ajax({
			url : '/merchant/new',
			data : JSON.stringify(merchant),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			success : function(data) {
				$("#createMerchant").modal('hide')
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").addClass('alert-success');
				$("#serverRespMsg").html(data);
				$("#serverRespStatus").html("SUCCESS!");
			},
			error : function(data) {
				$("#createMerchant").modal('hide')
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