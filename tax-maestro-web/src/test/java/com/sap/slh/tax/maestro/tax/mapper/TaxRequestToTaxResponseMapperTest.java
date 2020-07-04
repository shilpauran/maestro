package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaxRequestToTaxResponseMapperTest {

    TaxRequestToTaxResponseMapper mapper = TaxRequestToTaxResponseMapper.getInstance();

    @Test
    public void emptyMapping() {
        TaxRequest request = TaxRequest.builder().build();

        TaxResponse response = mapper.apply(request);
        this.verifyEquals(request, response);
    }

    @Test
    public void basicMapping() {
        TaxRequest request = TaxRequest.builder()
                .withDate(new Date())
                .withGrossOrNet(GrossOrNet.g)
                .withItems(Item.builder().withId("1").build())
                .build();

        TaxResponse response = mapper.apply(request);
        this.verifyEquals(request, response);
    }

    @Test
    public void multipleItemsMapping() {
        TaxRequest request = TaxRequest.builder()
                .withDate(new Date())
                .withGrossOrNet(GrossOrNet.n)
                .withItems(Item.builder().withId("1").build(), Item.builder().withId("2").build(),
                        Item.builder().withId("3").build())
                .build();

        TaxResponse response = mapper.apply(request);
        this.verifyEquals(request, response);
    }

    private void verifyEquals(TaxRequest request, TaxResponse response) {
        assertEquals(request.getDate(), response.getDate());

        if (request.getGrossOrNet().isGross()) {
            assertEquals("true", response.getInclusive());
        } else {
            assertEquals("false", response.getInclusive());
        }

        if (request.getItems() == null) {
            assertNull(response.getTaxLines());
        } else {
            assertEquals(request.getItems().size(), response.getTaxLines().size());

            for (int i = 0; i < request.getItems().size(); i++) {
                assertEquals(request.getItems().get(i).getId(), response.getTaxLines().get(i).getId());
            }
        }
    }
}
