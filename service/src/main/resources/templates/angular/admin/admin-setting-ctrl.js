app.controller('AdminSettingController', function($scope, $rootScope, $http,
		$cookies) {
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
	$scope.loadAdminSetting = function() {
		var req = {
			method : 'GET',
			url : "/admin/setting",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(setting) {
			$rootScope.adminSetting = setting.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.uploadBanner = function(files) {
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