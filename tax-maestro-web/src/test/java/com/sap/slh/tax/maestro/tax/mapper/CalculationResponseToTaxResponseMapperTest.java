package com.sap.slh.tax.maestro.tax.mapper;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.sap.slh.tax.calculation.model.common.ItemResult;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponseLine;
import com.sap.slh.tax.calculation.model.common.TaxResult;
import com.sap.slh.tax.maestro.api.v0.schema.TaxLine;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;

public class CalculationResponseToTaxResponseMapperTest {

    private CalculationResponseToTaxResponseMapper mapper = CalculationResponseToTaxResponseMapper.getInstance();

    @Test
    public void testFullMapping() {
        TaxCalculationResponse calcResponse = buildCalcResponse();
        TaxResponse taxResponse = mapper.apply(calcResponse);
        TaxLine taxLine = taxResponse.getTaxLines().get(0);
        ItemResult calcItem = calcResponse.getResult().getItems().get(0);

        assertEquals(calcItem.getId(), taxLine.getId());
        assertEquals(calcItem.getTaxes().size(), taxLine.getTaxValues().size());
        assertEquals(calcItem.getTaxes().get(0).getDeductibleTaxAmount().toString(), taxLine.getTaxValues().get(0).getDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(0).getNonDeductibleTaxAmount().toString(), taxLine.getTaxValues().get(0).getNonDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(0).getTaxAmount().toString(), taxLine.getTaxValues().get(0).getValue());
        assertEquals(calcItem.getTaxes().get(0).getTaxableBaseAmount().toString(), taxLine.getTaxValues().get(0).getTaxable());

        assertEquals(calcItem.getTaxes().get(1).getDeductibleTaxAmount().toString(), taxLine.getTaxValues().get(1).getDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(1).getNonDeductibleTaxAmount().toString(), taxLine.getTaxValues().get(1).getNonDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(1).getTaxAmount().toString(), taxLine.getTaxValues().get(1).getValue());
        assertEquals(calcItem.getTaxes().get(1).getTaxableBaseAmount().toString(), taxLine.getTaxValues().get(1).getTaxable());
    }

    @Test
    public void testEmptyCalculationResponseMapping() {
        TaxResponse response = mapper.apply(new TaxCalculationResponse());
        assertEquals(TaxResponse.builder().build(), response);
    }

    @Test
    public void testEmptyCalculationResponseResultMapping() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();
        calcResponse.setResult(new TaxCalculationResponseLine());

        TaxResponse response = mapper.apply(calcResponse);
        assertEquals(TaxResponse.builder().build(), response);
    }

    @Test
    public void testEmptyCalculationResponseResultWithEmptyItemMapping() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();
        TaxCalculationResponseLine line = new TaxCalculationResponseLine();
        line.setItems(Collections.emptyList());
        calcResponse.setResult(line);

        TaxResponse response = mapper.apply(calcResponse);
        assertEquals(TaxResponse.builder().build(), response);
    }

    @Test
    public void testEmptyCalculationResponseResultWithNullItemMapping() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();
        TaxCalculationResponseLine line = new TaxCalculationResponseLine();
        line.setItems(null);
        calcResponse.setResult(line);

        TaxResponse response = mapper.apply(calcResponse);
        assertEquals(TaxResponse.builder().build(), response);
    }

    private TaxCalculationResponse buildCalcResponse() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();

        TaxResult tax1 = new TaxResult();
        tax1.setId("tax1ID");
        tax1.setDeductibleTaxAmount(BigDecimal.ONE);
        tax1.setNonDeductibleTaxAmount(BigDecimal.ONE);
        tax1.setTaxableBaseAmount(BigDecimal.ONE);
        tax1.setTaxAmount(BigDecimal.ONE);

        TaxResult tax2 = new TaxResult();
        tax2.setId("tax2ID");
        tax2.setDeductibleTaxAmount(BigDecimal.TEN);
        tax2.setNonDeductibleTaxAmount(BigDecimal.TEN);
        tax2.setTaxableBaseAmount(BigDecimal.TEN);
        tax2.setTaxAmount(BigDecimal.TEN);

        ItemResult item1 = new ItemResult();
        item1.setId("itemID");
        item1.setTaxes(Arrays.asList(tax1, tax2));

        TaxCalculationResponseLine responseLine = new TaxCalculationResponseLine();
        responseLine.setId("responseLineID");
        responseLine.setItems(Arrays.asList(item1));

        calcResponse.setResult(responseLine);

        return calcResponse;
    }
}
