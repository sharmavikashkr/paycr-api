app.controller('UsersController',
function($scope, $http, $cookies, $httpParamSerializer, $timeout) {
	$scope.fetchMyUsers = function() {
		var req = {
			method : 'GET',
			url : "/common/users",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(myusers) {
			$scope.myusers = myusers.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createUser = function() {
		if(!this.addUserForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/common/create/user",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newuser
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchMyUsers();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createUserModal')).modal('hide');
	}
	$scope.toggleUser = function(userId) {
		var req = {
			method : 'GET',
			url : "/common/toggle/user/" + userId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchMyUsers();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});