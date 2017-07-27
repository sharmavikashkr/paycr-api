var app = angular.module('payCrApp', [ "ngRoute", "ngCookies", "ngMaterial" ]);
app.config(function($mdDateLocaleProvider) {
    $mdDateLocaleProvider.formatDate = function(date) {
       return moment(date).format('YYYY-MM-DD');
    };
});
app.controller('MerchantController',
function($scope, $http, $cookies, $httpParamSerializer, $timeout) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.server = {
		"hideMessage" : true,
		"respStatus" : "WELCOME!",
		"respMsg" : ":)",
		"isSuccess" : true
	}
	$scope.patterns = {
		"paramNamePattern" : "\\w{1,10}",
		"namePattern" : "[0-9a-zA-Z_\\- ]{1,50}",
		"emailPattern" : "([a-zA-Z0-9_.]{1,})((@[a-zA-Z]{2,})[\\\.]([a-zA-Z]{2}|[a-zA-Z]{3}))",
		"mobilePattern" : "\\d{10}",
		"amountPattern" : "\\d{1,7}",
		"numberPattern" : "\\d{1,4}"
	}
	$scope.searchInvoiceReq = {
		"invoiceCode" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : dateStart,
		"createdTo" : dateNow,
		"page" : "1"
	}
	$scope.newinvoice = {
		"invoiceCode" : "",
		"consumer" : {
			"email" : "",
			"mobile" : "",
			"name" : ""
		},
		"items" : [ {
			"name" : "",
			"rate" : 0,
			"quantity" : 1,
			"price" : 0
		} ],
		"sendEmail" : false,
		"sendSms" : false,
		"addItems" : false,
		"total" : 0.00,
		"tax" : 0.00,
		"discount" : 0,
		"payAmount" : 0,
		"currency" : "INR",
		"expiresIn" : "",
		"invoiceSettingId" : 0,
		"customParams" : [ {
			"paramName" : "",
			"paramValue" : "",
			"provider" : ""
		} ]
	}
	$scope.dismissServerAlert = function() {
		$scope.server.hideMessage = true;
	}
	$scope.serverMessage = function(data) {
		$scope.server.hideMessage = false;
		if(data.status==200) {
			$scope.server.isSuccess = true;
			$scope.server.respStatus = "SUCCESS!";
			$scope.server.respMsg = "operation successful";
		} else if(data.status==401) {
			$scope.server.isSuccess = false;
			$scope.server.respStatus = "FAILURE!";
			$scope.server.respMsg = "unauthorized request";
			$scope.logout();
		} else {
			$scope.server.isSuccess = false;
			$scope.server.respStatus = "FAILURE!";
			$scope.server.respMsg = data.headers('error_message');
		}
	}
	$scope.prepare = function() {
		$scope.fetchMerchant();
		$scope.fetchUser();
		$scope.fetchNotifications();
		$scope.fetchEnums();
	}
	$scope.fetchEnums = function() {
		var req = {
			method : 'GET',
			url : "/enum/providers",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(providers) {
			$scope.paramProviders = providers.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/paymodes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(payModes) {
			$scope.payModes = payModes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});

		var req = {
			method : 'GET',
			url : "/enum/usertypes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(usertypes) {
			$scope.userTypes = usertypes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/timeranges",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(timeranges) {
			$scope.timeRanges = timeranges.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/invoicestatuses",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(invoicestatuses) {
			$scope.invoiceStatuses = invoicestatuses.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/paytypes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(paytypes) {
			$scope.payTypes = paytypes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchMerchant = function() {
		var req = {
			method : 'GET',
			url : "/merchant/get",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(merchant) {
			$scope.merchant = merchant.data;
			$scope.refreshSetting(merchant.data.invoiceSetting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchUser = function() {
		var req = {
			method : 'GET',
			url : "/common/user",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(user) {
			$scope.user = user.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchNotifications = function() {
		var req = {
			method : 'GET',
			url : "/common/notifications",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(notifications) {
			$scope.notices = notifications.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
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
			method : 'post',
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
			method : 'get',
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
	$scope.updateAccount = function() {
		var req = {
			method : 'POST',
			url : "/merchant/account/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.merchant
		}
		$http(req).then(function(merchant) {
			$scope.merchant = merchant.data;
			$scope.serverMessage(merchant);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.searchInvoice = function() {
		var req = {
			method : 'POST',
			url : "/search/invoice",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.searchInvoiceReq
		}
		$http(req).then(function(invoices) {
			$scope.invoiceList = invoices.data;
			$scope.loadInvoicePage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchMyInvoices = function() {
		var req = {
			method : 'GET',
			url : "/common/invoices/",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(myInvoices) {
			$scope.myinvoices = myInvoices.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.savePaymentSetting = function() {
		var req = {
			method : 'POST',
			url : "/merchant/paymentsetting/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.merchant.paymentSetting
		}
		$http(req).then(function(paymentsettings) {
			$scope.merchant.paymentSettings = paymentsettings.data;
			$scope.serverMessage(paymentsettings);
			$scope.editPayPrefs = false;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.saveInvoiceSetting = function(invoiceSetting) {
		var req = {
			method : 'POST',
			url : "/merchant/invoicesetting/update",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : invoiceSetting
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
			$scope.serverMessage(invoicesetting);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.addParam = function() {
		if(!this.addCustomParamForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/merchant/customParam/new/",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newparam
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createParamModal')).modal('hide');
	}
	$scope.deleteParam = function(paramId, paramName) {
		if (!confirm('Delete ' + paramName + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/merchant/customParam/delete/" + paramId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(invoicesetting) {
			$scope.merchant.invoiceSetting = invoicesetting.data;
			$scope.refreshSetting(invoicesetting.data[0]);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchPricings = function() {
		var req = {
			method : 'GET',
			url : "/common/pricings",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(pricings) {
			$scope.pricings = pricings.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
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
	$scope.loadInvoicePage = function(page) {
		var pageSize = 1;
		$scope.invoiceResp = {};
		$scope.invoiceResp.invoiceList = angular.copy($scope.invoiceList);
		$scope.invoiceResp.invoiceList.splice(pageSize * page, $scope.invoiceList.length - pageSize);
		$scope.invoiceResp.invoiceList.splice(0, pageSize * (page - 1));
		$scope.invoiceResp.page = page;
		$scope.invoiceResp.allPages = [];
		var noOfPages = $scope.invoiceList.length/pageSize;
		if($scope.invoiceList.length%pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for(var i = 1; i <= noOfPages; i++) {
			$scope.invoiceResp.allPages.push(i);
		}
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
	$scope.updateInvoiceInfo = function(invoice) {
		$scope.invoiceInfo = invoice;
	}
	$scope.addItem = function() {
		if ($scope.newinvoice.items.length < 5) {
			$scope.newinvoice.items.push({
				"name" : "",
				"rate" : 0,
				"quantity" : 1,
				"price" : 0
			});
		}
	}
	$scope.deleteItem = function(pos) {
		if ($scope.newinvoice.items.length > 1) {
			$scope.newinvoice.items.splice(pos, 1);
			$scope.calculateTotal();
		}
	}
	$scope.calculateTotal = function() {
		var totals = 0;
		if($scope.newinvoice.addItems) {
			for ( var item in $scope.newinvoice.items) {
				if ($scope.newinvoice.items[item].rate == null) {
					$scope.newinvoice.items[item].rate = 0;
				}
				if ($scope.newinvoice.items[item].quantity == null) {
					$scope.newinvoice.items[item].quantity = 0;
				}
				$scope.newinvoice.items[item].price = parseFloat($scope.newinvoice.items[item].rate)
						* parseFloat($scope.newinvoice.items[item].quantity);
				totals = totals
						+ parseFloat($scope.newinvoice.items[item].price);
			}
			$scope.newinvoice.total = totals;
		} else {
			totals = $scope.newinvoice.total;
		}
		if ($scope.newinvoice.tax == null) {
			$scope.newinvoice.tax = 0;
		}
		if ($scope.newinvoice.discount == null) {
			$scope.newinvoice.discount = 0;
		}
		$scope.newinvoice.payAmount = Math.round(totals
				+ parseFloat((parseFloat($scope.newinvoice.tax) * totals) / 100)
				- parseFloat($scope.newinvoice.discount));
	}
	$scope.createInvoice = function() {
		if(!this.createInvoiceForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/invoice/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.newinvoice
		}
		$http(req).then(function(data) {
			$scope.fetchMerchant();
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createInvoiceModal')).modal('hide');
	}
	$scope.enquireInvoice = function(invoiceCode) {
		var req = {
			method : 'GET',
			url : "/invoice/enquire/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.notifyInvoice = function(invoiceCode) {
		var req = {
			method : 'GET',
			url : "/invoice/notify/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.expireInvoice = function(invoiceCode) {
		if (!confirm('Expire ' + invoiceCode + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/invoice/expire/" + invoiceCode,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.refundInvoice = function(invoiceCode, refundAmount) {
		if(refundAmount == undefined) {
			return false;
		}
		if (!confirm('Refund ' + invoiceCode + ' with amount ' + refundAmount + ' ?')) {
			return false;
		}
		var refundRequest = {};
		refundRequest.amount = refundAmount;
		refundRequest.invoiceCode = invoiceCode;
		var req = {
			method : 'POST',
			url : "/invoice/refund",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
		        "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
			},
			data : $httpParamSerializer(refundRequest)
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		$scope.refundAmount = ''
		angular.element(document.querySelector('#refundModal')).modal('hide');
	}
	$scope.markPaidInvoice = function(invoiceCode) {
		if(!this.markPaidForm.$valid) {
			return false;
		}
		this.markpaid.invoiceCode = invoiceCode;
		var req = {
			method : 'POST',
			url : "/invoice/markpaid",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.markpaid
		}
		$http(req).then(function(data) {
			$scope.searchInvoice();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#markPaidModal')).modal('hide');
	}
	$scope.refreshSetting = function(invoicesetting) {
		$scope.newInvSetting = invoicesetting;
		$scope.newinvoice.invoiceSettingId = angular.copy(invoicesetting.id);
		$scope.newinvoice.sendEmail = angular.copy(invoicesetting.sendEmail);
		$scope.newinvoice.sendMobile = angular.copy(invoicesetting.sendMobile);
		$scope.newinvoice.addItems = angular.copy(invoicesetting.addItems);
		$scope.newinvoice.expiresIn = angular.copy(invoicesetting.expiryDays);
		$scope.newinvoice.tax = angular.copy(invoicesetting.tax);
		$scope.newinvoice.customParams = [];
		for (var param in invoicesetting.customParams) {
			var copyParam = angular.copy(invoicesetting.customParams[param]);
			copyParam.id = null;
			$scope.newinvoice.customParams.push(copyParam);
		}
	}
	$scope.logout = function() {
		$timeout(function(){
			window.location.href="/login?logout";
		}, 1000);
	}
});