Feature: Quote V0 HTTP Methods
  Description: The service should only support POST method, and return an error when trying to use others.

  @GSSITAAS-3491  
  Scenario Outline: <method> to <endpoint> should not succeed
    Given I have a <method> method to the endpoint <endpoint>
    And It has body as "{\"id\":\"1\"}"
    And It is an authorized request
    When I send the request
    Then Response should be that resource was not found

    Examples: 
      | method | endpoint            |
      | "POST" | "/tax/v0/error"     |

  @GSSITAAS-3491
  Scenario Outline: <method> to <endpoint> should not succeed
    Given I have a <method> method to the endpoint <endpoint>
    And It has body as "{\"id\":\"1\"}"
    And It is an authorized request
    When I send the request
    Then Response should be that method is not allowed

    Examples: 
      | method   | endpoint            |
      | "GET"    | "/tax/v0/quote"     |
      | "PUT"    | "/tax/v0/quote"     |
      | "DELETE" | "/tax/v0/quote"     |
