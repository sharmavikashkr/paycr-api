app.controller('InventoryController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	$rootScope.searchInventoryReq = {
		"code" : "",
		"name" : ""
	}
	$rootScope.searchInventory = function() {
		var req = {
			method : 'POST',
			url : "/search/inventory",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $rootScope.searchInventoryReq
		}
		$http(req).then(function(inventories) {
			$rootScope.inventoryList = inventories.data;
			$scope.loadInventoryPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadInventoryPage = function(page) {
		var pageSize = 15;
		$rootScope.inventoryResp = {};
		$rootScope.inventoryResp.inventoryList = angular
				.copy($rootScope.inventoryList);
		$rootScope.inventoryResp.inventoryList.splice(pageSize * page,
				$rootScope.inventoryList.length - pageSize);
		$rootScope.inventoryResp.inventoryList.splice(0, pageSize * (page - 1));
		$rootScope.inventoryResp.page = page;
		$rootScope.inventoryResp.allPages = [];
		var noOfPages = $rootScope.inventoryList.length / pageSize;
		if ($rootScope.inventoryList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.inventoryResp.allPages.push(i);
		}
	}
	$scope.updateInventory = function(inventory) {
		var req = {
			method : 'POST',
			url : "/inventory/update/" + inventory.id,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : inventory
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchInventory();
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
			$scope.searchInventory();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createInventoryModal')).modal(
				'hide');
	}
	$scope.updateBulkInventoryUploads = function() {
		var req = {
			method : 'GET',
			url : "/inventory/bulk/uploads/all",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(bulkInventoryUploads) {
			$rootScope.bulkInventoryUploads = bulkInventoryUploads.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.uploadInventory = function(files) {
		if (!confirm('Upload Items?')) {
			return false;
		}
		var fd = new FormData();
		fd.append("inventory", files[0]);
		$http.post("/inventory/bulk/upload", fd, {
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
		angular.element(document.querySelector('#bulkInventoryUploadModal')).modal('hide');
	}
	$scope.fetchInventoryStats = function(inventory) {
		var req = {
			method : 'GET',
			url : "/inventory/stats/" + inventory.id,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			invnStats = data.data;
			if(invnStats != null) {
				invnStats.totalNo = invnStats.createdNo + invnStats.paidNo + invnStats.unpaidNo + invnStats.expiredNo + invnStats.declinedNo;
				invnStats.totalSum = invnStats.createdSum + invnStats.paidSum + invnStats.unpaidSum + invnStats.expiredSum + invnStats.declinedSum;
			}
			inventory.itemStats = angular.copy(invnStats);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.seachItemInvoice = function(code) {
		$rootScope.clearInvoiceSearch();
		$rootScope.searchInvoiceReq.itemCode = code;
		$rootScope.searchInvoice();
	}
	$scope.setDefaultTax = function() {
		for(var tax in $rootScope.taxList) {
			if(this.inventory.tax.id == $rootScope.taxList[tax].id) {
				this.inventory.tax = $rootScope.taxList[tax];
			}
		}
	}
	$scope.updateInvoiceItem = function(inventory) {
		$rootScope.saveinvoice = angular.copy($rootScope.newinvoice);
		var inventory = {
			"code" : inventory.code,
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
	$scope.clearInventorySearch = function() {
		$rootScope.searchInventoryReq = {
			"code" : "",
			"name" : ""
		}
	}
});