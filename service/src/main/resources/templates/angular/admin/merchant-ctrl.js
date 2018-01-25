app.controller('MerchantController', function($scope, $rootScope, $http,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.searchMerchantReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$rootScope.searchMerchant = function() {
		var req = {
			method : 'POST',
			url : "/admin/search/merchant",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchMerchantReq
		}
		$http(req).then(function(merchants) {
			$rootScope.merchantList = merchants.data;
			$scope.loadMerchantPage(1);
			$rootScope.loadAdminSetting();
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
	$scope.fetchSubscriptionDetails = function(pricingId) {
		var req = {
			method : 'GET',
			url : "/subscription/get/" + pricingId,
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
	$scope.calculateSubsAmount = function() {
		if(this.offlinesubs.pricing != null && this.offlinesubs.quantity != null) {
			var total = parseFloat(this.offlinesubs.pricing.rate) * parseFloat(this.offlinesubs.quantity);
			this.offlinesubs.total = total;
			this.offlinesubs.payAmount = total + parseFloat((parseFloat($scope.offlinesubs.pricing.interstateTax.value) * total) / 100);
		}
	}
	$scope.createOfflineSubs = function(merchantId) {
		this.offlinesubs.merchantId = merchantId;
		this.offlinesubs.pricingId = this.offlinesubs.pricing.id; 
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
	$scope.clearMerchantSearch = function() {
		$scope.searchMerchantReq.email = null;
		$scope.searchMerchantReq.mobile = null;
		$scope.searchMerchantReq.name = null;
	}
});