Feature: Quote V0 Multi Tenancy
  Description: Tests multitenance aspects in Quote V0
  
  @GSSITAAS-3491  
  Scenario Outline: Should call quote endpoint authenticated using <tenant> as tenant
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent a destination request to tenant <tenant> as from scenario <scenario>
    And It should have sent a determine request to tenant <tenant> as from scenario <scenario>
    And It should have sent a calculate request to tenant <tenant> as from scenario <scenario>
    And It should have sent 1 destination requests to tenant <tenant>
    And It should have sent 1 determine requests to tenant <tenant>
    And It should have sent 1 calculate requests to tenant <tenant>
    And It should have sent 0 destination requests to tenant "doNotCallTenant"
    And It should have sent 0 determine requests to tenant "doNotCallTenant"
    And It should have sent 0 calculate requests to tenant "doNotCallTenant"
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                         | tenant    |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" | "tenant"  |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" | "another" |
