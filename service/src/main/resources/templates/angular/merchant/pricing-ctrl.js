app.controller('PricingController', function($scope, $rootScope, $http,
		$cookies) {
	$rootScope.fetchPricings = function() {
		var req = {
			method : 'GET',
			url : "/common/pricings",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(pricings) {
			$rootScope.pricings = pricings.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});