Feature: Correlation ID
  Description: Correlation ID header should be returned exactly as it was passed.
  If it was not passed, it should be generated.

  Background:
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT
    * string correlationIdPattern = '#regex ^[0-9a-zA-Z\\-]{1,72}+$'
    * configure lowerCaseResponseHeaders = true

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: Ping with Correlation ID
    Given path "/ping"
    And header Authorization = token
    And header X-CorrelationID = <sentCorrelationID>
    When method GET
    Then match header x-correlationid == <expectedCorrelationID>

    Examples:
      | sentCorrelationID                                                                        | expectedCorrelationID         |
      | "dummystringforcorrelationid"                                                            | "dummystringforcorrelationid" |
      | "b03d17e7-480d-4f3b-9f3c-5307a0d27321-480059bf-2b24-4ba6-ba24-b76777da43f8-5307a0d27321" | correlationIdPattern          |
      | "<...>"                                                                                  | correlationIdPattern          |

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario: Ping without Correlation ID
    Given path "/ping"
    And header Authorization = token
    When method GET
    Then match header x-correlationid == correlationIdPattern

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: Correlation ID provided for <path>
    Given path <path>
    And header Authorization = token
    And header Content-Type = 'application/json'
    And header X-CorrelationID = <sentCorrelationID>
    And request ''
    When method POST
    Then match header x-correlationid == <expectedCorrelationID>

    Examples:
      | path        | sentCorrelationID                                                                        | expectedCorrelationID                  |
      | "/v0/quote" | "3721e7c7-bbe5-4c25-630a-52fd00349676"                                                   | "3721e7c7-bbe5-4c25-630a-52fd00349676" |
      | "/v0/quote" | "b03d17e7-480d-4f3b-9f3c-5307a0d27321-480059bf-2b24-4ba6-ba24-b76777da43f8-5307a0d27321" | correlationIdPattern                   |
      | "/v0/quote" | "<..>"                                                                                   | correlationIdPattern                   |
      | "/v1/quote" | "3721e7c7-bbe5-4c25-630a-52fd00349676"                                                   | "3721e7c7-bbe5-4c25-630a-52fd00349676" |
      | "/v1/quote" | "b03d17e7-480d-4f3b-9f3c-5307a0d27321-480059bf-2b24-4ba6-ba24-b76777da43f8-5307a0d27321" | correlationIdPattern                   |
      | "/v1/quote" | "<..>"                                                                                   | correlationIdPattern                   |

  @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: Correlation ID not provided for <path>
    Given path <path>
    And header Authorization = token
    And header Content-Type = 'application/json'
    And request ''
    When method POST
    Then match header x-correlationid == correlationIdPattern

    Examples:
      | path        |
      | "/v0/quote" |
      | "/v1/quote" |
