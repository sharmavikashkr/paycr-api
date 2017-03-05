$(document).ready(function() {
	$("#createCustomParamBtn").click(function() {
		var name = $("#cus-paramName").val();
		var provider = $("#cus-provider").val();
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
		var setting = {
				"sendSms" : isSendSms,
				"sendEmail" : isSendEmail
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