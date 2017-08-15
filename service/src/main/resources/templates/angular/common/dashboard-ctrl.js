app.controller('DashboardController', function($scope, $rootScope, $http,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.statsReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$scope.loadDashboard = function() {
		var req = {
			method : 'POST',
			url : "/common/dashboard",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.statsReq
		}
		$http(req).then(function(response) {
			$rootScope.statsResponse = response.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});