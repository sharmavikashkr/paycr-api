app.controller('ProfileController', function($scope, $rootScope, $http, $cookies,
			$httpParamSerializer) {
	$scope.newissue = {
		"name" : $rootScope.user.name,
		"email" : $rootScope.user.email,
		"type" : "User Issue"
	};
	$scope.updateAddress = function() {
		if (!this.editAddressForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/profile/update/address",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.user.address
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#editProfileAddressModal')).modal('hide');
	}
	$scope.createIssue = function() {
		if (!this.registerIssueForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/contactUs/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
				"Content-type" : "application/x-www-form-urlencoded; charset=utf-8"
			},
			data : $httpParamSerializer($scope.newissue)
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#registerIssueModal')).modal('hide');
	}
	$rootScope.loadProfileTab = function() {
		angular.element(document.querySelector('#profileTabId')).removeClass('active');
	}
	$scope.changePassword = function() {
		if (!this.changePasswordForm.$valid) {
			return false;
		}
		if (this.changeReq.newPass != this.changeReq.retypePass) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/profile/change/password",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
				"Content-type" : "application/x-www-form-urlencoded; charset=utf-8"
			},
			data : $httpParamSerializer(this.changeReq)
		}
		$http(req).then(function(data) {
			alert('SUCCESS! relogin');
			$scope.logout();
		}, function(data) {
			alert('FAILURE! relogin');
			$scope.logout();
		});
		angular.element(document.querySelector('#changePasswordModal')).modal('hide');
	}
});