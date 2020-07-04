function fn() {
	var txsJwt = java.lang.System.getenv('TXS_JWT');
	var txsPrimaryUrl = java.lang.System.getenv('TXS_PRIMARY_URL');
	var txsTaxAttributesDeterminationUrl = java.lang.System.getenv('TXS_TAX_ATTRIBUTES_DETERMINATION_URL');
	var txsTaxMaestroUrl = java.lang.System.getenv('TXS_TAX_MAESTRO_URL');
	var txsTaxContentUrl = java.lang.System.getenv('TXS_TAX_CONTENT_URL');
	var txsTaxCalculationServiceUrl = java.lang.System.getenv('TXS_TAX_CALCULATION_SERVICE_URL');
	var txsTaxDestinationConfigurationUrl = java.lang.System.getenv('TXS_TAX_DESTINATION_CONFIGURATION_URL');

	var config = {
		JWT: txsJwt,
		PRIMARY_URL: txsPrimaryUrl,
		TAX_ATTRIBUTES_DETERMINATION_URL: txsTaxAttributesDeterminationUrl,
		TAX_MAESTRO_URL: txsTaxMaestroUrl,
		TAX_CONTENT_URL: txsTaxContentUrl,
		TAX_CALCULATION_SERVICE_URL: txsTaxCalculationServiceUrl,
		TAX_DESTINATION_CONFIGURATION_URL: txsTaxDestinationConfigurationUrl
	};

	config['request_json'] = function (feature, scenario) {
		return read('classpath:com/sap/slh/tax/maestro/integration/testData/' + feature + '/' + scenario + '/QuoteRequest.json');
	};

	config['response_json'] = function (feature, scenario) {
		return read('classpath:com/sap/slh/tax/maestro/integration/testData/' + feature + '/' + scenario + '/QuoteResponse.json');
	};

	config['request_string'] = function (feature, scenario) {
		return karate.readAsString('classpath:com/sap/slh/tax/maestro/integration/testData/' + feature + '/' + scenario + '/QuoteRequest.json');
	};

	karate.log('config:', JSON.stringify(config));

	return config;
}
