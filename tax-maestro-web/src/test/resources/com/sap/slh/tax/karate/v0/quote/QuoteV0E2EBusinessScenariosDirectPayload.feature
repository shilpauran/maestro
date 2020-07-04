Feature: Quote V0 End 2 End Business Scenarios Direct Payload
  Describe: Integration business scenarios of tax quote.

  Background: 
    * string feature = 'quoteV0/directPayload'
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT

  @GSSITAAS-3714
  Scenario Outline: <scenario>
    Given path '/v0/quote'
    And header Authorization = token
    And request request_json(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples: 
      | scenario                        | status |
      | "E2E - Partner not found in JM" |    404 |

  @GSSITAAS-3872
  Scenario Outline: <scenario>
    Given path '/v0/quote'
    And header Authorization = token
    And request request_json(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples: 
      | scenario                            | status |
      | "E2E - Whitelist validation for DO" |    400 |
