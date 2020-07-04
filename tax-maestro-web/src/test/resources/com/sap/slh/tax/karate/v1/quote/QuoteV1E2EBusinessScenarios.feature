Feature: Quote V1 End 2 End Business Scenarios
  Describe: Integration business scenarios of tax quote.

  Background:
    * string feature = 'quoteV1'
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT

  @GSSITAAS-3606 @GSSITAAS-3605
  Scenario Outline: <scenario>
    Given path '/v1/quote'
    And header Authorization = token
    And request request_json(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples:
      | scenario                                                            | status |
      #CA
      | "E2E - Domestic Purchase in CA (AB-BC) - GST-PST"                   | 200    |
      | "E2E - Domestic Purchase in CA (AB-MB) - GST-PST"                   | 200    |
      | "E2E - Domestic Purchase in CA (AB-NS) - HST"                       | 200    |
      | "E2E - Domestic Purchase in CA (AB-QC) - GST-QST"                   | 200    |
      | "E2E - Domestic Purchase in CA (QC-AB) - GST"                       | 200    |
      | "E2E - Domestic Purchase in CA (AB-ON) - HST"                       | 200    |
      | "E2E - Domestic Sale in CA (AB-MB) - GST-PST"                       | 200    |
      | "E2E - Domestic Sale in CA (AB-MB) - GST-PST - PST Exempted"        | 200    |
      | "E2E - Domestic Sale in CA (AB-ON) - HST"                           | 200    |
      | "E2E - Domestic Sale in CA (AB-SK) - GST (BP Exemp)"                | 200    |
      | "E2E - Domestic Sale in CA (BC-NT) - GST"                           | 200    |
      | "E2E - Export Sales in CA (AB-ITALY) - HST"                         | 200    |
      #GB
      | "E2E - Domestic Purchase in GB - VAT Full Rate"                     | 200    |
      | "E2E - Domestic Sale in GB - VAT Full Rate"                         | 200    |
      | "E2E - Domestic Sale in GB - VAT Zero Rate (BP Exemp)"              | 200    |
      | "E2E - Domestic Sales of Service in GB - VAT Reduced Rate"          | 200    |
      #NL
      | "E2E - Domestic Purchase of Goods in NL - Standard Rate"            | 200    |
      | "E2E - EU Sale of Goods (NL-BE) - B2B"                              | 200    |
      | "E2E - Export of Goods (NL-CA)"                                     | 200    |
      | "E2E - Export of Service (NL-CA) - B2B"                             | 200    |
      #BE
      | "E2E - Domestic Purchase of Service in BE - Zero Rate"              | 200    |
      | "E2E - Domestic Sale of Goods in BE - Customer Exempt"              | 200    |
      | "E2E - Domestic Sale of Service in BE - Prod Exempt"                | 200    |
      | "E2E - EU Sale of Goods (BE-DE) - B2C (Gross Amount)"               | 200    |
      | "E2E - EU Distance Selling (NL-BE) - B2C"                           | 200    |
      | "E2E - EU MOSS (DE-BE) - B2C"                                       | 200    |
      #DE
      | "E2E - Domestic Sale of Goods in DE - Reduced Rate (Gross Amount)"  | 200    |
      | "E2E - EU MOSS (NL-DE) - B2C"                                       | 200    |
      #technical
      | "Integration - Enhance - Not Found"                                 | 400    |
      | "Integration - Determine - Invalid Input Product ID"                | 400    |