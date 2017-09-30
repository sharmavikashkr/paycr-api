app.controller('MerchantController', function($scope, $http, $cookies,
		$httpParamSerializer, $timeout) {
	$scope.searchMerchant = function() {
		var req = {
			method : 'POST',
			url : "/search/merchant",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchMerchantReq
		}
		$http(req).then(function(merchants) {
			$scope.merchantList = merchants.data;
			$scope.loadMerchantPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadMerchantPage = function(page) {
		var pageSize = 15;
		$scope.merchantResp = {};
		$scope.merchantResp.merchantList = angular.copy($scope.merchantList);
		$scope.merchantResp.merchantList.splice(pageSize * page, $scope.merchantList.length - pageSize);
		$scope.merchantResp.merchantList.splice(0, pageSize * (page - 1));
		$scope.merchantResp.page = page;
		$scope.merchantResp.allPages = [];
		var noOfPages = $scope.merchantList.length/pageSize;
		if($scope.merchantList.length%pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for(var i = 1; i <= noOfPages; i++) {
			$scope.merchantResp.allPages.push(i);
		}
	}
	$scope.createOfflineSubs = function(merchantId) {
		this.offlinesubs.merchantId = merchantId;
		var req = {
			method : 'POST',
			url : "/subscription/new/offline",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.offlinesubs
		}
		$http(req).then(function(data) {
			$scope.searchMerchant();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createOfflineSubsModal'))
				.modal('hide');
	}
	$scope.createMerchant = function() {
		if (!this.createMerchantForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/merchant/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newmerchant
		}
		$http(req).then(function(data) {
			$scope.searchMerchant();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createMerchantModal')).modal(
				'hide');
	}
});