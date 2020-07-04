Feature: Quote V0 Authentication
  Description: Proper authentication should be required to make requests to the service.
  
  @GSSITAAS-3491  
  Scenario: Unauthorized Request
    Given I want to perform a tax quote using "v0" version
    And It has body as "{\"id\":\"1\"}"
    And It is an unauthorized request
    When I send the request
    Then Response should be that request is unauthenticated

  @GSSITAAS-3491
  Scenario: Authorized Request
    Given I want to perform a tax quote using "v0" version
    And It has body as "{\"id\":\"1\"}"
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
