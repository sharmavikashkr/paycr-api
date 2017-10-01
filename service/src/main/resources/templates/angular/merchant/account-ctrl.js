app.controller('AccountController', function($scope, $rootScope, $http,
		$cookies) {
	$scope.updateAccount = function() {
		var req = {
			method : 'POST',
			url : "/merchant/account/update",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.merchant
		}
		$http(req).then(function(merchant) {
			$rootScope.merchant = merchant.data;
			$scope.serverMessage(merchant);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});