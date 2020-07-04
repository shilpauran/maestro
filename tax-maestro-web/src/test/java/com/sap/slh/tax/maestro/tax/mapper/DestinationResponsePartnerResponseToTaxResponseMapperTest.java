package com.sap.slh.tax.maestro.tax.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;

public class DestinationResponsePartnerResponseToTaxResponseMapperTest {

    private static final String PARTNER_NAME = "TAXWEB";
    private static final String DESTINATION_NAME = "TAXWEB-BR";
    private DestinationResponsePartnerResponseToTaxResponseMapper mapper = DestinationResponsePartnerResponseToTaxResponseMapper
            .getInstance();

    @Test
    public void emptyMap() {
        TaxResponse partnerResponse = TaxResponse.builder().build();
        DestinationResponse destinationResponse = DestinationResponse.builder().build();

        TaxResponse response = mapper.apply(destinationResponse, partnerResponse);

        assertEquals(partnerResponse, response);
    }

    @Test
    public void partnerNameFromPartnerResponse() {
        TaxResponse partnerResponse = TaxResponse.builder().withPartnerName(PARTNER_NAME).build();
        DestinationResponse destinationResponse = DestinationResponse.builder().withName(DESTINATION_NAME).build();

        TaxResponse response = mapper.apply(destinationResponse, partnerResponse);

        assertEquals(partnerResponse, response);
    }

    @Test
    public void partnerNameFromDestinationResponse() {
        DestinationResponse destinationResponse = DestinationResponse.builder().withName(DESTINATION_NAME).build();

        TaxResponse response = mapper.apply(destinationResponse, TaxResponse.builder().build());

        TaxResponse expectedResponse = TaxResponse.builder().withPartnerName(DESTINATION_NAME).build();

        assertEquals(expectedResponse, response);
    }

}
