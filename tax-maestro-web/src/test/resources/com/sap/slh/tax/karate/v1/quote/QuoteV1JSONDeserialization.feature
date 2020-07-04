Feature: Quote V1 JSON Deserialization
  Describe: JSON deserialization errors should be informed to the user so it can correct them.

  Background:
    * string feature = 'quoteV1'
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given path '/v1/quote'
    And header Authorization = token
    And request request_json(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples:
      | scenario                                    | status |
      | "Validation - Domain - Boolean 1"           | 400    |
      | "Validation - Domain - Boolean 2"           | 400    |
      | "Validation - Domain - transactionTypeCode" | 400    |
      | "Validation - Domain - amountTypeCode"      | 400    |
      | "Validation - Domain - currencyCode"        | 400    |
      | "Validation - Domain - Number 1"            | 400    |
      | "Validation - Domain - Number 2"            | 400    |
      | "Validation - Domain - products.typeCode"   | 400    |
      | "Validation - Domain - role"                | 400    |
      | "Validation - Domain - countryRegionCode"   | 400    |
      | "Validation - Domain - Many at same time"   | 400    |

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given path '/v1/quote'
    And header Authorization = token
    And header Content-Type = 'application/json'
    And request request_string(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples:
      | scenario                                    | status |
      | "Validation - Invalid json"                 | 400    |
      | "Validation - Invalid json - empty"         | 400    |
