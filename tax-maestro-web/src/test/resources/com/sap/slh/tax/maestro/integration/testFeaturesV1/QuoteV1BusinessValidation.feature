Feature: Quote V1 Business Validation
  Describe: Business validation errors should return a meaningful error message.
  
  @GSSITAAS-3584  
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v1" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should return response body as from scenario <scenario>

    Examples: 
      | scenario                                                 |
      | "quoteV1/Validation - Mandatory - Basic 1"               |
      | "quoteV1/Validation - Mandatory - Tables"                |
      | "quoteV1/Validation - Mandatory - Item Basic 1"          |
      | "quoteV1/Validation - Mandatory - Item Basic 2"          |
      | "quoteV1/Validation - Mandatory - Item Assig Parties 1"  |
      | "quoteV1/Validation - Mandatory - Item Assig Parties 2"  |
      | "quoteV1/Validation - Mandatory - Item Assig Parties 3"  |
      | "quoteV1/Validation - Mandatory - Item Cost"             |
      | "quoteV1/Validation - Mandatory - Product Basic"         |
      | "quoteV1/Validation - Mandatory - Product TaxClassif"    |
      | "quoteV1/Validation - Mandatory - Product StdClassif"    |
      | "quoteV1/Validation - Mandatory - Party Basic 1"         |
      | "quoteV1/Validation - Mandatory - Party Classif Regist"  |
      | "quoteV1/Validation - Mutual Exclusion - Basic 1"        |
      | "quoteV1/Validation - Reference Check - Basic 1"         |
      | "quoteV1/Validation - Mandatory - Transaction Type Code" |
