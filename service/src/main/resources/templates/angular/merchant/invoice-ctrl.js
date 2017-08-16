app.controller('InvoiceController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$rootScope.searchInvoiceReq = {
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
	$scope.updateSaveInvoice = function(invoice) {
		$rootScope.saveinvoice = invoice;
	}
	$scope.addItem = function() {
		if ($scope.saveinvoice.items.length < 5) {
			$scope.saveinvoice.items.push({
				"name" : "",
				"rate" : 0,
				"quantity" : 1,
				"price" : 0
			});
		}
	}
	$scope.addItemXs = function(item) {
		if ($scope.saveinvoice.items.length < 5) {
			$scope.saveinvoice.items.push({
				"name" : item.name,
				"rate" : item.rate,
				"quantity" : item.quantity,
				"price" : parseFloat(item.rate) * parseFloat(item.quantity)
			});
		}
		angular.element(document.querySelector('#addItemXsModal')).modal('hide');
		angular.element(document.querySelector('#createInvoiceXsModal')).modal('show');
		$scope.calculateTotal();
	}
	$scope.deleteItem = function(pos) {
		$scope.saveinvoice.items.splice(pos, 1);
		$scope.calculateTotal();
	}
	$scope.calculateTotal = function() {
		var totals = 0;
		if($scope.saveinvoice.addItems) {
			for ( var item in $scope.saveinvoice.items) {
				if ($scope.saveinvoice.items[item].rate == null) {
					$scope.saveinvoice.items[item].rate = 0;
				}
				if ($scope.saveinvoice.items[item].quantity == null) {
					$scope.saveinvoice.items[item].quantity = 0;
				}
				$scope.saveinvoice.items[item].price = parseFloat($scope.saveinvoice.items[item].rate)
						* parseFloat($scope.saveinvoice.items[item].quantity);
				totals = totals
						+ parseFloat($scope.saveinvoice.items[item].price);
			}
			$scope.saveinvoice.total = totals;
		} else {
			totals = $scope.saveinvoice.total;
		}
		if ($scope.saveinvoice.taxValue == null) {
			$scope.saveinvoice.taxValue = 0;
		}
		if ($scope.saveinvoice.discount == null) {
			$scope.saveinvoice.discount = 0;
		}
		$scope.saveinvoice.payAmount = Math.round(totals
				+ parseFloat((parseFloat($scope.saveinvoice.taxValue) * totals) / 100)
				- parseFloat($scope.saveinvoice.discount));
	}
	$scope.createInvoice = function() {
		if(!this.createInvoiceForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/invoice/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.saveinvoice
		}
		$http(req).then(function(invoice) {
			$scope.searchInvoice();
			$scope.serverMessage(invoice);
			$rootScope.invoiceInfo = invoice.data;
			angular.element(document.querySelector('#invoiceNotifyModal')).modal('show');
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createInvoiceModal')).modal('hide');
		angular.element(document.querySelector('#createInvoiceXsModal')).modal('hide');
	}
	$scope.enquireInvoice = function(invoiceCode) {
		var req = {
			method : 'GET',
			url : "/invoice/enquire/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.notifyInvoice = function(invoiceCode) {
		if(!this.invoiceNotifyForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/invoice/notify/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.invoiceNotify
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#invoiceNotifyModal')).modal('hide');
	}
	$scope.expireInvoice = function(invoiceCode) {
		if (!confirm('Expire ' + invoiceCode + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/invoice/expire/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.refundInvoice = function(invoiceCode, refundAmount) {
		if(refundAmount == undefined) {
			return false;
		}
		if (!confirm('Refund ' + invoiceCode + ' with amount ' + refundAmount + ' ?')) {
			return false;
		}
		var refundRequest = {};
		refundRequest.amount = refundAmount;
		refundRequest.invoiceCode = invoiceCode;
		var req = {
			method : 'POST',
			url : "/invoice/refund",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
		        "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
			},
			data : $httpParamSerializer(refundRequest)
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#refundModal')).modal('hide');
	}
	$scope.markPaidInvoice = function(invoiceCode) {
		if(!this.markPaidForm.$valid) {
			return false;
		}
		this.markpaid.invoiceCode = invoiceCode;
		var req = {
			method : 'POST',
			url : "/invoice/markpaid",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.markpaid
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#markPaidModal')).modal('hide');
	}
});