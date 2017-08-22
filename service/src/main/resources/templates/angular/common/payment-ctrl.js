app.controller('PaymentController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$rootScope.searchPaymentReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$scope.searchPayment = function() {
		var req = {
			method : 'POST',
			url : "/search/payment",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchPaymentReq
		}
		$http(req).then(function(payments) {
			$rootScope.paymentList = payments.data;
			$scope.loadPaymentPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadPaymentPage = function(page) {
		var pageSize = 15;
		$rootScope.paymentResp = {};
		$rootScope.paymentResp.paymentList = angular
				.copy($rootScope.paymentList);
		$rootScope.paymentResp.paymentList.splice(pageSize * page,
				$rootScope.paymentList.length - pageSize);
		$rootScope.paymentResp.paymentList.splice(0, pageSize * (page - 1));
		$rootScope.paymentResp.page = page;
		$rootScope.paymentResp.allPages = [];
		var noOfPages = $rootScope.paymentList.length / pageSize;
		if ($rootScope.paymentList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.paymentResp.allPages.push(i);
		}
	}
});