var app = angular.module('payCrApp', [ "ngRoute", "ngCookies" ]);
app.controller('MerchantController',
function($scope, $http, $cookies, $httpParamSerializer) {
	$scope.patterns = {
		"paramNamePattern" : "\\w{1,10}",
		"namePattern" : "\\w{1,50}",
		"emailPattern" : "([a-zA-Z0-9_.]{1,})((@[a-zA-Z]{2,})[\\\.]([a-zA-Z]{2}|[a-zA-Z]{3}))",
		"mobilePattern" : "\\d{10}",
		"amountPattern" : "\\d{1,7}",
		"numberPattern" : "\\d{1,4}",
	}
	$scope.invoices = [];
	$scope.searchRequest = {
		"invoiceCode" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : "2017-01-01",
		"createdTo" : "2017-05-01"
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
			data : $scope.searchRequest
		}
		$http(req).then(function(invoices) {
			$scope.invoices = invoices.data;
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
		});
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
});