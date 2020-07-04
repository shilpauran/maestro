package com.sap.slh.tax.maestro.tax.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sap.slh.tax.attributes.determination.model.response.ResponseItem;
import com.sap.slh.tax.attributes.determination.model.response.ItemBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModelBuilder;
import com.sap.slh.tax.attributes.determination.model.response.TaxLineBuilder;
import com.sap.slh.tax.maestro.api.v0.schema.TaxLine;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxValue;
import com.sap.slh.tax.maestro.tax.exceptions.OutOfBoundsEnumMappingException;

public class DeterminationResponseToTaxResponseMapperTest {

    private DeterminationResponseToTaxResponseMapper mapper = DeterminationResponseToTaxResponseMapper.getInstance();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void emptyMapping() {
        TaxAttributesDeterminationResponseModel detResponse = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(TaxAttributesDeterminationResponseBuilder.builder().build())
                .build();

        TaxResponse response = mapper.apply(detResponse);
        this.verifyMapping(detResponse, response);
    }

    @Test
    public void basicMapping() {
        TaxAttributesDeterminationResponseModel detResponse = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(TaxAttributesDeterminationResponseBuilder.builder()
                        .withId("id")
                        .withItems(ItemBuilder.builder()
                                .withCountryRegionCode("AE")
                                .withTaxCode("code")
                                .withTaxes(TaxLineBuilder.builder()
                                        .withTaxTypeCode("TaxType")
                                        .withTaxRate(BigDecimal.TEN)
                                        .withDueCategoryCode("p")
                                        .build())
                                .build())
                        .build())
                .build();

        TaxResponse response = mapper.apply(detResponse);
        this.verifyMapping(detResponse, response);
    }

    @Test
    public void multipleItemsMapping() {
        TaxAttributesDeterminationResponseModel detResponse = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(TaxAttributesDeterminationResponseBuilder.builder()
                        .withId("id")
                        .withItems(
                                ItemBuilder.builder()
                                        .withCountryRegionCode("AE")
                                        .withTaxCode("code1")
                                        .withTaxes(
                                                TaxLineBuilder.builder()
                                                        .withTaxTypeCode("TaxType1.1")
                                                        .withTaxRate(BigDecimal.TEN)
                                                        .withDueCategoryCode("p")
                                                        .build(),
                                                TaxLineBuilder.builder()
                                                        .withTaxTypeCode("TaxType1.2")
                                                        .withTaxRate(BigDecimal.ONE)
                                                        .withDueCategoryCode("r")
                                                        .build())
                                        .build(),
                                ItemBuilder.builder()
                                        .withCountryRegionCode("AE")
                                        .withTaxCode("code2")
                                        .withTaxes(
                                                TaxLineBuilder.builder()
                                                        .withTaxTypeCode("TaxType2.1")
                                                        .withTaxRate(BigDecimal.TEN)
                                                        .withDueCategoryCode("p")
                                                        .build(),
                                                TaxLineBuilder.builder()
                                                        .withTaxTypeCode("TaxType2.2")
                                                        .withTaxRate(BigDecimal.ONE)
                                                        .withDueCategoryCode("r")
                                                        .build())
                                        .build())
                        .build())
                .build();

        TaxResponse response = mapper.apply(detResponse);
                this.verifyMapping(detResponse, response);
    }

    @Test
    public void nullMapping() {
        TaxAttributesDeterminationResponseModel detResponse = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(TaxAttributesDeterminationResponseBuilder.builder()
                        .withId("id")
                        .withItems(ItemBuilder.builder()
                                .withCountryRegionCode(null)
                                .withTaxCode(null)
                                .withTaxes(TaxLineBuilder.builder()
                                        .withTaxTypeCode(null)
                                        .withTaxRate(null)
                                        .withDueCategoryCode(null)
                                        .build())
                                .build())
                        .build())
                .build();

        TaxResponse response = mapper.apply(detResponse);
                this.verifyMapping(detResponse, response);
    }

