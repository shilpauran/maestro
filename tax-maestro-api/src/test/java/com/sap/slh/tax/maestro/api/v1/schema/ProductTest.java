package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.assertion.HasMutualAttribute.hasMutualAttributes;
import static com.sap.slh.tax.maestro.api.v1.schema.Product.JSONParameter.ID;
import static com.sap.slh.tax.maestro.api.v1.schema.Product.JSONParameter.MASTER_DATA_PRODUCT_ID;
import static com.sap.slh.tax.maestro.api.v1.schema.Product.JSONParameter.PRODUCTS;
import static com.sap.slh.tax.maestro.api.v1.schema.Product.JSONParameter.TAX_CLASSIFICATIONS;
import static com.sap.slh.tax.maestro.api.v1.schema.Product.JSONParameter.TYPE_CODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ProductTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nullBuilder() {
        Product product = Product.builder().build();
        assertNull(product.getId());
        assertNull(product.getMasterDataProductId());
        assertNull(product.getTypeCode());
        assertNull(product.getTaxClassifications());
        assertNull(product.getStandardClassifications());
        assertNull(product.getAdditionalInformation());
    }

    @Test
    public void varargBuilder() {
        Product product = Product.builder().withTaxClassifications(Collections.emptyList())
                .withStandardClassifications(Collections.emptyList()).build();
        assertEquals(Collections.emptyList(), product.getTaxClassifications());
        assertEquals(Collections.emptyList(), product.getStandardClassifications());
    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        Product product = this.getDefaultProduct();
        String serialized = mapper.writeValueAsString(product);
        Product deserialized = mapper.readValue(serialized, Product.class);

        assertEquals(product, deserialized);
    }

    @Test
    public void nullValidations() {
        try {
            Product.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not null", e.getPropertyErrors(), hasMandatoryItems(ID, TYPE_CODE));
        }
    }

    @Test
    public void emptyValidations() {
        try {
            Product.builder().withId("").build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not empty", e.getPropertyErrors(), hasMandatoryItems(ID));
        }
    }

    @Test
    public void blankValidations() {
        try {
            Product.builder().withId("    ").build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not blank", e.getPropertyErrors(), hasMandatoryItems(ID));
        }
    }

    @Test
    public void mutualExclusionValidation_withMasterDataAndType() {
        try {
            Product.builder().withId("id").withMasterDataProductId("master").withTypeCode(ProductType.MATERIAL).build()
                    .validate();
            fail();
        } catch (InvalidModelException ex) {
            assertThat("mutual", ex.getPropertyErrors(), hasMutualAttributes(MASTER_DATA_PRODUCT_ID, TYPE_CODE));
            assertThat("mutual exclusion", ex.getPropertyErrors(), hasMandatoryItems(PRODUCTS));
        }
    }

    @Test
    public void mutualExclusionValidation_withMasterDataAndTaxClassifications() {
        try {
            Product.builder()
                    .withId("id")
                    .withTaxClassifications(ProductTaxClassification.builder()
                            .withCountryRegionCode(CountryCode.AD)
                            .withTaxTypeCode("taxTypeCode")
                            .withIsSoldElectronically(false)
                            .withIsServicePointTaxable(false)
                            .build())
                    .withMasterDataProductId("master")
                    .build()
                    .validate();
            fail();
        } catch (InvalidModelException ex) {
            assertThat("mutual", ex.getPropertyErrors(),
                    hasMutualAttributes(MASTER_DATA_PRODUCT_ID, TAX_CLASSIFICATIONS));
            assertThat("mutual exclusion", ex.getPropertyErrors(), hasMandatoryItems(PRODUCTS));
        }
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(Product.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

	private Product getDefaultProduct() {
		return Product.builder().withId("id").withMasterDataProductId("MD_ID_01").withTypeCode(ProductType.MATERIAL)
				.withTaxClassifications(Collections.emptyList())
				.withStandardClassifications(Collections.emptyList()).withAdditionalInformation(null).build();
	}
}
