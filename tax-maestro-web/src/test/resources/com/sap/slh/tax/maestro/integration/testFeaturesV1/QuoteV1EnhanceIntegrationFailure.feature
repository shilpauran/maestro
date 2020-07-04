Feature: Quote V1 Enhance Queues Integration Failure
  Describe: Quote V1 enhance queues integration tests, for failures.

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It has an enhance response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that server could not process the request
    And It should have sent an enhance request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                           |
      | "quoteV1/Integration - Enhance - Internal Error"   |
      | "quoteV1/Integration - Enhance - Queue timeout"    |

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It has an enhance response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should have sent an enhance request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                     |
      | "quoteV1/Integration - Enhance - Not Found"                  |