app.controller('InvoiceSettingController', function($scope, $http, $cookies,
		$httpParamSerializer, $timeout) {
	$scope.savePaymentSetting = function() {
		var req = {
			method : 'POST',
			url : "/merchant/paymentsetting/update",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.merchant.paymentSetting
		}
		$http(req).then(function(paymentsettings) {
			$scope.merchant.paymentSettings = paymentsettings.data;
			$scope.serverMessage(paymentsettings);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.saveInvoiceSetting = function(invoiceSetting) {
		var req = {
			method : 'POST',
			url : "/merchant/invoicesetting/update",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : invoiceSetting
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
			$scope.serverMessage(invoicesetting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.addParam = function() {
		if (!this.addCustomParamForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/merchant/customParam/new/",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newparam
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createParamModal')).modal(
				'hide');
	}
	$scope.deleteParam = function(paramId, paramName) {
		if (!confirm('Delete ' + paramName + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/merchant/customParam/delete/" + paramId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});