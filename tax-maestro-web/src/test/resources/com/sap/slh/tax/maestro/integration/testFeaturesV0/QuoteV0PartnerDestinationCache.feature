Feature: Quote V0 Partner Destination cache
  Describe: Quote scenarios of version 0 validated Destination cache

  # Scenario - Cache starts empty
  # TDC returns a response with error
  # The cache is not updated in this case
  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has a destination response as from scenario <scenario>
    When I send the request
    Then Response should be that request failed because it is invalid
    And It should have sent a destination request as from scenario <scenario>
    And It should have destination cache content as from scenario <scenario>
    And It should return response body as from scenario <scenario>
    And Destination cache should have 0 entries

    Examples:
      | scenario                                                          | tenant    |
      | "quoteV0/fullPayload/Integration - Destination - Invalid Content" | "tenant1" |

  # Scenario - No destination is returned from TDC for GB
  # The cache is updated with default 'tax-service' destination for tenant1
  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version keeping the cache from previous scenarios
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has an enhance response as from scenario <scenario>
    And It has a destination response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario <scenario>
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a determine request as from scenario <scenario>
    And It should have sent a calculate request as from scenario <scenario>
    And It should have destination cache content as from scenario <scenario>
    And It should return response body as from scenario <scenario>
    And Destination cache should have 1 entries

    Examples:
      | scenario                                                                              | tenant    |
      | "quoteV0/fullPayload/Cache - UK Domestic Sales Zero Rate Prod Exemption not in cache" | "tenant1" |

  # Scenario - Partner destination is returned from TDC for BR for two tenants
  # Partner destination is put twice in the cache for both tenant1 and tenant2
  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version keeping the cache from previous scenarios
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a partner request as from scenario <scenario>
    And It should have destination cache content as from scenario <scenario>
    And It should return response body as from scenario <scenario>
    And Destination cache should have <cacheSize> entries

    Examples:
      | scenario                                                       | tenant    | cacheSize |
      | "quoteV0/fullPayload/Cache - BR Sale of Service using Partner" | "tenant1" |         2 |
      | "quoteV0/fullPayload/Cache - BR Sale of Service using Partner" | "tenant2" |         3 |

  # Scenario - Default tax-service destination is gotten for the cache for GB
  # Destination cache is not updated in this case
  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version keeping the cache from previous scenarios
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has an enhance response as from scenario <scenario>
    And It has a determine response as from scenario <scenario>
    And It has a calculate response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent an enhance request as from scenario <scenario>
    And It should have sent a determine request as from scenario <scenario>
    And It should have sent a calculate request as from scenario <scenario>
    And It should have destination cache content as from scenario <scenario>
    And It should return response body as from scenario <scenario>
    And Destination cache should have 3 entries

    Examples:
      | scenario                                                                          | tenant    |
      | "quoteV0/fullPayload/Cache - UK Domestic Sales Zero Rate Prod Exemption in cache" | "tenant1" |

  # First scenario - Partner destination is returned from TDC for GB for tenant2
  # Partner destination is put into the cache for tenant2
  # Second scenario - Use no-cache property to bypass the cache for tenant1
  # Partner destination is returned from TDC for GB
  # Destination cache is updated with partner destination
  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version keeping the cache from previous scenarios
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has CacheControl HTTP header set as <cacheControl>
    And It has a destination response as from scenario <scenario>
    And It has a partner response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent a destination request as from scenario <scenario>
    And It should have sent a partner request as from scenario <scenario>
    And It should have destination cache content as from scenario <scenario>
    And It should return response body as from scenario <scenario>
    And Destination cache should have 4 entries

    Examples:
      | scenario                                                                                            | tenant    | cacheControl |
      | "quoteV0/fullPayload/Cache - UK Domestic Sales Zero Rate Prod Exemption using Partner not in cache" | "tenant2" | ""           |
      | "quoteV0/fullPayload/Cache - UK Domestic Sales Zero Rate Prod Exemption using Partner not in cache" | "tenant1" | "no-cache"   |

  # Scenario - Partner destination is already cached for GB and tenant1
  # TDC is not called in this case, as the information comes from the cache
  @GSSITAAS-3537
  Scenario Outline: <scenario>
    Given I want to perform a tax quote using "v0" version keeping the cache from previous scenarios
    And It has a request body as from scenario <scenario>
    And It is an authorized request for the tenant <tenant>
    And It has a partner response as from scenario <scenario>
    When I send the request
    Then Response should be that request succeeded
    And It should have sent a partner request as from scenario <scenario>
    And It should have destination cache content as from scenario <scenario>
    And It should return response body as from scenario <scenario>
    And Destination cache should have 4 entries

    Examples:
      | scenario                                                                                        | tenant    |
      | "quoteV0/fullPayload/Cache - UK Domestic Sales Zero Rate Prod Exemption using Partner in cache" | "tenant1" |
