var app = angular.module('payCrApp', ["ngRoute","ngCookies"]);
app.controller('LoginController', 
  function($scope, $http, $cookies, $httpParamSerializer) {
	
    $scope.data = {
        grant_type:"password",
        username: "",
        password: "",
        client_id: "web-client"
    };
    $scope.encoded = btoa("web-client:secret");
     
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
        });
   }
});