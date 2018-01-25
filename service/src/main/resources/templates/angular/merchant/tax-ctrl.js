app.controller('TaxController', function($scope, $rootScope, $http, $cookies) {
	$scope.month = "01-2018";
	$scope.loadGstr1Report = function(month) {
		var req = {
			method : 'GET',
			url : "/gst/gstr1/" + month,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(gstr1) {
			$rootScope.gstr1Report = gstr1.data;
			$rootScope.gstr1ReportResp = {};
			$scope.loadB2CSmallPage();
			$scope.loadB2CLargePage();
			$scope.loadB2BPage();
			$scope.loadB2BNotePage();
			$scope.loadB2CNotePage();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadB2CSmallPage = function(page) {
		var pageSize = 10;
		$rootScope.gstr1ReportResp.b2cSmall = {};
		$rootScope.gstr1ReportResp.b2cSmall = angular.copy($rootScope.gstr1Report.b2cSmall);
		$rootScope.gstr1ReportResp.b2cSmall.splice(pageSize * page, $rootScope.gstr1Report.b2cSmall.length - pageSize);
		$rootScope.gstr1ReportResp.b2cSmall.splice(0, pageSize * (page - 1));
		$rootScope.gstr1ReportResp.b2cSmall.page = page;
		$rootScope.gstr1ReportResp.b2cSmall.allPages = [];
		var noOfPages = $rootScope.gstr1Report.b2cSmall.length / pageSize;
		if ($rootScope.gstr1Report.b2cSmall.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.gstr1ReportResp.b2cSmall.allPages.push(i);
		}
	}
	$scope.loadB2CLargePage = function(page) {
		var pageSize = 10;
		$rootScope.gstr1ReportResp.b2cLarge = {};
		$rootScope.gstr1ReportResp.b2cLarge = angular.copy($rootScope.gstr1Report.b2cLarge);
		$rootScope.gstr1ReportResp.b2cLarge.splice(pageSize * page, $rootScope.gstr1Report.b2cLarge.length - pageSize);
		$rootScope.gstr1ReportResp.b2cLarge.splice(0, pageSize * (page - 1));
		$rootScope.gstr1ReportResp.b2cLarge.page = page;
		$rootScope.gstr1ReportResp.b2cLarge.allPages = [];
		var noOfPages = $rootScope.gstr1Report.b2cLarge.length / pageSize;
		if ($rootScope.gstr1Report.b2cLarge.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.gstr1ReportResp.b2cLarge.allPages.push(i);
		}
	}
	$scope.loadB2BPage = function(page) {
		var pageSize = 10;
		$rootScope.gstr1ReportResp.b2b = {};
		$rootScope.gstr1ReportResp.b2b = angular.copy($rootScope.gstr1Report.b2b);
		$rootScope.gstr1ReportResp.b2b.splice(pageSize * page, $rootScope.gstr1Report.b2b.length - pageSize);
		$rootScope.gstr1ReportResp.b2b.splice(0, pageSize * (page - 1));
		$rootScope.gstr1ReportResp.b2b.page = page;
		$rootScope.gstr1ReportResp.b2b.allPages = [];
		var noOfPages = $rootScope.gstr1Report.b2b.length / pageSize;
		if ($rootScope.gstr1Report.b2b.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.gstr1ReportResp.b2b.allPages.push(i);
		}
	}
	$scope.loadB2BNotePage = function(page) {
		var pageSize = 10;
		$rootScope.gstr1ReportResp.b2bNote = {};
		$rootScope.gstr1ReportResp.b2bNote = angular.copy($rootScope.gstr1Report.b2bNote);
		$rootScope.gstr1ReportResp.b2bNote.splice(pageSize * page, $rootScope.gstr1Report.b2bNote.length - pageSize);
		$rootScope.gstr1ReportResp.b2bNote.splice(0, pageSize * (page - 1));
		$rootScope.gstr1ReportResp.b2bNote.page = page;
		$rootScope.gstr1ReportResp.b2bNote.allPages = [];
		var noOfPages = $rootScope.gstr1Report.b2bNote.length / pageSize;
		if ($rootScope.gstr1Report.b2bNote.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.gstr1ReportResp.b2bNote.allPages.push(i);
		}
	}
	$scope.loadB2CNotePage = function(page) {
		var pageSize = 10;
		$rootScope.gstr1ReportResp.b2cNote = {};
		$rootScope.gstr1ReportResp.b2cNote = angular.copy($rootScope.gstr1Report.b2bNote);
		$rootScope.gstr1ReportResp.b2cNote.splice(pageSize * page, $rootScope.gstr1Report.b2bNote.length - pageSize);
		$rootScope.gstr1ReportResp.b2cNote.splice(0, pageSize * (page - 1));
		$rootScope.gstr1ReportResp.b2cNote.page = page;
		$rootScope.gstr1ReportResp.b2cNote.allPages = [];
		var noOfPages = $rootScope.gstr1Report.b2cNote.length / pageSize;
		if ($rootScope.gstr1Report.b2cNote.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.gstr1ReportResp.b2cNote.allPages.push(i);
		}
	}
});