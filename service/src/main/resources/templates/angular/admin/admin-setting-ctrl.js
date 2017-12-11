app.controller('AdminSettingController', function($scope, $rootScope, $http,
		$cookies) {
	$rootScope.loadAdminSetting = function() {
		var req = {
			method : 'GET',
			url : "/admin/setting",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(setting) {
			$rootScope.adminSetting = setting.data;
			$scope.setDefaultTax();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.setDefaultTax = function() {
		for(var tax in $rootScope.taxList) {
			if(this.adminSetting.tax.id == $rootScope.taxList[tax].id) {
				this.adminSetting.tax = $rootScope.taxList[tax];
			}
		}
	}
	$scope.saveAdminSetting = function(adminSetting) {
		var req = {
			method : 'POST',
			url : "/admin/setting/update",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : adminSetting
		}
		$http(req).then(function(setting) {
			$rootScope.adminSetting = setting.data;
			$scope.serverMessage(setting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.saveAddress = function(newaddress) {
		var req = {
			method : 'POST',
			url : "/admin/setting/address/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : newaddress
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createTax = function(tax) {
		if (!this.newTaxForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/setting/tax/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : tax
		}
		$http(req).then(function(data) {
			$rootScope.fetchTaxes();
			$scope.serverMessage(setting);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#addTaxModal')).modal('hide');
	}
	$scope.uploadBanner = function(files) {
		if (!confirm('Upload Banner?')) {
			return false;
		}
		var fd = new FormData();
		fd.append("banner", files[0]);
		$http.post("/banner/upload", fd, {
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
	}
});