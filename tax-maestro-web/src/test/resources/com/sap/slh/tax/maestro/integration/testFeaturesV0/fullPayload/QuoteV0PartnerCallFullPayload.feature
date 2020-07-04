Feature: Quote V0 Partner Call Full Payload
  Describe: Quote scenarios of version 0 validated on an end-to-end process using HTTP Calls
  
  @GSSITAAS-3537  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request succeeded
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a partner request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                                               |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner name from response"    |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - partner name from destination" |
