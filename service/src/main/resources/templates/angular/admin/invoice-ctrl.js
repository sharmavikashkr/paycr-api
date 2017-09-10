app.controller('InvoiceController', function($scope, $http, $rootScope,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.searchInvoiceReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
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
			$rootScope.invoiceList = invoices.data;
			$scope.loadInvoicePage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadInvoicePage = function(page) {
		var pageSize = 15;
		$rootScope.invoiceResp = {};
		$rootScope.invoiceResp.invoiceList = angular
				.copy($rootScope.invoiceList);
		$rootScope.invoiceResp.invoiceList.splice(pageSize * page,
				$rootScope.invoiceList.length - pageSize);
		$rootScope.invoiceResp.invoiceList.splice(0, pageSize * (page - 1));
		$rootScope.invoiceResp.page = page;
		$rootScope.invoiceResp.allPages = [];
		var noOfPages = $rootScope.invoiceList.length / pageSize;
		if ($rootScope.invoiceList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.invoiceResp.allPages.push(i);
		}
	}
	$scope.updateInvoiceInfo = function(invoice) {
		$rootScope.invoiceInfo = invoice;
	}
	$scope.searchChildInvoices = function(invoice) {
		$scope.searchInvoiceReq.merchant = null;
		$scope.searchInvoiceReq.parentInvoiceCode = invoice.invoiceCode;
		$scope.searchInvoiceReq.invoiceType = 'SINGLE';
		$scope.searchInvoiceReq.email = null;
		$scope.searchInvoiceReq.mobile = null;
		$scope.searchInvoiceReq.invoiceCode = null;
		$scope.searchInvoiceReq.invoiceStatus = null;
		$scope.searchInvoice();
	}
	$scope.searchMerchantInvoices = function(invoice) {
		$scope.searchInvoiceReq.merchant = invoice.merchant.id;
		$scope.searchInvoiceReq.parentInvoiceCode = null;
		$scope.searchInvoiceReq.invoiceType = null;
		$scope.searchInvoiceReq.email = null;
		$scope.searchInvoiceReq.mobile = null;
		$scope.searchInvoiceReq.invoiceCode = null;
		$scope.searchInvoiceReq.invoiceStatus = null;
		$scope.searchInvoice();
	}
	$scope.searchConsumerInvoices = function(invoice) {
		$scope.searchInvoiceReq.merchant = null;
		$scope.searchInvoiceReq.parentInvoiceCode = null;
		$scope.searchInvoiceReq.invoiceType = null;
		$scope.searchInvoiceReq.email = invoice.consumer.email;
		$scope.searchInvoiceReq.mobile = invoice.consumer.mobile;
		$scope.searchInvoiceReq.invoiceCode = null;
		$scope.searchInvoiceReq.invoiceStatus = null;
		$scope.searchInvoice();
	}
});