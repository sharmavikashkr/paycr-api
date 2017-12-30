app.controller('ConsumerController', function($scope, $http, $rootScope,
		$cookies, $httpParamSerializer) {
	$rootScope.searchConsumerReq = {
		"email" : "",
		"mobile" : "",
		"conCatList" : []
	}
	$rootScope.updateConsumerReq = {
		"conCatList" : [],
		"emailOnPay" : true,
		"emailOnRefund" : true,
		"active" : true
	}
	$scope.newconsumer = {
		"conCats" : [],
		"email" : "",
		"mobile" : "",
		"name" : ""
	}
	$rootScope.searchConsumer = function() {
		var req = {
			method : 'POST',
			url : "/invoice/search/consumer",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $rootScope.searchConsumerReq
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
	$scope.updateConsumerCategory = function() {
		if (!confirm('Update for search criteria above?')) {
			return false;
		}
		this.updateConsumerReq.searchReq = $rootScope.searchConsumerReq;
		var req = {
			method : 'POST',
			url : "/consumer/update/category",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.updateConsumerReq
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
			data : $scope.newconsumer
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
	$scope.updateBulkConsumerUploads = function() {
		var req = {
			method : 'GET',
			url : "/consumer/bulk/uploads/all",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(bulkConsumerUploads) {
			$rootScope.bulkConsumerUploads = bulkConsumerUploads.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.uploadConsumers = function(files) {
		if (!confirm('Upload Consumers?')) {
			return false;
		}
		var fd = new FormData();
		fd.append("consumers", files[0]);
		$http.post("/consumer/bulk/upload", fd, {
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
		angular.element(document.querySelector('#bulkConsumerUploadModal')).modal('hide');
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
			$rootScope.updateCategories = categories.data;
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
	$rootScope.fetchUpdateCategoryValues = function(name) {
		var req = {
			method : 'GET',
			url : "/consumer/category/" + name,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(updateCatValues) {
			$rootScope.updateCatValues = updateCatValues.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$rootScope.fetchNewCategoryValues = function(name) {
		var req = {
			method : 'GET',
			url : "/consumer/category/" + name,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(newCatValues) {
			$rootScope.newCatValues = newCatValues.data;
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
		if (!confirm('Delete tag?')) {
			return false;
		}
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
	$scope.updateEditAddress = function(addressType) {
		if(addressType == 'BILLING') {
			$rootScope.editAddress = angular.copy(this.consumer.billingAddress);
		} else {
			$rootScope.editAddress = angular.copy(this.consumer.shippingAddress);
		}
		if($rootScope.editAddress == null) {
			$rootScope.editAddress = {};
		}
		$rootScope.editAddress.type = addressType;
		$rootScope.editAddress.consumerId = this.consumer.id;
	}
	$scope.copyBillingAddress = function() {
		if (!confirm('Copy billing address to shipping?')) {
			return false;
		}
		$rootScope.editAddress = angular.copy(this.consumer.billingAddress);
		if($rootScope.editAddress == null) {
			return;
		}
		$rootScope.editAddress.id = null;
		$rootScope.editAddress.type = 'SHIPPING';
		$rootScope.editAddress.consumerId = this.consumer.id;
		$scope.saveAddress($rootScope.editAddress, true);
	}
	$scope.saveAddress = function(editAddress, isCopy) {
		if (!isCopy && !this.editAddressForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/consumer/address/update/" + editAddress.consumerId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : editAddress
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.searchConsumer();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#editAddressModal')).modal('hide');
	}
	$scope.updateInvoiceConsumer = function(consumer) {
		$rootScope.saveinvoice = angular.copy($rootScope.newinvoice);
		$rootScope.saveinvoice.consumer = consumer;
	}
	$scope.seachConInvoice = function(consumer) {
		$rootScope.searchConsumerInvoices(consumer);
	}
	$scope.addFilter = function(newFilter) {
		if(newFilter.value == null || newFilter.value == "" || newFilter.name == null || newFilter.name == "") {
			return;
		}
		$rootScope.searchConsumerReq.conCatList.push(angular.copy(newFilter));
		this.newFilter = {"name":"","value":""};
		$rootScope.catValues = [];
	}
	$scope.deleteFilter = function(pos) {
		$rootScope.searchConsumerReq.conCatList.splice(pos, 1);
	}
	$scope.addUpdateFilter = function(updateFilter) {
		if(updateFilter.value == null || updateFilter.value == "" || updateFilter.name == null || updateFilter.name == "") {
			return;
		}
		$rootScope.updateConsumerReq.conCatList.push(angular.copy(updateFilter));
		this.updateFilter = {"name":"","value":""};
		$rootScope.updateCatValues = [];
	}
	$scope.deleteUpdateFilter = function(pos) {
		$rootScope.updateConsumerReq.conCatList.splice(pos, 1);
	}
	$scope.addNewCategory = function(newConCat) {
		if(newConCat.value == null || newConCat.value == "" || newConCat.name == null || newConCat.name == "") {
			return;
		}
		$scope.newconsumer.conCats.push(angular.copy(newConCat));
	}
	$scope.removeNewCategory = function(pos) {
		$scope.newconsumer.conCats.splice(pos, 1);
	}
	$scope.clearConsumerSearch = function() {
		$rootScope.searchConsumerReq = {
			"email" : "",
			"mobile" : "",
			"conCatList" : []
		}
	}
});