Feature: Quote V0 JSON Deserialization Full Payload
  Describe: JSON deserialization errors should be informed to the user so it can correct them.

  @GSSITAAS-3491
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                           |
      | "quoteV0/fullPayload/Validation - Invalid json"                    |
      | "quoteV0/fullPayload/Validation - Invalid json - empty"            |
      | "quoteV0/fullPayload/Validation - Domain - Boolean"                |
      | "quoteV0/fullPayload/Validation - Domain - Date"                   |
      | "quoteV0/fullPayload/Validation - Domain - SaleOrPurchase"         |
      | "quoteV0/fullPayload/Validation - Domain - GrossOrNet"             |
      | "quoteV0/fullPayload/Validation - Domain - Currency"               |
      | "quoteV0/fullPayload/Validation - Domain - ItemType"               |
      | "quoteV0/fullPayload/Validation - Domain - Exemption LocationType" |
      | "quoteV0/fullPayload/Validation - Domain - Locations Type"         |
      | "quoteV0/fullPayload/Validation - Domain - Locations Country"      |
      | "quoteV0/fullPayload/Validation - Domain - Number"                 |

  @GSSITAAS-3491
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that server could not process the request
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a determine request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                  |
      | "quoteV0/fullPayload/Validation - Domain - Negative Test" |

  @GSSITAAS-3491
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because payload is too large
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                             |
      | "quoteV0/fullPayload/Validation - Payload too large" |
