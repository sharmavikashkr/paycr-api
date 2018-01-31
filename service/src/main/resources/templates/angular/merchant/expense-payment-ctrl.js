app.controller('ExpensePaymentController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$rootScope.searchPaymentReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$rootScope.searchExpensePayment = function() {
		var req = {
			method : 'POST',
			url : "/expense/search/payment",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchPaymentReq
		}
		$http(req).then(function(payments) {
			$rootScope.expPaymentList = payments.data;
			$scope.loadPaymentPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadPaymentPage = function(page) {
		var pageSize = 15;
		$rootScope.expPaymentResp = {};
		$rootScope.expPaymentResp.paymentList = angular
				.copy($rootScope.expPaymentList);
		$rootScope.expPaymentResp.paymentList.splice(pageSize * page,
				$rootScope.expPaymentList.length - pageSize);
		$rootScope.expPaymentResp.paymentList.splice(0, pageSize * (page - 1));
		$rootScope.expPaymentResp.page = page;
		$rootScope.expPaymentResp.allPages = [];
		var noOfPages = $rootScope.expPaymentList.length / pageSize;
		if ($rootScope.expPaymentList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.paymentResp.allPages.push(i);
		}
	}
	$scope.downloadCsv = function() {
		var req = {
			method : 'POST',
			url : "/expense/search/payment/download",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
				"Accept" : "text/csv"
			},
			data : $scope.searchPaymentReq
		}
		$http(req).then(function(content) {
			var hiddenElement = document.createElement('a');
			hiddenElement.href = 'data:attachment/csv,'
					+ encodeURI(content.data);
			hiddenElement.target = '_blank';
			hiddenElement.download = 'expense-payments.csv';
			hiddenElement.click();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.mailCsv = function() {
		var req = {
			method : 'POST',
			url : "/expense/search/payment/mail",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchPaymentReq
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.seachPayExpense = function(expenseCode) {
		$rootScope.clearExpenseSearch();
		$rootScope.searchExpenseReq.expenseCode = expenseCode;
		$rootScope.searchExpense();
	}
	$scope.clearPaymentSearch = function() {
		$rootScope.searchPaymentReq.expenseCode = '';
		$rootScope.searchPaymentReq.paymentRefNo = '';
		$rootScope.searchPaymentReq.payType = null;
	}
});