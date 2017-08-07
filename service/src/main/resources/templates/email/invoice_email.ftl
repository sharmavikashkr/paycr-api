<html style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-family: sans-serif;-webkit-text-size-adjust: 100%;-ms-text-size-adjust: 100%;font-size: 10px;-webkit-tap-highlight-color: rgba(0,0,0,0);">
<head style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <meta charset="utf-8" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <meta name="viewport" content="width=device-width, initial-scale=1" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <meta name="description" content="" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <meta name="author" content="" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <title style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">PayCr</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <link href="https://fonts.googleapis.com/css?family=Lato" rel="stylesheet" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <link href="https://fonts.googleapis.com/css?family=Catamaran:100,200,300,400,500,600,700,800,900" rel="stylesheet" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
    <link href="https://fonts.googleapis.com/css?family=Muli" rel="stylesheet" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
</head>
<body style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin: 0;font-family: &quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size: 14px;line-height: 1.42857143;color: #333;background-color: #fff;">
<div style="background: gray;-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
	<div class="row text-center" style="background: darksalmon;-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;text-align: center;margin-right: -15px;margin-left: -15px;">
		<div class="panel-body" style="width: 100%;-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 15px;">
			<h4 style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-family: inherit;font-weight: 500;line-height: 1.1;color: inherit;margin-top: 10px;margin-bottom: 10px;font-size: 18px;">Invoice request from <strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">${invoice.merchant.name}</strong></h4>
			<div style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">Invoice Id : <strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">${invoice.invoiceCode}</strong></div>
			<div style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;"><p style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;orphans: 3;widows: 3;margin: 0 0 10px;">Some Note</p></div>
		</div>
	</div>
	<div class="row" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
		<div class="panel-body" style="background: white;margin-right: 5px;margin-left: 5px;-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 15px;">
			<div style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">Billed To : </div>
			<p style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;orphans: 3;widows: 3;margin: 0 0 10px;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">${invoice.consumer.email}</strong></p>
			<br style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
			<div style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">Pay By : </div>
			<p style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;orphans: 3;widows: 3;margin: 0 0 10px;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">${invoice.expiry?date}</strong></p>
		</div>
	</div>
	<div class="row" style="background: darksalmon;-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;margin-right: -15px;margin-left: -15px;">
		<div class="panel-body" style="width: 100%;-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 15px;">
			<table style="width: 100%;-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;border-spacing: 0;border-collapse: collapse;background-color: transparent;">
				<tr style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;page-break-inside: avoid;">
					<td style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 0;">
						<div style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
							<div style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">Pay Amount : </div>
							<p style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;orphans: 3;widows: 3;margin: 0 0 10px;"><strong style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;font-weight: 700;">INR ${invoice.payAmount}</strong></p>
						</div>
					</td>
					<td class="pull-right" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;padding: 0;float: right!important;">
						<div style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
							<a href="${invoiceUrl}" type="button" class="btn btn-info" style="-webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;background-color: #5bc0de;color: #fff;text-decoration: underline;display: inline-block;padding: 6px 12px;margin-bottom: 0;font-size: 14px;font-weight: 400;line-height: 1.42857143;text-align: center;white-space: nowrap;vertical-align: middle;-ms-touch-action: manipulation;touch-action: manipulation;cursor: pointer;-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;background-image: none;border: 1px solid transparent;border-radius: 4px;border-color: #46b8da;">Proceed to Pay</a>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>
</body>
</html>