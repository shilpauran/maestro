Feature: Quote V0 End 2 End Business Scenarios Full Payload
  Describe: Integration business scenarios of tax quote.

  Background:
    * string feature = 'quoteV0/fullPayload'
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT

  @GSSITAAS-3491
  Scenario Outline: <scenario>
    Given path '/v0/quote'
    And header Authorization = token
    And request request_json(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples:
      | scenario                                                    | status |
      #CA
      | "E2E - Domestic Purchase in CA (AB-BC) - GST-PST"           | 200    |
      | "E2E - Domestic Purchase in CA (AB-NS) - HST"               | 200    |
      | "E2E - Domestic Purchase in CA (AB-QC) - GST-QST"           | 200    |
      | "E2E - Domestic Sale in CA (AB-MB) - GST-PST"               | 200    |
      | "E2E - Domestic Sale in CA (AB-ON) - HST"                   | 200    |
      | "E2E - Domestic Sale in CA (AB-SK) - GST (BP Exemp)"        | 200    |
      | "E2E - Domestic Sale in CA (BC-NT) - GST"                   | 200    |
      #GB
      | "E2E - Domestic Purchase in GB - VAT Full Rate"             | 200    |
      | "E2E - Domestic Sale in GB - VAT Full Rate"                 | 200    |
      | "E2E - Domestic Sale in GB - VAT Zero Rate (BP Exemp)"      | 200    |
      | "E2E - Domestic Sale in GB - VAT Zero Rate (Product Exemp)" | 200    |