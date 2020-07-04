Feature: Quote V0 Destination Queues Integration Failure
  Describe: Quote V0 destination queues integration tests, for failures.
  
  @GSSITAAS-3537  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that server could not process the request
    And It should have sent a destination request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                          |
      | "quoteV0/fullPayload/Integration - Destination - Internal Error"                  |
      | "quoteV0/fullPayload/Integration - Destination - Queue timeout"                   |
  
  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should have sent a destination request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                          |
      | "quoteV0/fullPayload/Integration - Destination - Invalid Content"                 |
      