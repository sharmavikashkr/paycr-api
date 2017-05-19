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
		"namePattern" : "[0-9a-zA-Z_ ]{1,50}",
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
		"createdTo" : "2017-06-30"
	}
	$scope.newparam = {
		"paramName" : "",
		"provider" : "OPTIONAL"
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
		"shipping" : 0,
		"discount" : 0,
		"payAmount" : 0,
		"currency" : "INR",
		"expiresIn" : "",
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
			$scope.server.respMsg = "something went wrong";
		}
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
			$scope.refreshSetting();
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
			url : "/merchant/invoices/",
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
	$scope.saveSetting = function() {
		var req = {
			method : 'POST',
			url : "/merchant/setting/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.merchant.setting
		}
		$http(req).then(function(setting) {
			$scope.merchant.setting = setting.data;
			$scope.refreshSetting();
			$scope.serverMessage(setting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.addParam = function() {
		if(!$scope.addCustomParamForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/merchant/customParam/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newparam
		}
		$http(req).then(function(setting) {
			$scope.merchant.setting = setting.data;
			$scope.refreshSetting();
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
		$http(req).then(function(setting) {
			$scope.merchant.setting = setting.data;
			$scope.refreshSetting();
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
		if ($scope.newinvoice.shipping == null) {
			$scope.newinvoice.shipping = 0;
		}
		if ($scope.newinvoice.discount == null) {
			$scope.newinvoice.discount = 0;
		}
		$scope.newinvoice.payAmount = totals
				+ parseFloat($scope.newinvoice.shipping)
				- parseFloat($scope.newinvoice.discount);
	}
	$scope.createInvoice = function() {
		if(!$scope.createInvoiceForm.$valid) {
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
	$scope.refreshSetting = function() {
		$scope.newinvoice.sendEmail = angular
				.copy($scope.merchant.setting.sendEmail);
		$scope.newinvoice.sendMobile = angular
				.copy($scope.merchant.setting.sendMobile);
		$scope.newinvoice.expiresIn = angular
				.copy($scope.merchant.setting.expiryDays);
		$scope.newinvoice.customParams = [];
		for ( var param in $scope.merchant.setting.customParams) {
			var copyParam = angular
					.copy($scope.merchant.setting.customParams[param]);
			copyParam.id = null;
			$scope.newinvoice.customParams.push(copyParam);
		}
		$scope.newparam = {
			"paramName" : "",
			"provider" : "OPTIONAL"
		}
	}
	$scope.logout = function() {
		$timeout(function(){
			window.location.href="/login?logout";
		}, 1000);
	}
});