var app = angular.module('payCrApp', [ "ngRoute", "ngCookies" ]);
app.controller('MerchantController',
function($scope, $http, $cookies, $httpParamSerializer, $timeout) {
	$scope.server = {
		"hideMessage" : true,
		"respStatus" : "WELCOME!",
		"respMsg" : ":)",
		"isSuccess" : true
	}
	$scope.patterns = {
		"paramNamePattern" : "\\w{1,10}",
		"namePattern" : "[0-9a-zA-Z_\\- ]{1,50}",
		"emailPattern" : "([a-zA-Z0-9_.]{1,})((@[a-zA-Z]{2,})[\\\.]([a-zA-Z]{2}|[a-zA-Z]{3}))",
		"mobilePattern" : "\\d{10}",
		"amountPattern" : "\\d{1,7}",
		"numberPattern" : "\\d{1,4}"
	}
	$scope.searchInvoiceReq = {
		"invoiceCode" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : "2017-01-01",
		"createdTo" : "2017-12-31"
	}
	$scope.newInvoiceSetting = {
		"name" : "",
		"sendEmail" : true,
		"sendSms" : false,
		"expiryDays" : 7
	}
	$scope.newinvoice = {
		"invoiceCode" : "",
		"consumer" : {
			"email" : "",
			"mobile" : "",
			"name" : ""
		},
		"items" : [ {
			"name" : "",
			"rate" : 0,
			"quantity" : 0,
			"price" : 0
		} ],
		"sendEmail" : false,
		"sendSms" : false,
		"tax" : 18.00,
		"discount" : 0,
		"payAmount" : 0,
		"currency" : "INR",
		"expiresIn" : "",
		"invoiceSettingId" : 0,
		"customParams" : [ {
			"paramName" : "",
			"paramValue" : "",
			"provider" : ""
		} ]
	}
	$scope.dismissServerAlert = function() {
		$scope.server.hideMessage = true;
	}
	$scope.serverMessage = function(data) {
		$scope.server.hideMessage = false;
		if(data.status==200) {
			$scope.server.isSuccess = true;
			$scope.server.respStatus = "SUCCESS!";
			$scope.server.respMsg = "operation successful";
		} else if(data.status==401) {
			$scope.server.isSuccess = false;
			$scope.server.respStatus = "FAILURE!";
			$scope.server.respMsg = "unauthorized request";
			$scope.logout();
		} else {
			$scope.server.isSuccess = false;
			$scope.server.respStatus = "FAILURE!";
			$scope.server.respMsg = data.headers('error_message');
		}
	}
	$scope.prepare = function() {
		$scope.fetchMerchant();
		$scope.fetchUser();
		$scope.fetchRoles();
		$scope.fetchNotifications();
		$scope.fetchEnums();
	}
	$scope.fetchEnums = function() {
		var req = {
			method : 'GET',
			url : "/enum/providers",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(providers) {
			$scope.paramProviders = providers.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
				method : 'GET',
				url : "/enum/paymodes",
				headers : {
					"Authorization" : "Bearer "
							+ $cookies.get("access_token")
				}
			}
			$http(req).then(function(payModes) {
				$scope.payModes = payModes.data;
			}, function(data) {
				$scope.serverMessage(data);
			});
	}
	$scope.fetchMerchant = function() {
		var req = {
			method : 'GET',
			url : "/merchant/get",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(merchant) {
			$scope.merchant = merchant.data;
			$scope.refreshSetting(merchant.data.invoiceSetting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchUser = function() {
		var req = {
			method : 'GET',
			url : "/common/user",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(user) {
			$scope.user = user.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchRoles = function() {
		var req = {
			method : 'GET',
			url : "/common/roles",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(roles) {
			$scope.roles = roles.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchNotifications = function() {
		var req = {
			method : 'GET',
			url : "/merchant/notifications",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(notifications) {
			$scope.notices = notifications.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchMyUsers = function() {
		var req = {
			method : 'GET',
			url : "/common/users",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(myusers) {
			$scope.myusers = myusers.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createUser = function() {
		if(!this.addUserForm.$valid) {
			return false;
		}
		var req = {
			method : 'post',
			url : "/common/create/user",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newuser
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchMyUsers();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createUser')).modal('hide');
	}
	$scope.toggleUser = function(userId) {
		var req = {
			method : 'get',
			url : "/common/toggle/user/" + userId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchMyUsers();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.updateAccount = function() {
		var req = {
			method : 'POST',
			url : "/merchant/account/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.merchant
		}
		$http(req).then(function(merchant) {
			$scope.merchant = merchant.data;
			$scope.serverMessage(merchant);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.searchInvoice = function() {
		var req = {
			method : 'POST',
			url : "/search/invoice",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.searchInvoiceReq
		}
		$http(req).then(function(invoices) {
			$scope.invoices = invoices.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchMyInvoices = function() {
		var req = {
			method : 'GET',
			url : "/common/invoices/",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(myInvoices) {
			$scope.myinvoices = myInvoices.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.savePaymentSetting = function() {
		var req = {
			method : 'POST',
			url : "/merchant/paymentsetting/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.merchant.paymentSetting
		}
		$http(req).then(function(paymentsettings) {
			$scope.merchant.paymentSettings = paymentsettings.data;
			$scope.serverMessage(paymentsettings);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createInvoiceSetting = function() {
		var req = {
			method : 'POST',
			url : "/merchant/invoicesetting/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newInvoiceSetting
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
			$scope.serverMessage(invoicesetting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.saveInvoiceSetting = function(invoiceSetting) {
		var req = {
			method : 'POST',
			url : "/merchant/invoicesetting/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : invoiceSetting
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
			$scope.serverMessage(invoicesetting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.addParam = function() {
		if(!this.addCustomParamForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/merchant/customParam/new/",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newparam
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createCustomParam')).modal('hide');
	}
	$scope.deleteParam = function(paramId, paramName) {
		if (!confirm('Delete ' + paramName + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/merchant/customParam/delete/" + paramId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data[0]);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchPricings = function() {
		var req = {
			method : 'GET',
			url : "/common/pricings",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(pricings) {
			$scope.pricings = pricings.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.updateInvoiceInfo = function(invoice) {
		$scope.invoiceInfo = invoice;
	}
	$scope.addItem = function() {
		if ($scope.newinvoice.items.length < 5) {
			$scope.newinvoice.items.push({
				"name" : "",
				"rate" : 0,
				"quantity" : 0,
				"price" : 0
			});
		}
	}
	$scope.deleteItem = function(pos) {
		if ($scope.newinvoice.items.length > 1) {
			$scope.newinvoice.items.splice(pos, 1);
			$scope.calculateTotal();
		}
	}
	$scope.calculateTotal = function() {
		var totals = 0;
		for ( var item in $scope.newinvoice.items) {
			if ($scope.newinvoice.items[item].rate == null) {
				$scope.newinvoice.items[item].rate = 0;
			}
			if ($scope.newinvoice.items[item].quantity == null) {
				$scope.newinvoice.items[item].quantity = 0;
			}
			$scope.newinvoice.items[item].price = parseFloat($scope.newinvoice.items[item].rate)
					* parseFloat($scope.newinvoice.items[item].quantity);
			totals = totals
					+ parseFloat($scope.newinvoice.items[item].price);
		}
		if ($scope.newinvoice.tax == null) {
			$scope.newinvoice.tax = 0;
		}
		if ($scope.newinvoice.discount == null) {
			$scope.newinvoice.discount = 0;
		}
		$scope.newinvoice.payAmount = totals
				+ parseFloat((parseFloat($scope.newinvoice.tax) * totals) / 100)
				- parseFloat($scope.newinvoice.discount);
	}
	$scope.createInvoice = function() {
		if(!this.createInvoiceForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/invoice/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newinvoice
		}
		$http(req).then(function(data) {
			$scope.fetchMerchant();
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createInvoice')).modal('hide');
	}
	$scope.enquireInvoice = function(invoiceCode) {
		var req = {
			method : 'GET',
			url : "/invoice/enquire/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.notifyInvoice = function(invoiceCode) {
		var req = {
			method : 'GET',
			url : "/invoice/notify/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.expireInvoice = function(invoiceCode) {
		if (!confirm('Expire ' + invoiceCode + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/invoice/expire/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.refundInvoice = function(invoiceCode, refundAmount) {
		if(refundAmount == undefined) {
			return false;
		}
		if (!confirm('Refund ' + invoiceCode + ' with amount ' + refundAmount + ' ?')) {
			return false;
		}
		var refundRequest = {};
		refundRequest.amount = refundAmount;
		refundRequest.invoiceCode = invoiceCode;
		var req = {
			method : 'POST',
			url : "/invoice/refund",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
		        "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
			},
			data : $httpParamSerializer(refundRequest)
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		$scope.refundAmount = ''
		angular.element(document.querySelector('#refundInvoice')).modal('hide');
	}
	$scope.markPaidInvoice = function(invoiceCode) {
		if(!this.markPaidForm.$valid) {
			return false;
		}
		this.markpaid.invoiceCode = invoiceCode;
		var req = {
			method : 'POST',
			url : "/invoice/markpaid",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.markpaid
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#markPaidInvoice')).modal('hide');
	}
	$scope.refreshSetting = function(invoicesetting) {
		$scope.newInvSetting = invoicesetting;
		$scope.newinvoice.invoiceSettingId = angular.copy(invoicesetting.id);
		$scope.newinvoice.sendEmail = angular.copy(invoicesetting.sendEmail);
		$scope.newinvoice.sendMobile = angular.copy(invoicesetting.sendMobile);
		$scope.newinvoice.expiresIn = angular.copy(invoicesetting.expiryDays);
		$scope.newinvoice.customParams = [];
		for (var param in invoicesetting.customParams) {
			var copyParam = angular.copy(invoicesetting.customParams[param]);
			copyParam.id = null;
			$scope.newinvoice.customParams.push(copyParam);
		}
	}
	$scope.logout = function() {
		$timeout(function(){
			window.location.href="/login?logout";
		}, 1000);
	}
});