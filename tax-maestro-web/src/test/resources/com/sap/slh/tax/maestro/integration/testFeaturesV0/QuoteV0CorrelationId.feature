Feature: Quote Correlation ID
  Description: Correlation ID should be returned by the service.

  @GSSITAAS-4060 @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: <scenario> with Correlation ID
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    And It is an authorized request
    And It has a Correlation ID in the header
    When I send the request
    Then Response should have the same Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header
    And Determine queue should have the same Correlation ID from HTTP response header
    And Calculate queue should have the same Correlation ID from HTTP response header

    Examples: 
      | scenario                                                         |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" |

  @GSSITAAS-4060 @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: <scenario> without Correlation ID
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    And It is an authorized request
    And It does not have a Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header
    And Determine queue should have the same Correlation ID from HTTP response header
    And Calculate queue should have the same Correlation ID from HTTP response header

    Examples: 
      | scenario                                                         |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" |
      
  @GSSITAAS-4060 @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: <scenario> with invalid Correlation ID
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    And It is an authorized request
    And It has an invalid Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header
    And Determine queue should have the same Correlation ID from HTTP response header
    And Calculate queue should have the same Correlation ID from HTTP response header

    Examples: 
      | scenario                                                         |
      | "quoteV0/fullPayload/UK Domestic Sales Zero Rate Prod Exemption" |

  @GSSITAAS-4060 @GSSITAAS-4062 @GSSITAAS-4063
  Scenario Outline: <scenario> - handle correlation ID in a scenario terminated with an error
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It is an authorized request
    And It does not have a Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header

    Examples: 
      | scenario                                                         |
      | "quoteV0/fullPayload/Integration - Destination - Internal Error" |

  @GSSITAAS-4062 @GSSITAAS-4063 @GSSITAAS-4064
  Scenario Outline: <scenario> with Correlation ID (partner)
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    And It is an authorized request
    And It has a Correlation ID in the header
    When I send the request
    Then Response should have the same Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header
    And Partner request should have the same Correlation ID from HTTP response header

    Examples: 
      | scenario                                                                     |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - authentication none" |

  @GSSITAAS-4062 @GSSITAAS-4063 @GSSITAAS-4064
  Scenario Outline: <scenario> without Correlation ID (partner)
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    And It is an authorized request
    And It does not have a Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header
    And Partner request should have the same Correlation ID from HTTP response header
    
  @GSSITAAS-4062 @GSSITAAS-4063 @GSSITAAS-4064
  Scenario Outline: <scenario> with invalid Correlation ID (partner)
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    And It is an authorized request
    And It has an invalid Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header
    And Partner request should have the same Correlation ID from HTTP response header

    Examples: 
      | scenario                                                                     |
      | "quoteV0/fullPayload/BR Sale of Service using Partner - authentication none" |

  @GSSITAAS-4062 @GSSITAAS-4063 @GSSITAAS-4064
  Scenario Outline: <scenario> - handle correlation ID in a scenario terminated with an error (partner)
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    And It has a partner response status as <partnerStatus>
    And It is an authorized request
    And It does not have a Correlation ID in the header
    When I send the request
    Then Response should have a valid Correlation ID in the header
    And Destination queue should have the same Correlation ID from HTTP response header
    And Partner request should have the same Correlation ID from HTTP response header

    Examples: 
      | scenario                                                                 | partnerStatus |
      | "quoteV0/directPayload/CA Purchase using Partner - partner error server" |           500 |
