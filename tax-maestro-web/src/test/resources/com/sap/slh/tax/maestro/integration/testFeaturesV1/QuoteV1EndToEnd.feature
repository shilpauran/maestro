Feature: Quote V1 End to End
  Describe: Quote scenarios of version 1 validated on an end-to-end process
  
  @GSSITAAS-3584  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It has an enhance response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario <scenario>
    And It should have sent a determine request as from scenario <scenario>
    And It should have sent a calculate request as from scenario <scenario>
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                     |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption"         |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD" |
