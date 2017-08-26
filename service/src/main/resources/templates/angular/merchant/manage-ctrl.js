app.controller('ManageController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	$scope.fetchConsumers = function() {
		var req = {
			method : 'GET',
			url : "/consumer/get",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(consumers) {
			$rootScope.consumerList = consumers.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchInventory = function() {
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
			$scope.fetchConsumers();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createConsumerModal')).modal(
				'hide');
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
		$rootScope.saveinvoice = $rootScope.newinvoice;
		$rootScope.saveinvoice.consumer = consumer;
	}
	$scope.seachConInvoice = function(consumer) {
		$rootScope.searchInvoiceReq.email = consumer.email;
		$rootScope.searchInvoiceReq.mobile = consumer.mobile;
		$rootScope.searchInvoice();
	}
	$scope.updateInvoiceItem = function(inventory) {
		$rootScope.saveinvoice = $rootScope.newinvoice;
		$scope.saveinvoice.items.push({
			"name" : inventory.name,
			"rate" : inventory.rate,
			"quantity" : 1,
			"price" : 0
		});
		$rootScope.calculateTotal();
	}
});