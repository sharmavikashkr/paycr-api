var app = angular.module('payCrApp', ["ngRoute","ngCookies"]);
app.controller('LoginController', 
  function($scope, $http, $cookies, $httpParamSerializer) {
	$scope.invalidcreds = false;
    $scope.data = {
        grant_type:"password",
        username: "",
        password: ""
    };
    $scope.encoded = "d2ViLWNsaWVudDozYjVlOGViM2ZjZmFmYTJlN2IzMDJmNzVjMGUxODVkMzNkODY5MGMy";
     
    $scope.login = function() {   
        var req = {
            method: 'POST',
            url: "/oauth/token",
            headers: {
                "Authorization": "Basic " + $scope.encoded,
                "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
            },
            data: $httpParamSerializer($scope.data)
        }
        $http(req).then(function(data){
        	$http.defaults.headers.common.Authorization = 'Bearer ' + data.data.access_token;
            $cookies.put("access_token", data.data.access_token);
            window.location.href="/merchant";
        }, function(data) {
			$scope.invalidcreds = true;
		});
   }
});