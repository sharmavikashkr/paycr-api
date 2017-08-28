app.controller('InvoiceController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$rootScope.searchInvoiceReq = {
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
	$scope.setConsumer = function(from) {
		if(from == "EMAIL") {
			var email = this.saveinvoice.consumer.email;
			for(var index in $rootScope.consumerList) {
				var consumer = $rootScope.consumerList[index];
				if(consumer.email == email){
					this.saveinvoice.consumer.email = consumer.email;
					this.saveinvoice.consumer.mobile = consumer.mobile;
					this.saveinvoice.consumer.name = consumer.name;
					break;
				}
			}
		} else if(from == "MOBILE") {
			var mobile = this.saveinvoice.consumer.mobile;
			for(var index in $rootScope.consumerList) {
				var consumer = $rootScope.consumerList[index];
				if(consumer.mobile == mobile){
					this.saveinvoice.consumer.email = consumer.email;
					this.saveinvoice.consumer.mobile = consumer.mobile;
					this.saveinvoice.consumer.name = consumer.name;
					break;
				}
			}
		} else if(from == "NAME") {
			var name = this.saveinvoice.consumer.name;
			for(var index in $rootScope.consumerList) {
				var consumer = $rootScope.consumerList[index];
				if(consumer.name == name){
					this.saveinvoice.consumer.email = consumer.email;
					this.saveinvoice.consumer.mobile = consumer.mobile;
					this.saveinvoice.consumer.name = consumer.name;
					break;
				}
			}
		}
	}
	$scope.setInventory = function(item) {
		for(var index in $rootScope.inventoryList) {
			var invn = $rootScope.inventoryList[index];
			if(invn.name == item.name){
				item.rate = invn.rate;
				$scope.calculateTotal();
				break;
			}
		}
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
		$rootScope.invoiceInfo = angular.copy(invoice);
	}
	$scope.updateSaveInvoice = function(invoice) {
		$rootScope.saveinvoice = angular.copy(invoice);
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
	$rootScope.calculateTotal = function() {
		var totals = 0;
		if($scope.saveinvoice.addItems) {
			for ( var item in $scope.saveinvoice.items) {
				if ($scope.saveinvoice.items[item].rate == null) {
					$scope.saveinvoice.items[item].rate = 0;
				}
				if ($scope.saveinvoice.items[item].quantity == null) {
					$scope.saveinvoice.items[item].quantity = 0;
				}
				$scope.saveinvoice.items[item].price = parseFloat((parseFloat($scope.saveinvoice.items[item].rate)
						* parseFloat($scope.saveinvoice.items[item].quantity)).toFixed(2));
				totals = totals + parseFloat($scope.saveinvoice.items[item].price);
			}
			$scope.saveinvoice.total = parseFloat(totals.toFixed(2));
		} else {
			totals = $scope.saveinvoice.total;
		}
		if ($scope.saveinvoice.taxValue == null) {
			$scope.saveinvoice.taxValue = 0;
		}
		if ($scope.saveinvoice.discount == null) {
			$scope.saveinvoice.discount = 0;
		}
		$scope.saveinvoice.payAmount = parseFloat((totals
				+ parseFloat((parseFloat($scope.saveinvoice.taxValue) * totals) / 100)
				- parseFloat($scope.saveinvoice.discount)).toFixed(2));
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
	$scope.uploadAttachment = function(files) {
		if (!confirm('Upload Attachment?')) {
			return false;
		}
		var invoiceCode = this.invoiceInfo.invoiceCode;
		var fd = new FormData();
		fd.append("attach", files[0]);
		$http.post("/invoice/"+invoiceCode+"/attachment/new", fd, {
			transformRequest : angular.identity,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
				'Content-Type' : undefined
			}
		}).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#attachmentModal')).modal('hide');
		$rootScope.searchInvoice();
	}
});