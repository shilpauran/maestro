Feature: Quote V0 Business Validation Full Payload
  Describe: Business validation errors should return a meaningful error message.

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
      | scenario                                                     | status |
      | "Validation - Mandatory - Combined 1 - Header"               | 400    |
      | "Validation - Mandatory - Combined 2 - Item"                 | 400    |
      | "Validation - Mandatory - Combined 3 - Item properties"      | 400    |
      | "Validation - Mandatory - Combined 4 - Locations"            | 400    |
      | "Validation - Mandatory - Combined 5 - Locations properties" | 400    |
      | "Validation - Mandatory - Combined 6 - Locations type"       | 400    |
