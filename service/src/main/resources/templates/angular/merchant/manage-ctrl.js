app.controller('ManageController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	$rootScope.searchConsumerReq = {
		"email" : "",
		"mobile" : "",
		"conCatList" : []
	}
	$rootScope.searchConsumer = function() {
		var req = {
			method : 'POST',
			url : "/search/consumer",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.searchConsumerReq
		}
		$http(req).then(function(consumers) {
			$rootScope.consumerList = consumers.data;
			$scope.loadConsumerPage(1);
			$scope.fetchCategories();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadConsumerPage = function(page) {
		var pageSize = 15;
		$rootScope.consumerResp = {};
		$rootScope.consumerResp.consumerList = angular
				.copy($rootScope.consumerList);
		$rootScope.consumerResp.consumerList.splice(pageSize * page,
				$rootScope.consumerList.length - pageSize);
		$rootScope.consumerResp.consumerList.splice(0, pageSize * (page - 1));
		$rootScope.consumerResp.page = page;
		$rootScope.consumerResp.allPages = [];
		var noOfPages = $rootScope.consumerList.length / pageSize;
		if ($rootScope.consumerList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.consumerResp.allPages.push(i);
		}
	}
	$rootScope.fetchInventory = function() {
		var req = {
			method : 'GET',
			url : "/inventory/get",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(inventory) {
			$rootScope.inventoryList = inventory.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.updateConsumer = function(consumer) {
		var req = {
			method : 'POST',
			url : "/consumer/update/" + consumer.id,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : consumer
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchConsumer();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createConsumer = function() {
		if (!this.addConsumerForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/consumer/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newconsumer
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchConsumer();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createConsumerModal')).modal(
				'hide');
	}
	$rootScope.fetchCategories = function() {
		var req = {
			method : 'GET',
			url : "/consumer/categories",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(categories) {
			$rootScope.categories = categories.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$rootScope.fetchCategoryValues = function(name) {
		var req = {
			method : 'GET',
			url : "/consumer/category/" + name,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(catValues) {
			$rootScope.catValues = catValues.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.addCategory = function(consumerId, newConCat) {
		var req = {
			method : 'POST',
			url : "/consumer/category/new/" + consumerId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : newConCat
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchConsumer();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.deleteCategory = function(consumerId, conCatId) {
		var req = {
			method : 'GET',
			url : "/consumer/category/delete/" + consumerId + "/" + conCatId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchConsumer();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createInventory = function() {
		if (!this.addItemForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/inventory/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newinventory
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchInventory();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createInventoryModal')).modal(
				'hide');
	}
	$scope.updateInvoiceConsumer = function(consumer) {
		$rootScope.saveinvoice = angular.copy($rootScope.newinvoice);
		$rootScope.saveinvoice.consumer = consumer;
	}
	$scope.seachConInvoice = function(consumer) {
		$rootScope.searchInvoiceReq.email = consumer.email;
		$rootScope.searchInvoiceReq.mobile = consumer.mobile;
		$rootScope.searchInvoiceReq.invoiceCode = '';
		$rootScope.searchInvoiceReq.invoiceType = null;
		$rootScope.searchInvoiceReq.parentInvoiceCode = '';
		$rootScope.searchInvoiceReq.invoiceStatus = null;
		$rootScope.searchInvoice();
	}
	$scope.updateInvoiceItem = function(inventory) {
		$rootScope.saveinvoice = angular.copy($rootScope.newinvoice);
		var inventory = {
			"name" : inventory.name,
			"rate" : inventory.rate,
		}
		$scope.saveinvoice.items.push({
			"inventory" : inventory,
			"quantity" : 1,
			"price" : 0
		});
		$rootScope.calculateTotal();
	}
	$scope.addFilter = function(newFilter) {
		$rootScope.searchConsumerReq.conCatList.push(angular.copy(newFilter));
		this.newFilter = {"name":"","value":""};
		$rootScope.catValues = [];
	}
	$scope.deleteFilter = function(pos) {
		$rootScope.searchConsumerReq.conCatList.splice(pos, 1);
	}
});