    @Test
    public void enumCountryErrorMapping() {
        TaxAttributesDeterminationResponseModel detResponse = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(TaxAttributesDeterminationResponseBuilder.builder()
                        .withId(null)
                        .withItems(ItemBuilder.builder()
                                .withCountryRegionCode("NOEMUM")
                                .withTaxCode(null)
                                .withTaxes(TaxLineBuilder.builder()
                                        .withTaxTypeCode(null)
                                        .withTaxRate(null)
                                        .withDueCategoryCode(null)
                                        .build())
                                .build())
                        .build())
                .build();

        thrown.expect(OutOfBoundsEnumMappingException.class);
        mapper.apply(detResponse);
    }

    @Test
    public void enumDueCategoryErrorMapping() {
        TaxAttributesDeterminationResponseModel detResponse = TaxAttributesDeterminationResponseModelBuilder.builder()
                .withResult(TaxAttributesDeterminationResponseBuilder.builder()
                        .withId(null)
                        .withItems(ItemBuilder.builder()
                                .withCountryRegionCode(null)
                                .withTaxCode(null)
                                .withTaxes(TaxLineBuilder.builder()
                                        .withTaxTypeCode(null)
                                        .withTaxRate(null)
                                        .withDueCategoryCode("NOEMUM")
                                        .build())
                                .build())
                        .build())
                .build();

        thrown.expect(OutOfBoundsEnumMappingException.class);
        mapper.apply(detResponse);
    }

    private void verifyMapping(TaxAttributesDeterminationResponseModel detModelResponse,
            TaxResponse response) {

        if (detModelResponse == null
                || detModelResponse.getResult() == null
                || detModelResponse.getResult().getItems() == null) {
            assertNull(response.getCountry());
            assertNull(response.getTaxLines());
            assertNull(response.getTaxLines());
            return;
        }

        TaxAttributesDeterminationResponse detResponse = detModelResponse.getResult();

        assertEquals(detResponse.getItems().size(), response.getTaxLines().size());
        for (int i = 0; i < detResponse.getItems().size(); i++) {
            ResponseItem detItem = detResponse.getItems().get(i);
            TaxLine taxLine = response.getTaxLines().get(i);

            assertNullableObjectAsString(detItem.getCountryRegionCode(), response.getCountry());
            assertNullableObjectAsString(detItem.getCountryRegionCode(), taxLine.getCountry());
            assertEquals(detItem.getTaxEventLegalPhrase(), taxLine.getTaxCodeLegalPhrase());
            assertEquals(detItem.getTaxCode(), taxLine.getTaxCode());

            assertEquals(detItem.getTaxes().size(), taxLine.getTaxValues().size());
            for (int j = 0; j < detItem.getTaxes().size(); j++) {
                com.sap.slh.tax.attributes.determination.model.response.TaxLine detTaxLine = detItem.getTaxes().get(j);
                TaxValue taxValue = taxLine.getTaxValues().get(j);

                assertEquals(detTaxLine.getTaxTypeCode(), taxValue.getTaxTypeCode());
                assertNullableObjectAsString(detTaxLine.getTaxRate(), taxValue.getRate());
                assertNullableObjectAsString(detTaxLine.getNonDeductibleTaxRate(), taxValue.getNonDeductibleTaxRate());
                assertNullableObjectAsString(detTaxLine.getDueCategoryCode(), taxValue.getDueCategory());
                assertEquals(detTaxLine.getIsTaxDeferred() == null
                        ? Boolean.FALSE
                        : detTaxLine.getIsTaxDeferred(), taxValue.getIsTaxDeferred());
            }
        }
    }

    private void assertNullableObjectAsString(Object taxCountry, Object countryCode) {
        if (taxCountry == null) {
            assertNull(countryCode);
        } else {
            assertNotNull(countryCode);
            assertEquals(taxCountry.toString(), countryCode.toString());
        }
    }
}
