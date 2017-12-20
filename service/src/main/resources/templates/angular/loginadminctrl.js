var app = angular.module('payCrAppAdmin', ["ngRoute","ngCookies"]);
app.controller('LoginController', function($scope, $http, $cookies, $httpParamSerializer) {
	$scope.invalidcreds = false;
    $scope.data = {
        email : "",
        password : ""
    };
    
    $scope.login = function() {
        var req = {
            method : 'POST',
            url : "/secure/login",
            headers : {
                "Content-type" : "application/x-www-form-urlencoded; charset=utf-8"
            },
            data : $httpParamSerializer($scope.data)
        }
        $http(req).then(function(data) {
            $cookies.put("access_token", data.data.access_token);
            window.location.href = "/admin";
        }, function(data) {
    		$scope.invalidcreds = true;
		});
    }
});