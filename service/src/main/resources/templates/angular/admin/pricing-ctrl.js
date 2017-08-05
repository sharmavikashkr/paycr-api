app.controller('PricingController', function($scope, $rootScope, $http, $cookies) {
	$scope.fetchPricings = function() {
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
	$scope.createPricing = function() {
		if (!this.createPricingForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/pricing/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newpricing
		}
		$http(req).then(function(data) {
			$scope.fetchPricings();
			$scope.serverMessage(data);
			this.newpricing = {};
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createPricingModal')).modal(
				'hide');
	}
	$scope.togglePricing = function(pricingId) {
		var req = {
			method : 'GET',
			url : "/admin/pricing/toggle/" + pricingId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.fetchPricings();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});