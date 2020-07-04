Feature: Quote V0 End 2 End Business Scenarios Correlation ID
  Describe: Integration business scenarios of tax quote.

  Background:
    * string featureDirectPayload = 'quoteV0/directPayload'
    * string featureFullPayload = 'quoteV0/fullPayload'
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT
    * string correlationIdPattern = '#regex ([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})'
    * configure lowerCaseResponseHeaders = true

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: <scenario>
    Given path '/v0/quote'
    And header Authorization = token
    And header X-Correlationid = '3721e7c7-bbe5-4c25-630a-52fd00349676'
    And request request_json(featureDirectPayload, <scenario>)
    When method POST
    Then match response == response_json(featureDirectPayload, <scenario>)
    And match header x-correlationid == '3721e7c7-bbe5-4c25-630a-52fd00349676'
    And status <status>

    Examples:
      | scenario                        | status |
      | "E2E - Partner not found in JM" | 404    |

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: <scenario>
    Given path '/v0/quote'
    And header Authorization = token
    And request request_json(featureFullPayload, <scenario>)
    When method POST
    Then match response == response_json(featureFullPayload, <scenario>)
    And match header x-correlationid == correlationIdPattern
    And status <status>

    Examples:
      | scenario                                          | status |
      | "E2E - Domestic Purchase in CA (AB-BC) - GST-PST" | 200    |