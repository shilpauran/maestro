Feature: Quote V0 Message Properties
  Description: Message Values, such as locale, should be passed to message properties
  
  @GSSITAAS-3491  
  Scenario Outline: Queues message properties should have language as <expectedLanguage>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And Client has language configured as <language>
    And It is an authorized request
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario <scenario>
    And Destination queue should have received language as <expectedLanguage>
    And Determine queue should have received language as <expectedLanguage>
    And Calculate queue should have received language as <expectedLanguage>

    Examples: 
      | scenario                                                         | language | expectedLanguage |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" | "fr"     | "fr"             |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" | "en"     | "en"             |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" | "pt-BR"  | "pt-BR"          |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" | ""       | "en"             |

  @GSSITAAS-3491
  Scenario: Queues message properties should have language set as default
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption"
    And It is an authorized request
    And It has a destination response as from scenario "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption"
    And It has an enhance response as from scenario "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption"
    And It has a determine response as from scenario "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption"
    And It has a calculate response as from scenario "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption"
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption"
    And Destination queue should have received language as "en"
    And Determine queue should have received language as "en"
    And Calculate queue should have received language as "en"
