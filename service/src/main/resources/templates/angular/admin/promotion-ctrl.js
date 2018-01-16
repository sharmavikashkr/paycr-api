app.controller('PromotionController', function($scope, $rootScope, $http,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.searchPromotionReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$rootScope.searchPromotion = function() {
		var req = {
			method : 'POST',
			url : "/admin/search/promotion",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchPromotionReq
		}
		$http(req).then(function(promotions) {
			$rootScope.promotionList = promotions.data;
			$scope.loadPromotionPage(1);
			$rootScope.loadAdminSetting();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadPromotionPage = function(page) {
		var pageSize = 15;
		$rootScope.promotionResp = {};
		$rootScope.promotionResp.promotionList = angular
				.copy($rootScope.promotionList);
		$rootScope.promotionResp.promotionList.splice(pageSize * page,
				$rootScope.promotionList.length - pageSize);
		$rootScope.promotionResp.promotionList.splice(0, pageSize * (page - 1));
		$rootScope.promotionResp.page = page;
		$rootScope.promotionResp.allPages = [];
		var noOfPages = $rootScope.promotionList.length / pageSize;
		if ($rootScope.promotionList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.promotionResp.allPages.push(i);
		}
	}
	$scope.createPromotion = function() {
		if (!this.createPromotionForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/promotion/send",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newpromotion
		}
		$http(req).then(function(data) {
			$scope.searchPromotion();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createPromotionModal')).modal('hide');
	}
	$scope.notify = function(promoId) {
		if (!confirm('Notify again?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/promotion/notify/" + promoId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.searchPromotion();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createPromotionModal')).modal('hide');
	}
	$scope.clearPromotionSearch = function() {
		$scope.searchPromotionReq.email = null;
		$scope.searchPromotionReq.phone = null;
		$scope.searchPromotionReq.name = null;
	}
});