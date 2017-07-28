app.controller('ReportsController',
function($scope, $http, $cookies, $httpParamSerializer, $timeout) {
	$scope.fetchReports = function() {
		var req = {
			method : 'GET',
			url : "/reports",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(reports) {
			$scope.reports = reports.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createReport = function() {
		var req = {
			method : 'POST',
			url : "/reports/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newreport
		}
		$http(req).then(function(data) {
			$scope.fetchReports();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		this.newreport = null;
	}
	$scope.loadReport = function(report) {
		var req = {
			method : 'POST',
			url : "/reports/load",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : report
		}
		$http(req).then(function(invoiceReports) {
			$scope.invoiceReports = invoiceReports.data;
			$scope.loadedreport = angular.copy(report);
			$scope.loadReportPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.downloadReport = function(report) {
		var req = {
			method : 'POST',
			url : "/reports/download",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token"),
				"Accept" : "text/csv"
			},
			data : report
		}
		$http(req).then(function(content) {
		    var hiddenElement = document.createElement('a');
		    hiddenElement.href = 'data:attachment/csv,' + encodeURI(content.data);
		    hiddenElement.target = '_blank';
		    hiddenElement.download = 'report.csv';
		    hiddenElement.click();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.deleteReport = function(reportId, reportName) {
		if (!confirm('Delete ' + reportName + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/reports/delete/" + reportId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchReports();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadReportPage = function(page) {
		var pageSize = 15;
		$scope.reportsResp = {};
		$scope.reportsResp.invoiceReports = angular.copy($scope.invoiceReports);
		$scope.reportsResp.invoiceReports.splice(pageSize * page, $scope.invoiceReports.length - pageSize);
		$scope.reportsResp.invoiceReports.splice(0, pageSize * (page - 1));
		$scope.reportsResp.page = page;
		$scope.reportsResp.allPages = [];
		var noOfPages = $scope.invoiceReports.length/pageSize;
		if($scope.invoiceReports.length%pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for(var i = 1; i <= noOfPages; i++) {
			$scope.reportsResp.allPages.push(i);
		}
	}
});