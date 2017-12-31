app.controller('ExpenseController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$rootScope.searchExpenseReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$rootScope.searchExpense = function() {
		var req = {
			method : 'POST',
			url : "/expense/search/expense",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchExpenseReq
		}
		$http(req).then(function(expenses) {
			$rootScope.expenseList = expenses.data;
			$scope.loadExpensePage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.setSupplier = function(newsup, from) {
		if(from == "EMAIL") {
			var email = newsup.email;
			for(var index in $rootScope.supplierList) {
				var supplier = $rootScope.supplierList[index];
				if(supplier.email == email){
					newsup.email = supplier.email;
					newsup.mobile = supplier.mobile;
					newsup.name = supplier.name;
					break;
				}
			}
		} else if(from == "MOBILE") {
			var mobile = newsup.mobile;
			for(var index in $rootScope.supplierList) {
				var supplier = $rootScope.supplierList[index];
				if(supplier.mobile == mobile){
					newsup.email = supplier.email;
					newsup.mobile = supplier.mobile;
					newsup.name = supplier.name;
					break;
				}
			}
		} else if(from == "NAME") {
			var name = newsup.name;
			for(var index in $rootScope.supplierList) {
				var supplier = $rootScope.supplierList[index];
				if(supplier.name == name){
					newsup.email = supplier.email;
					newsup.mobile = supplier.mobile;
					newsup.name = supplier.name;
					break;
				}
			}
		}
	}
	$scope.setAsset = function(item, from) {
		if(from == "CODE") {
			for(var index in $rootScope.assetList) {
				var ast = $rootScope.assetList[index];
				if(ast.code == item.asset.code){
					item.asset.name = ast.name;
					item.asset.rate = ast.rate;
					for(var tax in $rootScope.taxList) {
						if(ast.tax.id == $rootScope.taxList[tax].id) {
							item.tax = $rootScope.taxList[tax];
							break;
						}
					}
					$scope.calculateExpTotal();
					break;
				}
			}
		} else {
			for(var index in $rootScope.assetList) {
				var ast = $rootScope.assetList[index];
				if(ast.name == item.asset.name){
					item.asset.code = ast.code;
					item.asset.rate = ast.rate;
					for(var tax in $rootScope.taxList) {
						if(ast.tax.id == $rootScope.taxList[tax].id) {
							item.tax = $rootScope.taxList[tax];
							break;
						}
					}
					$scope.calculateExpTotal();
					break;
				}
			}
		}
	}
	$scope.loadExpensePage = function(page) {
		var pageSize = 15;
		$rootScope.expenseResp = {};
		$rootScope.expenseResp.expenseList = angular
				.copy($rootScope.expenseList);
		$rootScope.expenseResp.expenseList.splice(pageSize * page,
				$rootScope.expenseList.length - pageSize);
		$rootScope.expenseResp.expenseList.splice(0, pageSize * (page - 1));
		$rootScope.expenseResp.page = page;
		$rootScope.expenseResp.allPages = [];
		var noOfPages = $rootScope.expenseList.length / pageSize;
		if ($rootScope.expenseList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.expenseResp.allPages.push(i);
		}
	}
	$scope.updateExpenseInfo = function(expense) {
		$rootScope.expenseInfo = angular.copy(expense);
	}
	$scope.updateSaveExpense = function(expense) {
		$rootScope.saveexpense = angular.copy(expense);
		if(expense.id != null) {
			$rootScope.saveexpense.update = true;
		} else {
			$rootScope.saveexpense.update = false;
		}
	}
	$scope.addItem = function() {
		if ($scope.saveexpense.items.length < 5) {
			var asset = {
				"name" : "",
				"rate" : 0
			}
			$scope.saveexpense.items.push({
				"asset" : asset,
				"quantity" : 1,
				"price" : 0
			});
		}
	}
	$scope.addItemXs = function(item) {
		if ($scope.saveexpense.items.length < 5) {
			var asset = {
				"code" : item.asset.code,
				"name" : item.asset.name,
				"rate" : item.asset.rate
			}
			$scope.saveexpense.items.push({
				"asset" : asset,
				"quantity" : item.quantity,
				"tax" : item.tax,
				"price" : 0
			});
		}
		angular.element(document.querySelector('#addExpenseItemXsModal')).modal('hide');
		angular.element(document.querySelector('#createExpenseXsModal')).modal('show');
		$scope.calculateExpTotal();
	}
	$scope.deleteItem = function(pos) {
		$scope.saveexpense.items.splice(pos, 1);
		$scope.calculateExpTotal();
	}
	$rootScope.calculateExpTotal = function() {
		var totalPrice = 0;
		var totalRate = 0;
		if($scope.saveexpense.addItems) {
			for ( var item in $scope.saveexpense.items) {
				var itemTax = 0;
				if ($scope.saveexpense.items[item].asset.rate == null) {
					$scope.saveexpense.items[item].asset.rate = 0;
				}
				if ($scope.saveexpense.items[item].quantity == null) {
					$scope.saveexpense.items[item].quantity = 0;
				}
				if ($scope.saveexpense.items[item].tax != null) {
					itemTax = parseFloat($scope.saveexpense.items[item].tax.value).toFixed(2);
				}
				var itemTotal = parseFloat((parseFloat($scope.saveexpense.items[item].asset.rate)
						* parseFloat($scope.saveexpense.items[item].quantity)).toFixed(2));
				$scope.saveexpense.items[item].price = parseFloat((itemTotal + parseFloat((itemTotal * itemTax) / 100)).toFixed(2));
				totalRate = totalRate + itemTotal;
				totalPrice = totalPrice + parseFloat($scope.saveexpense.items[item].price);
			}
			$scope.saveexpense.total = parseFloat(totalRate.toFixed(2));
			$scope.saveexpense.totalPrice = parseFloat(totalPrice.toFixed(2));
		} else {
			totalPrice = $scope.saveexpense.total;
			$scope.saveexpense.totalPrice = parseFloat(totalPrice.toFixed(2));
		}
		if ($scope.saveexpense.shipping == null) {
			$scope.saveexpense.shipping = 0;
		}
		if ($scope.saveexpense.discount == null) {
			$scope.saveexpense.discount = 0;
		}
		$scope.saveexpense.payAmount = parseFloat((totalPrice
				+ parseFloat($scope.saveexpense.shipping)
				- parseFloat($scope.saveexpense.discount)).toFixed(2));
	}
	$scope.createExpense = function() {
		if(!this.createExpenseForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/expense/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.saveexpense
		}
		$http(req).then(function(expense) {
			$rootScope.searchExpense();
			$scope.serverMessage(expense);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createExpenseModal')).modal('hide');
		angular.element(document.querySelector('#createExpenseXsModal')).modal('hide');
	}
	$scope.updateExpensePayInfo = function(expense) {
		$rootScope.expensePayInfo = angular.copy(expense);
		var req = {
			method : 'GET',
			url : "/expense/payments/" + expense.expenseCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(allPayments) {
			$rootScope.expensePayInfo.allPayments = allPayments.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.refundExpense = function(expenseCode, refundAmount) {
		if(refundAmount == undefined) {
			return false;
		}
		if (!confirm('Refund ' + expenseCode + ' with amount ' + refundAmount + ' ?')) {
			return false;
		}
		var refundRequest = {};
		refundRequest.amount = refundAmount;
		refundRequest.expenseCode = expenseCode;
		var req = {
			method : 'POST',
			url : "/expense/refund",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
		        "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
			},
			data : $httpParamSerializer(refundRequest)
		}
		$http(req).then(function(data) {
			$rootScope.searchExpense();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#expenseRefundModal')).modal('hide');
	}
	$scope.markPaidExpense = function(expenseCode) {
		if(!this.markPaidForm.$valid) {
			return false;
		}
		this.markpaid.expenseCode = expenseCode;
		var req = {
			method : 'POST',
			url : "/expense/markpaid",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.markpaid
		}
		$http(req).then(function(data) {
			$rootScope.searchExpense();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#markExpensePaidModal')).modal('hide');
	}
	$scope.uploadAttachment = function(files) {
		if (!confirm('Upload Attachment?')) {
			return false;
		}
		var expenseCode = this.expenseInfo.expenseCode;
		var fd = new FormData();
		fd.append("attach", files[0]);
		$http.post("/expense/"+expenseCode+"/attachment/new", fd, {
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
		angular.element(document.querySelector('#expenseAttachmentModal')).modal('hide');
		$rootScope.searchExpense();
	}
	$rootScope.clearExpenseSearch = function() {
		$rootScope.searchExpenseReq.email = '';
		$rootScope.searchExpenseReq.mobile = '';
		$rootScope.searchExpenseReq.expenseCode = '';
		$rootScope.searchExpenseReq.expenseStatus = null;
		$rootScope.searchExpenseReq.itemCode = '';
	}
	$rootScope.searchSupplierExpenses = function(supplier) {
		$rootScope.clearExpenseSearch();
		$rootScope.searchExpenseReq.email = supplier.email;
		$rootScope.searchExpenseReq.mobile = supplier.mobile;
		$rootScope.searchExpense();
	}
	$scope.fetchTimelines = function(expense) {
		$rootScope.timelineExpense = expense;
		var req = {
			method : 'GET',
			url : "/common/timeline/EXPENSE/"+expense.id,
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
	$scope.addCommentExpense = function(objectId) {
		var timeline = {};
		timeline.objectId = objectId;
		timeline.message = this.newInvComment;
		timeline.objectType = 'EXPENSE';
		var req = {
			method : 'PUT',
			url : "/common/timeline/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : timeline
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#expenseTimelineModal')).modal('hide');
		this.newInvComment = "";
	}
});