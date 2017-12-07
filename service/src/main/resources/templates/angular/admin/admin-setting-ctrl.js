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