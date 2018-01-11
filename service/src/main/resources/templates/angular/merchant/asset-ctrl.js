app.controller('AssetController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	$rootScope.searchAssetReq = {
		"code" : "",
		"name" : ""
	}
	$rootScope.searchAsset = function() {
		var req = {
			method : 'POST',
			url : "/expense/search/asset",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $rootScope.searchAssetReq
		}
		$http(req).then(function(inventories) {
			$rootScope.assetList = inventories.data;
			$scope.loadAssetPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadAssetPage = function(page) {
		var pageSize = 15;
		$rootScope.assetResp = {};
		$rootScope.assetResp.assetList = angular
				.copy($rootScope.assetList);
		$rootScope.assetResp.assetList.splice(pageSize * page,
				$rootScope.assetList.length - pageSize);
		$rootScope.assetResp.assetList.splice(0, pageSize * (page - 1));
		$rootScope.assetResp.page = page;
		$rootScope.assetResp.allPages = [];
		var noOfPages = $rootScope.assetList.length / pageSize;
		if ($rootScope.assetList.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.assetResp.allPages.push(i);
		}
	}
	$scope.updateAsset = function(asset) {
		var req = {
			method : 'POST',
			url : "/asset/update/" + asset.id,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : asset
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchAsset();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createAsset = function() {
		if (!this.addItemForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/asset/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newasset
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchAsset();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createAssetModal')).modal(
				'hide');
	}
	$scope.updateBulkAssetUploads = function() {
		var req = {
			method : 'GET',
			url : "/asset/bulk/uploads/all",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(bulkAssetUploads) {
			$rootScope.bulkAssetUploads = bulkAssetUploads.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.uploadAsset = function(files) {
		if (!confirm('Upload Items?')) {
			return false;
		}
		var fd = new FormData();
		fd.append("asset", files[0]);
		$http.post("/asset/bulk/upload", fd, {
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
		angular.element(document.querySelector('#bulkAssetUploadModal')).modal('hide');
	}
	$scope.fetchAssetStats = function(asset) {
		var req = {
			method : 'GET',
			url : "/asset/stats/" + asset.id,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			invnStats = data.data;
			if(invnStats != null) {
				invnStats.totalNo = invnStats.paidNo + invnStats.unpaidNo;
				invnStats.totalSum = invnStats.paidSum + invnStats.unpaidSum;
			}
			asset.itemStats = angular.copy(invnStats);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.seachItemExpense = function(code) {
		$rootScope.clearExpenseSearch();
		$rootScope.searchExpenseReq.itemCode = code;
		$rootScope.searchExpense();
	}
	$scope.setDefaultTax = function() {
		for(var tax in $rootScope.taxList) {
			if(this.asset.tax.id == $rootScope.taxList[tax].id) {
				this.asset.tax = $rootScope.taxList[tax];
			}
		}
	}
	$scope.updateExpenseItem = function(asset) {
		$rootScope.saveexpense = angular.copy($rootScope.newexpense);
		var asset = {
			"code" : asset.code,
			"name" : asset.name,
			"rate" : asset.rate,
			"tax" : asset.tax,
		}
		$scope.saveexpense.items.push({
			"asset" : asset,
			"quantity" : 1,
			"tax" : asset.tax,
			"price" : 0
		});
		$rootScope.calculateExpTotal();
	}
	$scope.clearAssetSearch = function() {
		$rootScope.searchAssetReq = {
			"code" : "",
			"name" : ""
		}
	}
});