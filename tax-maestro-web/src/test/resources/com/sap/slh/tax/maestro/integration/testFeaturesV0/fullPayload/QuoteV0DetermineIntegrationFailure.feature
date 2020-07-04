Feature: Quote V0 Determine Queue Integration Failure
  Describe: Quote V0 determine queue integration tests, for failures.
  
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
      | scenario                                                                     |
      | "quoteV0/fullPayload/Integration - Determine - Internal Error"               |
      | "quoteV0/fullPayload/Integration - Determine - Request Denied"               |
      | "quoteV0/fullPayload/Integration - Determine - Invalid Input False Positive" |
      | "quoteV0/fullPayload/Integration - Determine - Queue timeout"                |

  @GSSITAAS-3491
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a determine request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                         |
      | "quoteV0/fullPayload/Integration - Determine - Invalid Input"                    |
      | "quoteV0/fullPayload/Integration - Determine - Invalid Input Mix"                |
      | "quoteV0/fullPayload/Integration - Determine - No Content"                       |
      | "quoteV0/fullPayload/Integration - Determine - Partial Content"                  |
