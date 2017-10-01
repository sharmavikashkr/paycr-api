app.controller('MerchantController', function($scope, $rootScope, $http,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.searchMerchantReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
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
			$rootScope.merchantList = merchants.data;
			$scope.loadMerchantPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadMerchantPage = function(page) {
		var pageSize = 15;
		$rootScope.merchantResp = {};
		$rootScope.merchantResp.merchantList = angular
				.copy($rootScope.merchantList);
		$rootScope.merchantResp.merchantList.splice(pageSize * page,
				$rootScope.merchantList.length - pageSize);
		$rootScope.merchantResp.merchantList.splice(0, pageSize * (page - 1));
		$rootScope.merchantResp.page = page;
		$rootScope.merchantResp.allPages = [];
		var noOfPages = $rootScope.merchantList.length / pageSize;
		if ($rootScope.merchantList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.merchantResp.allPages.push(i);
		}
	}
	$scope.fetchSubscriptionDetails = function(subscriptionId) {
		var req = {
			method : 'GET',
			url : "/subscription/get/" + subscriptionId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(subscription) {
			$rootScope.subsinfo = subscription.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.updateOffSubsMerchant = function(merchant) {
		$rootScope.offSubsmerchant = merchant;
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