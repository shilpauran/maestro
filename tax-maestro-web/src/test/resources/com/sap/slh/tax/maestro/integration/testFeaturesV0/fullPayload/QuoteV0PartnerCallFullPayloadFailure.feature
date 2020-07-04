Feature: Quote V0 Partner Call Full Payload Failure
  Describe: Quote scenarios of version 0 validated on an end-to-end process using HTTP Calls

  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    And It has a partner response status as <partnerStatus>
    And It is an authorized request
    When I send the request
    Then Response should be with status <status>
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a partner request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                             | partnerStatus | status |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner error formatted"     |           400 |    400 |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner error not formatted" |           400 |    500 |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner error not found"     |           404 |    404 |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner error unauthorized"  |           401 |    401 |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner error server"        |           500 |    500 |

  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a partner response too large
    And It has a partner response status as <partnerStatus>
    And It is an authorized request
    When I send the request
    Then Response should be with status <status>
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a partner request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                            | partnerStatus | status |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner response too large" |           200 |    413 |
