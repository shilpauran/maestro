Feature: Ping
  Description: The ping endpoint should only respond to authenticated requests.

  Background:
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT

  @GSSITAAS-3472
  Scenario: Authorized Request
    Given path '/ping'
    And header Authorization = token
    When method GET
    Then status 200
    And match response == { status: 'UP' }

  @GSSITAAS-3472
  Scenario: Unauthorized Request
    Given path '/ping'
    When method GET
    Then status 401
    And match response == ''
