Feature: Quote V1 Message Properties
  Description: Message Values, such as locale, should be passed to message properties
  
  @GSSITAAS-3584  
  Scenario Outline: Queues message properties should have language as <expectedLanguage>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And Client has language configured as <language>
    And It is an authorized request
    And It has an enhance response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario <scenario>
    And Enhance queue should have received language as <expectedLanguage>
    And Determine queue should have received language as <expectedLanguage>
    And Calculate queue should have received language as <expectedLanguage>

    Examples: 
      | scenario                                                     | language | expectedLanguage |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD" | "fr"     | "fr"             |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD" | "en"     | "en"             |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD" | "pt-BR"  | "pt-BR"          |
      | "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD" | ""       | "en"             |

  @GSSITAAS-3584
  Scenario: Queues message properties should have language set as default
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD"
    And It is an authorized request
    And It has an enhance response as from scenario "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD"
    And It has a determine response as from scenario "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD"
    And It has a calculate response as from scenario "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD"
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario "quoteV1/UK Domestic Sales Zero Rate Prod Exemption with MD"
    And Enhance queue should have received language as "en"
    And Determine queue should have received language as "en"
    And Calculate queue should have received language as "en"
