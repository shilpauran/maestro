package com.sap.slh.tax.maestro.tax.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;
import com.sap.slh.tax.destinationconfiguration.destinations.models.CountryRegionCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class DestinationCacheKeyTest {

    private static final String TENANT_ID = "tenant";
    private DestinationRequest destinationRequest = DestinationRequest.builder()
            .withCountryRegionCode(CountryRegionCode.CA).build();

    @Test
    public void nullFieldsBuilder() {
        DestinationCacheKey destinationRequest = DestinationCacheKey.builder().build();
        assertNull(destinationRequest.getTenantId());
        assertNull(destinationRequest.getDestinationRequest());
    }

    @Test
    public void initializeFields() {
        DestinationCacheKey destinationCahceKey = this.getDefaultDestinationCacheKey();
        assertEquals(destinationCahceKey.getTenantId(), TENANT_ID);
        assertEquals(destinationCahceKey.getDestinationRequest(), destinationRequest);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(DestinationCacheKey.class).suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }

    private DestinationCacheKey getDefaultDestinationCacheKey() {
        return DestinationCacheKey.builder().withTenantId(TENANT_ID).withDestinationRequest(destinationRequest).build();
    }

}
