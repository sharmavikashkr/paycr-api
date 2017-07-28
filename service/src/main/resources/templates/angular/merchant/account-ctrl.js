app.controller('AccountController', function($scope, $http, $cookies,
		$httpParamSerializer, $timeout) {
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
			$scope.merchant = merchant.data;
			$scope.serverMessage(merchant);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});