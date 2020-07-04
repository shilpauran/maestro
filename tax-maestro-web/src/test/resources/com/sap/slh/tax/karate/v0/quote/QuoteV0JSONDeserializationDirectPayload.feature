Feature: Quote V0 JSON Deserialization Direct Payload
  Describe: JSON deserialization errors should be informed to the user so it can correct them.

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
      | scenario                                     | status |
      | "Validation - Domain - Date"                 | 400    |
      | "Validation - Domain - GrossOrNet"           | 400    |
      | "Validation - Domain - Item - Tax Country"   | 400    |
      | "Validation - Domain - Item - Tax Category"  | 400    |
      | "Validation - Domain - Item - ItemType"      | 400    |
