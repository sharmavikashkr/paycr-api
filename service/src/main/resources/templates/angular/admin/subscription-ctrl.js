app.controller('SubscriptionController', function($scope, $http, $rootScope,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.searchSubsReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$rootScope.searchSubscription = function() {
		var req = {
			method : 'POST',
			url : "/search/subscription",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchSubsReq
		}
		$http(req).then(function(subs) {
			$rootScope.subsList = subs.data;
			$scope.loadSubsPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadSubsPage = function(page) {
		var pageSize = 15;
		$rootScope.subsResp = {};
		$rootScope.subsResp.subsList = angular
				.copy($rootScope.subsList);
		$rootScope.subsResp.subsList.splice(pageSize * page,
				$rootScope.subsList.length - pageSize);
		$rootScope.subsResp.subsList.splice(0, pageSize * (page - 1));
		$rootScope.subsResp.page = page;
		$rootScope.subsResp.allPages = [];
		var noOfPages = $rootScope.subsList.length / pageSize;
		if ($rootScope.subsList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.subsResp.allPages.push(i);
		}
	}
	$scope.clearSubscriptionSearch = function() {
		$scope.searchSubsReq.merchant = null;
		$scope.searchSubsReq.pricing = null;
		$scope.searchSubsReq.subsMode = null;
		$scope.searchSubsReq.subsCode = null;
	}
});