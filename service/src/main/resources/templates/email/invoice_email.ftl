<html style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-family: sans-serif;-webkit-text-size-adjust: 100%;-ms-text-size-adjust: 100%;font-size: 10px;-webkit-tap-highlight-color: rgba(0,0,0,0);">
<head style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
<style style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
.invoice-title h2, .invoice-title h3 {
	display: inline-block;
}

.table>tbody>tr>.no-line {
	border-top: none;
}

.table>thead>tr>.no-line {
	border-bottom: none;
}

.table>tbody>tr>.thick-line {
	border-top: 2px solid;
}
</style>
</head>
<body style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin: 0;font-family: &quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size: 14px;line-height: 1.42857143;color: #333;background-color: #fff;">
	<div class="container" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding-right: auto;padding-left: auto;margin-right: auto;margin-left: auto;width:100%">
		<div class="row" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box">
			<div class="col-xs-12" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;float: left;width: 100%;">
				<div class="invoice-title" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box; padding-right:15px; padding-left:15px;">
					<h4 style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;orphans: 3;widows: 3;page-break-after: avoid;font-family: inherit;font-weight: 500;line-height: 1.1;color: inherit;margin-top: 20px;margin-bottom: 10px;font-size: 20px;display: inline-block;">Invoice</h4>
					<h4 class="pull-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;orphans: 3;widows: 3;page-break-after: avoid;font-family: inherit;font-weight: 500;line-height: 1.1;color: inherit;margin-top: 20px;margin-bottom: 10px;font-size: 20px;display: inline-block;float: right!important;"># ${invoice.invoiceCode}</h4>
				</div>
				<hr style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;height: 0;margin-top: 20px;margin-bottom: 20px;border: 0;border-top: 1px solid #eee;">
				<div class="row" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-right: -15px;margin-left: -15px;">
					<div class="col-xs-6" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;float: left;width: 50%;">
						<strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Billed To:</strong><br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> ${invoice.consumer.name}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
						${invoice.consumer.email}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> ${invoice.consumer.mobile}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
					</div>
					<div class="col-xs-6 text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;text-align: right;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;float: left;width: 50%;">
						<address style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-bottom: 20px;font-style: normal;line-height: 1.42857143;">
							<strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Billed From:</strong><br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> ${merchant.name}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
							${merchant.email}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> ${merchant.mobile}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
						</address>
					</div>
				</div>
				<div class="row" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-right: -15px;margin-left: -15px;">
					<div class="col-xs-6" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;float: left;width: 50%;">
						<address style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-bottom: 20px;font-style: normal;line-height: 1.42857143;">
							<strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Payment Mode:</strong><br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> Online<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
						</address>
					</div>
					<div class="col-xs-6 text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;text-align: right;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;float: left;width: 50%;">
						<address style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-bottom: 20px;font-style: normal;line-height: 1.42857143;">
							<strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Order Date:</strong><br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> ${invoice.created?date}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
							<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
						</address>
					</div>
				</div>
				<div class="row" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-right: -15px;margin-left: -15px;">
					<div class="col-xs-6" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;float: left;width: 50%;">
						<#list invoice.customParams as param>
						<address style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-bottom: 20px;font-style: normal;line-height: 1.42857143;">
							<strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">${param.paramName}:</strong><br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> ${param.paramValue}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
						</address>
						</#list>
						<div class="col-xs-6 text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;text-align: right;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;float: left;width: 50%;">
							<address style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-bottom: 20px;font-style: normal;line-height: 1.42857143;">
								<strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Due Date:</strong><br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"> ${invoice.expiry?date}<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
								<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
							</address>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-right: -15px;margin-left: -15px;">
			<div class="col-md-12" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;width: 100%;">
				<div class="panel panel-default" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-bottom: 20px;background-color: #fff;border: 1px solid transparent;border-radius: 4px;-webkit-box-shadow: 0 1px 1px rgba(0,0,0,.05);box-shadow: 0 1px 1px rgba(0,0,0,.05);border-color: #ddd;">
					<div class="panel-heading" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 10px 15px;border-bottom: 1px solid transparent;border-top-left-radius: 3px;border-top-right-radius: 3px;color: #333;background-color: #f5f5f5;border-color: #ddd;">
						<h3 class="panel-title" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;orphans: 3;widows: 3;page-break-after: avoid;font-family: inherit;font-weight: 500;line-height: 1.1;color: inherit;margin-top: 0;margin-bottom: 0;font-size: 16px;text-align:center;">
							<strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Order summary</strong>
						</h3>
					</div>
					<div class="panel-body" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 15px;">
						<div class="table-responsive" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
							<table class="table table-condensed" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;border-spacing: 0;border-collapse: collapse!important;background-color: transparent;width: 100%;max-width: 100%;margin-bottom: 0;font-size: 12px;">
								<thead style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;display: table-header-group;">
									<tr style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;page-break-inside: avoid;">
										<td style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Item</strong></td>
										<td class="text-center" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: center;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Price</strong></td>
										<td class="text-center" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: center;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Quantity</strong></td>
										<td class="text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: right;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Totals</strong></td>
									</tr>
								</thead>
								<tbody style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
									<#list invoice.items as item>
									<tr style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;page-break-inside: avoid;">
										<td style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;">${item.name}</td>
										<td class="text-center" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: center;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;">&#8377; ${item.rate}</td>
										<td class="text-center" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: center;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;">${item.quantity}</td>
										<td class="text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: right;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;white-space: nowrap;background-color: #fff!important;">&#8377; ${item.price}</td>
									</tr>
									</#list>
									<tr style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;page-break-inside: avoid;">
										<td class="no-line" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"></td>
										<td class="no-line" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"></td>
										<td class="no-line text-center" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: center;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Shipping</strong></td>
										<td class="no-line text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: right;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;">&#8377; ${invoice.shipping}</td>
									</tr>
									<tr style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;page-break-inside: avoid;">
										<td class="no-line" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"></td>
										<td class="no-line" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"></td>
										<td class="no-line text-center" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: center;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Discount</strong></td>
										<td class="no-line text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: right;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;">&#8377; ${invoice.discount}</td>
									</tr>
									<tr style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;page-break-inside: avoid;">
										<td class="no-line" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"></td>
										<td class="no-line" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"></td>
										<td class="no-line text-center" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: center;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">Total</strong></td>
										<td class="no-line text-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 5px;text-align: right;line-height: 1.42857143;vertical-align: top;border-top: none;white-space: nowrap;background-color: #fff!important;">&#8377; ${invoice.payAmount}</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-right: -15px;margin-left: -15px;">
			<div class="col-md-12" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;width: 100%;">
				<a href="${invoiceUrl}" target="_blank" class="btn btn-info pull-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;display: inline-block;padding: 6px 12px;margin-bottom: 0;font-size: 14px;font-weight: 400;line-height: 1.42857143;text-align: center;white-space: nowrap;vertical-align: middle;-ms-touch-action: manipulation;touch-action: manipulation;cursor: pointer;-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;background-image: none;border: 1px solid transparent;border-radius: 4px;color: #fff;background-color: #5bc0de;border-color: #46b8da;float: right!important;">Proceed To Pay</a>
			</div>
		</div>
		<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
	</div>
</body>
</html>