Feature: Quote V0 JSON Deserialization Direct Payload
  Describe: JSON deserialization errors should be informed to the user so it can correct them.
  
  @GSSITAAS-3714  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should return response body as from scenario <scenario>

    Examples:
      | scenario                                                           |
      | "quoteV0/directPayload/Validation - Domain - Date"                 |
      | "quoteV0/directPayload/Validation - Domain - GrossOrNet"           |
      | "quoteV0/directPayload/Validation - Domain - Item - Tax Country"   |
      | "quoteV0/directPayload/Validation - Domain - Item - Tax Category"  |
      | "quoteV0/directPayload/Validation - Domain - Item - ItemType"      |
