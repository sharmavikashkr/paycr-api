app.controller('InvoiceController', function($scope, $http, $rootScope,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.searchInvoiceReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$rootScope.searchInvoice = function() {
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
	$scope.updateInvoicePayInfo = function(invoice) {
		$rootScope.invoicePayInfo = angular.copy(invoice);
		var req = {
			method : 'GET',
			url : "/invoice/payments/" + invoice.invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(allPayments) {
			$rootScope.invoicePayInfo.allPayments = allPayments.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$rootScope.clearInvoiceSearch = function() {
		$rootScope.searchInvoiceReq.parentInvoiceCode = '';
		$rootScope.searchInvoiceReq.invoiceType = null;
		$rootScope.searchInvoiceReq.email = '';
		$rootScope.searchInvoiceReq.mobile = '';
		$rootScope.searchInvoiceReq.invoiceCode = '';
		$rootScope.searchInvoiceReq.invoiceStatus = null;
	}
	$scope.searchChildInvoices = function(invoiceCode) {
		$rootScope.clearInvoiceSearch();
		$scope.searchInvoiceReq.parentInvoiceCode = invoiceCode;
		$scope.searchInvoice();
	}
	$scope.searchMerchantInvoices = function(invoice) {
		$rootScope.clearInvoiceSearch();
		$scope.searchInvoiceReq.merchant = invoice.merchant.id;
		$scope.searchInvoice();
	}
	$scope.searchConsumerInvoices = function(consumer) {
		$rootScope.clearInvoiceSearch();
		$scope.searchInvoiceReq.email = consumer.email;
		$scope.searchInvoiceReq.mobile = consumer.mobile;
		$scope.searchInvoice();
	}
	$scope.fetchTimelines = function(invoice) {
		$rootScope.timelineInvoice = invoice;
		var req = {
			method : 'GET',
			url : "/common/timeline/INVOICE/"+invoice.id,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(timelines) {
			$rootScope.timelineList = timelines.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});