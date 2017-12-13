app.controller('AccountController', function($scope, $rootScope, $http,
		$cookies) {
	$scope.updateAccount = function() {
		var req = {
			method : 'POST',
			url : "/merchant/account/update",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $rootScope.merchant
		}
		$http(req).then(function(merchant) {
			$rootScope.merchant = merchant.data;
			$scope.serverMessage(merchant);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.saveAccAddress = function() {
		var req = {
			method : 'POST',
			url : "/merchant/address/update",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $rootScope.merchant.address
		}
		$http(req).then(function(merchant) {
			$rootScope.merchant = merchant.data;
			$scope.serverMessage(merchant);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#editAccAddressModal')).modal('hide');
	}
});