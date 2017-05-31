var app = angular.module('payCrAdminApp', [ "ngRoute", "ngCookies" ]);
app.controller('AdminController',
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
	$scope.searchMerchantReq = {
		"name" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : "2017-01-01",
		"createdTo" : "2017-06-30"
	}
	$scope.searchInvoiceReq = {
		"invoiceCode" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : "2017-01-01",
		"createdTo" : "2017-06-30"
	}
	$scope.newsubsmode = {
		"name" : "",
		"rzpMerchantId" : "",
		"rzpKeyId" : "",
		"rzpSecretId" : "",
		"payType" : "",
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
	$scope.offlinesubs = {
		"subscriptionModeId" : 0,
		"pricingId" : 0,
		"merchantId" : 0,
		"paymentRefNo" : ""
	}
	$scope.newmerchant = {
		"name" : "",
		"email" : "",
		"mobile" : ""
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
		}, function(data) {
			$scope.serverMessage(data);
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
		if(!$scope.addUserForm.$valid) {
			return false;
		}
		var req = {
			method : 'post',
			url : "/common/create/user",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newuser
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
			$scope.fetchPricings();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createPricing')).modal('hide');
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
			$scope.fetchPricings();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchSubsModes = function() {
		var req = {
			method : 'GET',
			url : "/subscription/modes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(subsModes) {
			$scope.subsModes = subsModes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createSubsMode = function() {
		if(!$scope.createSubsModeForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/subscription/mode/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newsubsmode
		}
		$http(req).then(function(data) {
			$scope.fetchSubsModes();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#addSubscriptionMode')).modal('hide');
	}
	$scope.toggleSubsMode = function(modeId) {
		var req = {
			method : 'GET',
			url : "/subscription/mode/toggle/" + modeId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.fetchSubsModes();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createOfflineSubs = function(merchantId) {
		$scope.offlinesubs.merchantId = merchantId;
		var req = {
			method : 'POST',
			url : "/subscription/new/offline",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.offlinesubs
		}
		$http(req).then(function(data) {
			$scope.searchMerchant();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createOfflineSubscription')).modal('hide');
	}
	$scope.fetchSubscriptionDetails = function(subscriptionId) {
		var req = {
			method : 'GET',
			url : "/subscription/get/" + subscriptionId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(subscription) {
			$scope.subsinfo = subscription.data;
		}, function(data) {
			$scope.serverMessage(data);
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
			$scope.searchMerchant();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createMerchant')).modal('hide');
	}
	$scope.logout = function() {
		$timeout(function(){
			window.location.href="/adminlogin?logout";
		}, 1000); 
	}
});