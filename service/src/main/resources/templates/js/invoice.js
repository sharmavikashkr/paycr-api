$(document).ready(function() {
	var jsonRequest;
	var bulkFile;
	$('#createAndSend').click(function() {
		$('#invoiceEmail').focusout();
		$('#invoiceMobile').focusout();
		$('#invoiceAlternateMobile').focusout();
		$('#invoiceName').focusout();
		$('#invoiceAmount').focusout();
		$('#invoiceMaxPay').focusout();
		$('#invoiceValidity').focusout();
		$('[name = customParam]').focusout();
		if($('#invoiceDiv input.error').length === 0) {
			validate();
			var signData = jsonRequest.vanityUrl + jsonRequest.orderAmount.amount + jsonRequest.orderAmount.currency;
			jsonRequest.signature = getSignature(signData);
			$.ajax({
				url : $("#invoiceBaseUrl").val() + '/invoices/standalone',
				data : JSON.stringify(jsonRequest),
				type : 'POST',
				contentType : 'application/json',
				async : false,
				success : function(data) {
					$("#successStatus").text(data.responseMsg);
					$("#successMessage").text(data.specialMsg);
					$("#successbox").css("display", "block");
					clearFields();
				},
				error : function(data) {
					alert("Request Failed");
					clearFields();
				}
			});
		}
	});
	$('#uploadBulk').click(function() {
		$("#fileUpload").click();
	});
	$('#fileUpload').change(function () {
		bulkFile = this.files[0];
		$("#uploadBulkText").text(bulkFile.name);
		$('#uploadBulkAction').prop("disabled",false);
	});
	$('#uploadBulkAction').click(function() {
		var signData = $("#invoiceVanity").val() + bulkFile.name;
		var signature = getSignature(signData);
		var formdata = new FormData();
		formdata.append("bulk", bulkFile);
		$.ajax({
			url : $("#invoiceBaseUrl").val() + '/invoices/csv',
			data : formdata,
			type : 'POST',
			headers : {
				"vanityUrl":$("#invoiceVanity").val(),
				"signature":signature
			},
			cache : false,
	        contentType : false,
	        processData : false,
			success : function(data) {
				$("#successStatus").text(data.responseMsg);
				$("#successMessage").text(data.specialMsg);
				$("#jqicloseBulk").click();
				$("#successbox").css("display", "block");
			},
			error : function(data) {
				alert.html(JSON.stringify(data));
			}
		});
	});
	$("#settingSavebtn").click(function() {
		var signData = $("#invoiceVanity").val()+$("#settingReturnUrl").val();
		var signature = getSignature(signData);
		var invSetting = {
			"vanityUrl":$("#invoiceVanity").val(),
			"notifyUrl":undefined,
			"returnUrl":$("#settingReturnUrl").val(),
			"validityDays":0,
			"signature":signature
		};
		$.ajax({
			url : $("#invoiceBaseUrl").val() + '/merchant/add',
			data : JSON.stringify(invSetting),
			type : 'POST',
			contentType : 'application/json',
			async : false,
			complete : function(data) {
				$("#settingCancelbtn").click();
			}
		});
	});
	$("[name = invoiceExpire]").click(function() {
		var invoiceCode = this.id.split("invoiceExpire")[1];
		var signData = $("#invoiceVanity").val()+invoiceCode;
		var signature = getSignature(signData);
		$.ajax({
			url : $("#invoiceBaseUrl").val() + '/invoices/expire/' + $("#invoiceVanity").val(),
			type : 'GET',
			headers : {
				"invoiceCode":invoiceCode,
				"signature":signature
			},
			complete : function(data) {
				alert(data.responseText);
			}
		});
	});
	$("[name = invoiceNotify]").click(function() {
		var invoiceCode = this.id.split("invoiceNotify")[1];
		var signData = $("#invoiceVanity").val()+invoiceCode;
		var signature = getSignature(signData);
		$.ajax({
			url : $("#invoiceBaseUrl").val() + '/invoices/notify/' + $("#invoiceVanity").val(),
			type : 'GET',
			headers : {
				"invoiceCode":invoiceCode,
				"signature":signature
			},
			complete : function(data) {
				alert(data.responseText);
			}
		});
	});
	function getSignature(data) {
		var signature = "";
		$.ajax({
			url: $("#invoiceBaseUrl").val() + '/signature',
			type: 'GET',
			headers : {
				'vanityUrl':$("#invoiceVanity").val(),
				'data':data,
				'requestTime':$("#invoiceRequestTime").val(),
				'signature':$("#signSignature").val()
			},
			async: false,
			complete: function(data) {
				signature = data.responseText;
			}
		});
		return signature;
	}
	function validate() {
		var firstName;
		var lastName;
		var phoneNumber;
		var consumerDetail;
		var customParamsMap = {};
		var invoiceName = $("#invoiceName").val().trim();
		if(invoiceName == '') {
			invoiceName = undefined;
		} else {
			var name = $("#invoiceName").val().split(" ");
			firstName = name[0];
			lastName = name[name.length - 1];
		}
		var invoiceEmail = $("#invoiceEmail").val().trim();
		if(invoiceEmail == '') {
			invoiceEmail = undefined;
		}
		var invoiceMobile = $("#invoiceMobile").val().trim();
		if(invoiceMobile == '') {
			invoiceMobile = undefined;
		}
		var alternateNumber = $("#invoiceAlternateMobile").val().trim();
		if(alternateNumber == '') {
			alternateNumber = undefined;
		}
		var invoiceAmount= $("#invoiceAmount").val().trim();
		if(invoiceAmount == '') {
			invoiceAmount = '0';
		}
		var invoiceCurrency= $("#invoiceCurrency").val().trim();
		if(invoiceCurrency == '') {
			invoiceCurrency = 'INR';
		}
		if(invoiceMobile != undefined) {
			phoneNumber = {
				"phoneNumber" : invoiceMobile,
				"type" : "Mobile"
			};
		}
		$("[name = customParam").each( function() {
			var name = this.id;
			var value = $(this).val();
			customParamsMap[name] = value;
		});
		if(firstName != undefined | lastName != undefined | invoiceEmail != undefined | phoneNumber != undefined) {
			consumerDetail = {
					"firstName" : firstName,
					"lastName" : lastName,
					"email" : invoiceEmail,
					"phoneNumber" : phoneNumber,
					"alternateNumber":alternateNumber/*,
					"contactAddress" : {
						"addressStreet1" : "addressStreet1",
						"addressStreet2" : "addressStreet2",
						"addressCity" : "addressCity",
						"addressState" : "addressState",
						"addressCountry" : "addressCountry",
						"addressZip" : "addressZip"
					}*/
				};
		}
		jsonRequest = {
			"invoiceCode" : $("#invoiceCode").val().trim(),
			"consumerDetail" : consumerDetail,
			"orderAmount" : {
				"currency" : invoiceCurrency,
				"amount" : invoiceAmount
			},
			"customParamsMap" : customParamsMap,
			"maxPayAllowed" : $("#invoiceMaxPay").val().trim(),
			"validityDays" : $("#invoiceValidity").val().trim(),
			"createSmsInvoice" : $("#smsInvoice").is(":checked"),
			"createEmailInvoice" : $("#emailInvoice").is(":checked"),
			"notifyThirdParties" : false,
			/*"invoiceThirdParties" : [ {
				"name" : "Vikash Travels",
				"phoneNumber" : {
					"phoneNumber" : "9970197591",
					"type" : "Mobile"
				}
			}, {
				"name" : "Vikash Enterprises",
				"phoneNumber" : {
					"phoneNumber" : "9970197591",
					"type" : "Mobile"
				}
			} ],*/
			"vanityUrl" : $("#invoiceVanity").val(),
			"signature" : ''
		};
	}
	
	function clearFields() {
		$('#invoiceEmail').val("");
		$('#invoiceMobile').val("");
		$('#invoiceAlternateMobile').val("");
		$('#invoiceName').val("");
		$('#invoiceAmount').val("");
	}
});