Feature: Quote V1 Business Validation
  Describe: Business validation errors should return a meaningful error message.

  Background:
    * string feature = 'quoteV1'
    * url TAX_MAESTRO_URL
    * string token = 'Bearer ' + JWT

  @GSSITAAS-3584
  Scenario Outline: <scenario>
    Given path '/v1/quote'
    And header Authorization = token
    And request request_json(feature, <scenario>)
    When method POST
    Then match response == response_json(feature, <scenario>)
    And status <status>

    Examples:
      | scenario                                         | status |
      | "Validation - Mandatory - Basic 1"               | 400    |
      | "Validation - Mandatory - Tables"                | 400    |
      | "Validation - Mandatory - Item Basic 1"          | 400    |
      | "Validation - Mandatory - Item Basic 2"          | 400    |
      | "Validation - Mandatory - Item Assig Parties 1"  | 400    |
      | "Validation - Mandatory - Item Assig Parties 2"  | 400    |
      | "Validation - Mandatory - Item Assig Parties 3"  | 400    |
      | "Validation - Mandatory - Item Cost"             | 400    |
      | "Validation - Mandatory - Product Basic"         | 400    |
      | "Validation - Mandatory - Product TaxClassif"    | 400    |
      | "Validation - Mandatory - Product StdClassif"    | 400    |
      | "Validation - Mandatory - Party Basic 1"         | 400    |
      | "Validation - Mandatory - Party Classif Regist"  | 400    |
      | "Validation - Mutual Exclusion - Basic 1"        | 400    |
      | "Validation - Reference Check - Basic 1"         | 400    |
      | "Validation - Mandatory - Transaction Type Code" | 400    |
