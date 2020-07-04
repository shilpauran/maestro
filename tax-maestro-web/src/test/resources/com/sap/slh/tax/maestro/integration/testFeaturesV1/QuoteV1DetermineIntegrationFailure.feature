Feature: Quote V1 Determine Queue Integration Failure
  Describe: Quote V1 determine queue integration tests, for failures.

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that server could not process the request
    And It should have sent a determine request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                           |
      | "quoteV1/Integration - Determine - Internal Error" |
      | "quoteV1/Integration - Determine - Request Denied" |
      | "quoteV1/Integration - Determine - Queue timeout"  |

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should have sent a determine request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                     |
      | "quoteV1/Integration - Determine - Invalid Input"            |
      | "quoteV1/Integration - Determine - Invalid Input Product ID" |
      | "quoteV1/Integration - Determine - Invalid Input References" |
      | "quoteV1/Integration - Determine - No Content"               |
      | "quoteV1/Integration - Determine - Partial Content"          |
