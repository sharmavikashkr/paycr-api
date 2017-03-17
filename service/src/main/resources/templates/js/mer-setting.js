$(document).ready(function() {
	$("#createCustomParamBtn").click(function() {
		var name = $("#cus-paramName").val().trim();
		var regex = new RegExp('[a-zA-Z0-9_ ]{1,20}');
		if(!regex.test(name)) {
			$("#cus-paramName").parent().addClass('has-error');
			return false;
		} else {
			$("#cus-paramName").parent().removeClass('has-error');
		}
		var provider = $("#cus-provider").val().trim();
		var customParam = {
			"paramName" : name,
			"provider" : provider
		}
		$.ajax({
			url : '/merchant/setting/customParam/new',
			data : JSON.stringify(customParam),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			success : function(data) {
				window.location.reload();
			},
			error : function(data) {
				$("#createCustomParam").modal('hide')
				$("#serverRespAlert").show();
				$("#serverRespAlert").removeClass('alert-danger');
				$("#serverRespAlert").removeClass('alert-success');
				$("#serverRespAlert").addClass('alert-danger');
				$("#serverRespMsg").html(data.responseText);
				$("#serverRespStatus").html("FAILURE!");
			}
		});
	});
	$("[id = deleteCustomParamBtn]").click(function() {
		var merchantCustomParamId = $(this).attr('ref');
		$.ajax({
			url : '/merchant/setting/customParam/delete/'+merchantCustomParamId,
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
				$("#serverRespMsg").html(data.responseText);
				$("#serverRespStatus").html("FAILURE!");
			}
		});
	});
	$("#updateMerchantSettingBtn").click(function() {
		var isSendSms = $("#set-sendSms").is(':checked');
		var isSendEmail = $("#set-sendEmail").is(':checked');
		var expiryDays = $("#set-expiry").val().trim();
		var regex = new RegExp('[0-9]{1,5}');
		if(!regex.test(expiryDays)) {
			$("#set-expiry").parent().addClass('has-error');
			return false;
		} else {
			$("#set-expiry").parent().removeClass('has-error');
		}
		var rzpMerchantId = $("#set-rzpmi").val().trim();
		var rzpKeyId = $("#set-rzpki").val().trim();
		var rzpSecretId = $("#set-rzpsi").val().trim();
		var setting = {
				"sendSms" : isSendSms,
				"sendEmail" : isSendEmail,
				"expiryDays" : expiryDays,
				"rzpMerchantId" : rzpMerchantId,
				"rzpKeyId" : rzpKeyId,
				"rzpSecretId" : rzpSecretId
		}
		$.ajax({
			url : '/merchant/setting/update',
			data : JSON.stringify(setting),
			type : 'POST',
			async : false,
			contentType : 'application/json',
			success : function(data) {
				window.location.reload();
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
});