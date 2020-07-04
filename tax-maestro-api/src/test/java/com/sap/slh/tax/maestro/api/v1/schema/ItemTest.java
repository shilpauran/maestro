package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.Item.JSONParameter.ASSIGNED_PARTIES;
import static com.sap.slh.tax.maestro.api.v1.schema.Item.JSONParameter.ASSIGNED_PRODUCT_ID;
import static com.sap.slh.tax.maestro.api.v1.schema.Item.JSONParameter.ID;
import static com.sap.slh.tax.maestro.api.v1.schema.Item.JSONParameter.QUANTITY;
import static com.sap.slh.tax.maestro.api.v1.schema.Item.JSONParameter.UNIT_PRICE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ItemTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nullAttributesBuild() {
        Item item = Item.builder().build();

        assertNull(item.getId());
        assertNull(item.getAssignedProductId());
        assertNull(item.getQuantity());
        assertNull(item.getUnitPrice());
        assertNull(item.getCosts());
        assertNull(item.getAdditionalInformation());
        assertNull(item.getAssignedParties());
    }

    @Test
    public void initializeFields() {
        String id = "1";
        String assignedProduct = "2";

        Item item = Item.builder().withId(id).withAssignedProductId(assignedProduct).withQuantity(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.TEN).withCosts(Collections.emptyList())
                .withAdditionalInformation(Collections.emptyMap()).withAssignedParties(Collections.emptyList()).build();

        assertEquals(id, item.getId());
        assertEquals(assignedProduct, item.getAssignedProductId());
        assertEquals(BigDecimal.ZERO, item.getQuantity());
        assertEquals(BigDecimal.TEN, item.getUnitPrice());
        assertEquals(Collections.emptyList(), item.getCosts());
        assertEquals(Collections.emptyMap(), item.getAdditionalInformation());
        assertEquals(Collections.emptyList(), item.getAssignedParties());
    }

    @Test
    public void initializeFieldsWithVarArgs() {
        Item item = Item.builder().withAssignedParties(Collections.emptyList()).withCosts(CostInformation.builder().build())
                .build();
        assertNotNull(item.getAssignedParties());
        assertNotNull(item.getCosts());

    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        Item item = this.getDefaultItem();
        String serialized = mapper.writeValueAsString(item);
        Item deserialized = mapper.readValue(serialized, Item.class);

        assertEquals(item, deserialized);
    }

    @Test
    public void nullValidation() {
        try {
            Item.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not null", e.getPropertyErrors(),
                    hasMandatoryItems(ID, QUANTITY, UNIT_PRICE, ASSIGNED_PARTIES, ASSIGNED_PRODUCT_ID));
        }
    }

    @Test
    public void emptyValidation() {
        try {
            Item.builder().withId("").withAssignedProductId("").withAssignedParties(Collections.emptyList()).build()
                    .validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not empty", e.getPropertyErrors(),
                    hasMandatoryItems(ID, ASSIGNED_PARTIES, ASSIGNED_PRODUCT_ID));
        }
    }

    @Test
    public void idValidation() {
        try {
            Item.builder().withId("      ").withAssignedProductId("   ").build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not blank", e.getPropertyErrors(), hasMandatoryItems(ID, ASSIGNED_PRODUCT_ID));
        }
    }

    @Test
    public void assignedPartiesNotNullElements() {
        try {
            Item.builder().withAssignedParties(null, null).build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not null elements", e.getPropertyErrors(), hasMandatoryItems(ASSIGNED_PARTIES));
        }
    }

    @Test
    public void validationWithoutErrors() {
        Item.builder().withId("id").withAssignedProductId("0010").withQuantity(BigDecimal.ZERO)
                .withUnitPrice(BigDecimal.TEN)
                .withAssignedParties(AssignedParty.builder().withId("1").withRole(PartyRole.SHIP_FROM).build(),
                        AssignedParty.builder().withId("2").withRole(PartyRole.SHIP_TO).build())
                .build();
        assertTrue(true);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(Item.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    private Item getDefaultItem() {
        return Item.builder().withId("id").withAssignedProductId("assignedProductId").withQuantity(BigDecimal.TEN)
                .withUnitPrice(BigDecimal.TEN).withCosts(Collections.emptyList()).withAdditionalInformation(null)
                .withAssignedParties(Collections.emptyList()).build();
    }
}
