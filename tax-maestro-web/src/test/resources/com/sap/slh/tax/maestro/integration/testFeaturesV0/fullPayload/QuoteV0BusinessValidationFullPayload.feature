Feature: Quote V0 Business Validation Full Payload
  Describe: Business validation errors should return a meaningful error message.
  
  @GSSITAAS-3491  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                         |
      | "quoteV0/fullPayload/Validation - Mandatory - Combined 1 - Header"               |
      | "quoteV0/fullPayload/Validation - Mandatory - Combined 2 - Item"                 |
      | "quoteV0/fullPayload/Validation - Mandatory - Combined 3 - Item properties"      |
      | "quoteV0/fullPayload/Validation - Mandatory - Combined 4 - Locations"            |
      | "quoteV0/fullPayload/Validation - Mandatory - Combined 5 - Locations properties" |
      | "quoteV0/fullPayload/Validation - Mandatory - Combined 6 - Locations type"       |
