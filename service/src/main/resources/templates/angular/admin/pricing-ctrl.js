app.controller('PricingController', function($scope, $http, $cookies) {
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
	$scope.createSubsMode = function() {
		if (!this.createSubsModeForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/subscription/mode/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newsubsmode
		}
		$http(req).then(function(data) {
			$scope.fetchSubsModes();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createSubsModeModal')).modal(
				'hide');
	}
	$scope.toggleSubsMode = function(modeId) {
		var req = {
			method : 'GET',
			url : "/subscription/mode/toggle/" + modeId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.fetchSubsModes();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});