package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.api.v1.schema.ResultDocumentItem;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class QuoteDocumentToQuoteResultDocumentMapperTest {

	QuoteDocumentToQuoteResultDocumentMapper mapper = QuoteDocumentToQuoteResultDocumentMapper.getInstance();

	QuoteDocument.Builder quoteDocumentBuilder = QuoteDocument.builder();
	QuoteResultDocument.Builder quoteResultDocumentBuilder = QuoteResultDocument.builder();
	Item.Builder itemBuilder = Item.builder();
	ResultDocumentItem.Builder resultDocumentItemBuilder = ResultDocumentItem.builder();
	
	@Test
	public void basicDocument() {
		String itemiD = UUID.randomUUID().toString();
		Date date = Date.from(Instant.now());
		String document1Id = UUID.randomUUID().toString();
		String document2Id = UUID.randomUUID().toString();
		
		QuoteDocument quoteDocument = quoteDocumentBuilder
				.withId(itemiD)
				.withDate(date)
				.withCurrencyCode(CurrencyCode.BRL)
				.withAmountTypeCode(AmountType.GROSS)
				.withItems(itemBuilder.withId(document1Id).build(), itemBuilder.withId(document2Id).build())
				.build();
		QuoteResultDocument expected = quoteResultDocumentBuilder
				.withId(itemiD)
				.withDate(date)
				.withCurrencyCode(CurrencyCode.BRL)
				.withAmountTypeCode(AmountType.GROSS)
				.withItems(resultDocumentItemBuilder.withId(document1Id).build(), resultDocumentItemBuilder.withId(document2Id).build())
				.build();

		QuoteResultDocument actual = mapper.apply(quoteDocument);

			assertEquals(expected.getId(), actual.getId());
			assertEquals(expected.getDate(), actual.getDate());
			assertEquals(expected.getCurrencyCode(), actual.getCurrencyCode());
			assertEquals(expected.getItems(), actual.getItems());
	}

	@Test
	public void emptyDocument() {
		QuoteDocument quoteDocument = quoteDocumentBuilder.withAmountTypeCode(null).build();
		QuoteResultDocument expectedResult = quoteResultDocumentBuilder.build();

		assertEquals(mapper.apply(quoteDocument), expectedResult);
	}
}
