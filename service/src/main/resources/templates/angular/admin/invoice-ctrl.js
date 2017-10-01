app.controller('InvoiceController', function($scope, $http, $cookies) {
	$scope.searchInvoice = function() {
		var req = {
			method : 'POST',
			url : "/search/invoice",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchInvoiceReq
		}
		$http(req).then(function(invoices) {
			$scope.invoiceList = invoices.data;
			$scope.loadInvoicePage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadInvoicePage = function(page) {
		var pageSize = 15;
		$scope.invoiceResp = {};
		$scope.invoiceResp.invoiceList = angular.copy($scope.invoiceList);
		$scope.invoiceResp.invoiceList.splice(pageSize * page,
				$scope.invoiceList.length - pageSize);
		$scope.invoiceResp.invoiceList.splice(0, pageSize * (page - 1));
		$scope.invoiceResp.page = page;
		$scope.invoiceResp.allPages = [];
		var noOfPages = $scope.invoiceList.length / pageSize;
		if ($scope.invoiceList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$scope.invoiceResp.allPages.push(i);
		}
	}
});