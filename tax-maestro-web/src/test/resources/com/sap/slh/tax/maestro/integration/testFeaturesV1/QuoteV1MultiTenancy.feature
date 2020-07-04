Feature: Quote V1 Multi Tenancy
  Description: Tests multitenance aspects in Quote V1
  
  @GSSITAAS-3584  
  Scenario Outline: Should call quote endpoint authenticated using <tenant> as tenant
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has an enhance response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request to tenant <tenant> as from scenario <scenario>
    And It should have sent a determine request to tenant <tenant> as from scenario <scenario>
    And It should have sent a calculate request to tenant <tenant> as from scenario <scenario>
    And It should have sent 1 enhance requests to tenant <tenant>
    And It should have sent 1 determine requests to tenant <tenant>
    And It should have sent 1 calculate requests to tenant <tenant>
    And It should have sent 0 enhance requests to tenant "doNotCallTenant"
    And It should have sent 0 determine requests to tenant "doNotCallTenant"
    And It should have sent 0 calculate requests to tenant "doNotCallTenant"
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                     | tenant    |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD" | "tenant"  |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD" | "another" |
