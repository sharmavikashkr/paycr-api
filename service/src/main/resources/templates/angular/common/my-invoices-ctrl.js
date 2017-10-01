app.controller('MyInvoicesController', function($scope, $rootScope, $http,
		$cookies) {
	$scope.fetchMyInvoices = function() {
		var req = {
			method : 'GET',
			url : "/common/invoices/",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(myInvoices) {
			$rootScope.myinvoices = myInvoices.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});