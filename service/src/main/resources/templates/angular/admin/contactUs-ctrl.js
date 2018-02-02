app.controller('ContactUsController', function($scope, $http,
		$cookies, $httpParamSerializer) {
	$scope.searchContactUsReq = {};
	$scope.searchPage = function(page) {
		$scope.searchContactUs(page);
	}
	$scope.searchContactUs = function(page) {
		$scope.searchContactUsReq.page = page;
		var req = {
			method : 'POST',
			url : "/contactUs/search",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
				"Content-type" : "application/x-www-form-urlencoded; charset=utf-8"
			},
			data : $httpParamSerializer($scope.searchContactUsReq)
		}
		$http(req).then(function(contactUs) {
			$scope.contactUsResp = {};
			$scope.contactUsResp.contactUsList = contactUs.data;
			$scope.contactUsResp.page = page;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.toggleStatus = function(contactUsId) {
		var req = {
			method : 'GET',
			url : "/contactUs/toggle/" + contactUsId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(contactUs) {
			$scope.searchPage($scope.contactUsResp.page);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.clearContactUsSearch = function() {
		$scope.searchContactUsReq.email = null;
		$scope.searchContactUsReq.type = null;
		$scope.searchContactUsReq.resolved = false;
	}
});