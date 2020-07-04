package com.sap.slh.tax.maestro.tax.mapper;

import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.api.v1.schema.ResultDocumentItem;

import java.util.function.Function;
import java.util.stream.Collectors;

public final class QuoteDocumentToQuoteResultDocumentMapper implements Function<QuoteDocument, QuoteResultDocument> {

    private static QuoteDocumentToQuoteResultDocumentMapper instance;

    private QuoteDocumentToQuoteResultDocumentMapper() {
    }

    public static QuoteDocumentToQuoteResultDocumentMapper getInstance() {
        if (instance == null) {
            instance = new QuoteDocumentToQuoteResultDocumentMapper();
        }
        return instance;
    }

    @Override
    public QuoteResultDocument apply(QuoteDocument quoteDocument) {
        return QuoteResultDocument.builder()
                .withId(quoteDocument.getId())
                .withDate(quoteDocument.getDate())
                .withCurrencyCode(quoteDocument.getCurrencyCode())
                .withAmountTypeCode(quoteDocument.getAmountTypeCode())
                .withItems(quoteDocument.getItems() != null
                        ? quoteDocument.getItems().stream().map(this::quoteItemToResultItem).collect(Collectors.toList())
                        : null)
                .build();
    }

    private ResultDocumentItem quoteItemToResultItem(Item quoteItem) {
        return ResultDocumentItem.builder().withId(quoteItem.getId()).build();
    }
}
