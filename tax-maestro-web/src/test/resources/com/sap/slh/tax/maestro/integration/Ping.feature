Feature: Ping
  Description: The ping endpoint should only respond to authenticated requests.

  @GSSITAAS-3472
  Scenario: Authorized Request
    Given I want to perform a health check
    And It has an empty body
    And It is an authorized request
    When I send the request
    Then Response should be that request succeeded
    And It should return json body "{\"status\": \"UP\"}"

  @GSSITAAS-3472
  Scenario: Unauthorized Request
    Given I want to perform a health check
    And It has an empty body
    And It is an unauthorized request
    When I send the request
    Then Response should be that request is unauthenticated

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario: Ping with Correlation ID
    Given I want to perform a health check
    And It has an empty body
    And It is an authorized request
    And It has a Correlation ID in the header
    When I send the request
    Then Response should have the same Correlation ID in the header

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario: Ping with Correlation ID too large
    Given I want to perform a health check
    And It has an empty body
    And It is an authorized request
    And It has a Correlation ID too large in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario: Ping without Correlation ID
    Given I want to perform a health check
    And It has an empty body
    And It is an authorized request
    And It does not have a Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
    
  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario: Ping with invalid Correlation ID
    Given I want to perform a health check
    And It has an empty body
    And It is an authorized request
    And It has an invalid Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
