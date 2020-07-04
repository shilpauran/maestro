Feature: Quote V0 Internal Engine Call Direct Payload
  Describe: Validates a direct payload call to the internal engine.
  
  @GSSITAAS-3714  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                      |
      | "quoteV0/directPayload/Validation - CA Purchase using Tax Service engine"     |