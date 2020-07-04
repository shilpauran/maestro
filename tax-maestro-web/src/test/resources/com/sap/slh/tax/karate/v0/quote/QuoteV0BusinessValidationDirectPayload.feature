Feature: Quote V0 Business Validation Direct Payload
  Describe: Business validation errors should return a meaningful error message.

  Background:
    * string feature = 'quoteV0/directPayload'
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT

  @GSSITAAS-3714
  Scenario Outline: <scenario>
    Given path '/v0/quote'
    And header Authorization = token
    And request request_json(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples:
      | scenario                                                | status |
      | "Validation - Mandatory - Header"                       | 400    |
      | "Validation - Mandatory - Item - Id"                    | 400    |
      | "Validation - Mandatory - Item - Properties"            | 400    |
      | "Validation - Mandatory - Item - Different Tax Country" | 400    |
      | "Validation - Mandatory - Item - Missing Tax Country"   | 400    |
