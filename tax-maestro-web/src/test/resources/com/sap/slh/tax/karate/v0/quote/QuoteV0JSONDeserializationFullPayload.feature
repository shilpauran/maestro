Feature: Quote V0 JSON Deserialization Full Payload
  Describe: JSON deserialization errors should be informed to the user so it can correct them.

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
      | scenario                                       | status |
      | "Validation - Domain - Boolean"                | 400    |
      | "Validation - Domain - Date"                   | 400    |
      | "Validation - Domain - SaleOrPurchase"         | 400    |
      | "Validation - Domain - GrossOrNet"             | 400    |
      | "Validation - Domain - Currency"               | 400    |
      | "Validation - Domain - ItemType"               | 400    |
      | "Validation - Domain - Exemption LocationType" | 400    |
      | "Validation - Domain - Locations Type"         | 400    |
      | "Validation - Domain - Locations Country"      | 400    |
      | "Validation - Domain - Number"                 | 400    |

  @GSSITAAS-3491
  Scenario Outline: <scenario>
    Given path '/v0/quote'
    And header Authorization = token
    And header Content-Type = 'application/json'
    And request request_string(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status 400

    Examples:
      | scenario                                    | status |
      | "Validation - Invalid json"                 | 400    |
      | "Validation - Invalid json - empty"         | 400    |
