$(document).ready(function() {
	$("#merchantsTabLink").click();
	$("#dismissServerRespAlertBtn").click(function() {
		$("#serverRespAlert").hide();
	});
	$("#createMerchantBtn").click(function() {
		var errors = 0;
		$('#createMerchantForm :input').each(function() {
			if($(this).prop('required')) {
				if($(this).attr('type') == 'email') {
					var filter = /^(("[\w-+\s]+")|([\w-+]+(?:\.[\w-+]+)*)|("[\w-+\s]+")([\w-+]+(?:\.[\w-+]+)*))(@((?:[\w-+]+\.)*\w[\w-+]{0,66})\.([a-zA-Z]{2,6}(?:\.[a-zA-Z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][\d]\.|1[\d]{2}\.|[\d]{1,2}\.))((25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\.){2}(25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\]?$)/;
					if(!filter.test($(this).val().trim())) {
						errors = errors + 1;
						$(this).parent().addClass('has-error');
					} else {
						$(this).parent().removeClass('has-error');
					}
				} else {
					var pattern = $(this).attr('pattern');
					if(pattern != undefined) {
						var regex = new RegExp(pattern);
						if(!regex.test($(this).val().trim())) {
							errors = errors + 1;
							$(this).parent().addClass('has-error');
						} else {
							$(this).parent().removeClass('has-error');
						}
					}
				}
			}
		});
		if(errors > 0) {
			return false;
		}
		var name = $("#mer-name").val().trim();
		var email = $("#mer-email").val().trim();
		var mobile = $("#mer-mobile").val().trim();
		var admin = $("#mer-admin").val().trim();
		var pricingId = $("#mer-price").val();
		
		var merchant = {
				"name" : name,
				"email" : email,
				"mobile" : mobile,
				"adminName" : admin,
				"pricingId" : pricingId,
		}
		
		$.ajax({
			url : '/admin/merchant/new',
			data : JSON.stringify(merchant),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			success : function(data) {
				window.location.reload();
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
		var errors = 0;
		$('#createPricingForm :input').each(function() {
			if($(this).prop('required')) {
				var pattern = $(this).attr('pattern');
				if(pattern != undefined) {
					var regex = new RegExp(pattern);
					if(!regex.test($(this).val().trim())) {
						errors = errors + 1;
						$(this).parent().addClass('has-error');
					} else {
						$(this).parent().removeClass('has-error');
					}
				}
			}
		});
		if(errors > 0) {
			return false;
		}
		var name = $("#pri-name").val().trim();
		var desc = $("#pri-desc").val().trim();
		var limit = $("#pri-limit").val().trim();
		var start = $("#pri-start").val().trim();
		var end = $("#pri-end").val().trim();
		var rate = $("#pri-rate").val().trim();
		var dura = $("#pri-dura").val().trim();
		
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
			url : '/admin/pricing/new',
			data : JSON.stringify(merchant),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			success : function(data) {
				window.location.reload();
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
	$("[id = togglePricingBtn]").click(function() {
		var pricingId = $(this).attr('ref');
		$.ajax({
			url : '/admin/pricing/toggle/'+pricingId,
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
				$("#serverRespMsg").html("FAILURE");
				$("#serverRespStatus").html("FAILURE!");
			}
		});
	});
	$("#dismissServerRespAlertBtn").click();
});