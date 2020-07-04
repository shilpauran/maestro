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
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.api.v1.schema.ResultDocumentItem;

public class CalculationResponseToQuoteResultDocumentMapperTest {

    private CalculationResponseToQuoteResultDocumentMapper mapper = CalculationResponseToQuoteResultDocumentMapper.getInstance();

    @Test
    public void testFullMapping() {
        TaxCalculationResponse calcResponse = buildCalcResponse();
        QuoteResultDocument quoteResult = mapper.apply(calcResponse);
        ResultDocumentItem documentItem = quoteResult.getItems().get(0);
        ItemResult calcItem = calcResponse.getResult().getItems().get(0);

        assertEquals(calcItem.getId(), documentItem.getId());
        assertEquals(calcItem.getTaxes().size(), documentItem.getTaxes().size());
        assertEquals(calcItem.getTaxes().get(0).getId(), documentItem.getTaxes().get(0).getId());
        assertEquals(calcItem.getTaxes().get(0).getDeductibleTaxAmount(), documentItem.getTaxes().get(0).getDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(0).getNonDeductibleTaxAmount(), documentItem.getTaxes().get(0).getNonDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(0).getTaxAmount(), documentItem.getTaxes().get(0).getTaxAmount());
        assertEquals(calcItem.getTaxes().get(0).getTaxableBaseAmount(), documentItem.getTaxes().get(0).getTaxableBaseAmount());

        assertEquals(calcItem.getTaxes().get(1).getId(), documentItem.getTaxes().get(1).getId());
        assertEquals(calcItem.getTaxes().get(1).getDeductibleTaxAmount(), documentItem.getTaxes().get(1).getDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(1).getNonDeductibleTaxAmount(), documentItem.getTaxes().get(1).getNonDeductibleTaxAmount());
        assertEquals(calcItem.getTaxes().get(1).getTaxAmount(), documentItem.getTaxes().get(1).getTaxAmount());
        assertEquals(calcItem.getTaxes().get(1).getTaxableBaseAmount(), documentItem.getTaxes().get(1).getTaxableBaseAmount());
    }

    @Test
    public void testEmptyCalculationResponseMapping() {
        QuoteResultDocument result = mapper.apply(new TaxCalculationResponse());
        assertEquals(QuoteResultDocument.builder().build(), result);
    }

    @Test
    public void testEmptyCalculationResponseResultMapping() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();
        calcResponse.setResult(new TaxCalculationResponseLine());

        QuoteResultDocument result = mapper.apply(calcResponse);
        assertEquals(QuoteResultDocument.builder().build(), result);
    }

    @Test
    public void testEmptyCalculationResponseResultWithEmptyItemMapping() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();
        TaxCalculationResponseLine line = new TaxCalculationResponseLine();
        line.setItems(Collections.emptyList());
        calcResponse.setResult(line);

        QuoteResultDocument quoteResult = mapper.apply(calcResponse);
        assertEquals(QuoteResultDocument.builder().build(), quoteResult);
    }

    @Test
    public void testEmptyCalculationResponseResultWithNullItemMapping() {
        TaxCalculationResponse calcResponse = new TaxCalculationResponse();
        TaxCalculationResponseLine line = new TaxCalculationResponseLine();
        line.setItems(null);
        
        calcResponse.setResult(line);

        QuoteResultDocument quoteResult = mapper.apply(calcResponse);
        assertEquals(QuoteResultDocument.builder().build(), quoteResult);
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
