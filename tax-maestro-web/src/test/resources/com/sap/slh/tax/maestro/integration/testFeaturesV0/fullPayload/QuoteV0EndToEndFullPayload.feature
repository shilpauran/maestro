Feature: Quote V0 End to End Full Payload
  Describe: Quote scenarios of version 0 validated on an end-to-end process
  
  @GSSITAAS-3491  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has an enhance response as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario <scenario>
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a determine request as from scenario <scenario>
    And It should have sent a calculate request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                          |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" |
