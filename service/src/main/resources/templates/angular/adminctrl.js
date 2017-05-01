var app = angular.module('payCrAdminApp', [ "ngRoute", "ngCookies" ]);
app.controller('AdminController',
function($scope, $http, $cookies, $httpParamSerializer) {
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
	$scope.searchMerchantReq = {
		"name" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : "2017-01-01",
		"createdTo" : "2017-05-01"
	}
	$scope.newsubssetting = {
		"rzpMerchantId" : "",
		"rzpKeyId" : "",
		"rzpSecretId" : "",
		"active" : true
	}
	$scope.newpricing = {
		"name" : "",
		"description" : "",
		"invoiceLimit" : 0,
		"startAmount" : 0,
		"endAmount" : 0,
		"duration" : 0,
		"rate" : 0
	}
	$scope.newmerchant = {
		"name" : "",
		"email" : "",
		"mobile" : "",
		"adminName" : "",
		"pricingId" : 1
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
		} else {
			$scope.server.isSuccess = false;
			$scope.server.respStatus = "FAILURE!";
			$scope.server.respMsg = "something went wrong";
		}
	}
	$scope.fetchNotifications = function() {
		var req = {
			method : 'GET',
			url : "/admin/notifications",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(notifications) {
			$scope.notices = notifications.data;
		});
	}
	$scope.searchMerchant = function() {
		var req = {
			method : 'POST',
			url : "/search/merchant",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.searchMerchantReq
		}
		$http(req).then(function(merchants) {
			$scope.merchants = merchants.data;
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
	$scope.createPricing = function() {
		if(!$scope.createPricingForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/pricing/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newpricing
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchPricings();
		});
	}
	$scope.togglePricing = function(pricingId) {
		var req = {
			method : 'GET',
			url : "/admin/pricing/toggle/" + pricingId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchPricings();
		});
	}
	$scope.fetchSubsSettings = function() {
		var req = {
			method : 'GET',
			url : "/admin/subscription/settings",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(subsSettings) {
			$scope.subsSettings = subsSettings.data;
		});
	}
	$scope.createSubsSetting = function() {
		if(!$scope.createSubsSettingForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/subscription/setting/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newsubssetting
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchSubsSettings();
		});
	}
	$scope.toggleSubsSetting = function(subsSettingId) {
		var req = {
			method : 'GET',
			url : "/admin/subscription/setting/toggle/" + subsSettingId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchSubsSettings();
		});
	}
	$scope.createMerchant = function() {
		if(!$scope.createMerchantForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/merchant/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newmerchant
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.seachMerchant();
		});
	}
});