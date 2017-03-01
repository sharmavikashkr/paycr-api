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
	$("#createPricingBtn").click(function() {
		var name = $("#pri-name").val();
		var desc = $("#pri-desc").val();
		var limit = $("#pri-limit").val();
		var start = $("#pri-start").val();
		var end = $("#pri-end").val();
		var rate = $("#pri-rate").val();
		var dura = $("#pri-dura").val();
		
		var merchant = {
				"name" : name,
				"description" : desc,
				"invoiceLimit" : limit,
				"startAmount" : start,
				"endAmount" : end,
				"rate" : rate,
				"duration" : dura
		}
		
		$.ajax({
			url : '/pricing/new',
			data : JSON.stringify(merchant),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			success : function(data) {
				$("#createPricing").modal('hide')
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").addClass('alert-success');
				$("#serverRespMsg").html(data);
				$("#serverRespStatus").html("SUCCESS!");
			},
			error : function(data) {
				$("#createPricing").modal('hide')
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