Feature: Quote Latest Authentication
  Description: Proper authentication should be required to make requests to the service.
  
  @GSSITAAS-3584  
  Scenario: Unauthorized Request
    Given I want to perform a tax quote using "latest" version
    And It has body as "{\"id\":\"1\"}"
    And It is an unauthorized request
    When I send the request
    Then Response should be that request is unauthenticated

  Scenario: Authorized Request
    Given I want to perform a tax quote using "latest" version
    And It has body as "{\"id\":\"1\"}"
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
