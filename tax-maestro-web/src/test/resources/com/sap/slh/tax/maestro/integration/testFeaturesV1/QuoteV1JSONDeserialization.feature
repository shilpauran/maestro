Feature: Quote V1 JSON Deserialization
  Describe: JSON deserialization errors should be informed to the user so it can correct them.

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                            |
      | "quoteV1/Validation - Invalid json"                 |
      | "quoteV1/Validation - Invalid json - empty"         |
      | "quoteV1/Validation - Domain - Boolean 1"           |
      | "quoteV1/Validation - Domain - Boolean 2"           |
      | "quoteV1/Validation - Domain - transactionTypeCode" |
      | "quoteV1/Validation - Domain - amountTypeCode"      |
      | "quoteV1/Validation - Domain - currencyCode"        |
      | "quoteV1/Validation - Domain - Number 1"            |
      | "quoteV1/Validation - Domain - Number 2"            |
      | "quoteV1/Validation - Domain - products.typeCode"   |
      | "quoteV1/Validation - Domain - role"                |
      | "quoteV1/Validation - Domain - countryRegionCode"   |
      | "quoteV1/Validation - Domain - Many at same time"   |

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because payload is too large
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                             |
      | "quoteV1/Validation - Payload too large" |
