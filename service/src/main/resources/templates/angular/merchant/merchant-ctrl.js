var app = angular.module('payCrMerchantApp', [ "ngRoute", "ngCookies", "ngMaterial" ]);
app.config(function($mdDateLocaleProvider) {
    $mdDateLocaleProvider.formatDate = function(date) {
       return moment(date).format('YYYY-MM-DD');
    };
});
app.controller('MerchantController', function($scope, $rootScope, $http, $cookies, $timeout) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
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
			$rootScope.paramProviders = providers.data;
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
			$rootScope.payModes = payModes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});

		var req = {
			method : 'GET',
			url : "/enum/usertypes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(usertypes) {
			$rootScope.userTypes = usertypes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/timeranges",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(timeranges) {
			$rootScope.timeRanges = timeranges.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/invoicestatuses",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(invoicestatuses) {
			$rootScope.invoiceStatuses = invoicestatuses.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/paytypes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(paytypes) {
			$rootScope.payTypes = paytypes.data;
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
			$rootScope.user = user.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchNotifications = function() {
		var req = {
			method : 'GET',
			url : "/common/notifications",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(notifications) {
			$rootScope.notices = notifications.data;
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
			$rootScope.merchant = merchant.data;
			$scope.refreshSetting(merchant.data.invoiceSetting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.refreshSetting = function(invoicesetting) {
		$rootScope.newInvSetting = invoicesetting;
		$rootScope.newinvoice.sendEmail = angular.copy(invoicesetting.sendEmail);
		$rootScope.newinvoice.sendMobile = angular.copy(invoicesetting.sendMobile);
		$rootScope.newinvoice.addItems = angular.copy(invoicesetting.addItems);
		$rootScope.newinvoice.expiresIn = angular.copy(invoicesetting.expiryDays);
		$rootScope.newinvoice.tax = angular.copy(invoicesetting.tax);
		$rootScope.newinvoice.customParams = [];
		for (var param in invoicesetting.customParams) {
			var copyParam = angular.copy(invoicesetting.customParams[param]);
			copyParam.id = null;
			$rootScope.newinvoice.customParams.push(copyParam);
		}
	}
	$scope.logout = function() {
		$timeout(function(){
			window.location.href="/login?logout";
		}, 1000);
	}
});