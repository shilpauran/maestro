package com.sap.slh.tax.maestro.tax.mapper;

import static com.sap.slh.tax.maestro.tax.mapper.DetermineMapperFunction.convertList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.sap.slh.tax.attributes.determination.model.request.CompanyInformation;
import com.sap.slh.tax.attributes.determination.model.request.Item;
import com.sap.slh.tax.attributes.determination.model.request.Party;
import com.sap.slh.tax.attributes.determination.model.request.Product;
import com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;

import reactor.core.publisher.Mono;

public final class QuoteDocumentToDetermineRequestMapper
        implements Function<QuoteDocument, Mono<TaxAttributesDeterminationRequest>> {

    private static QuoteDocumentToDetermineRequestMapper instance;

    private QuoteDocumentToDetermineRequestMapper() {

    }

    public static QuoteDocumentToDetermineRequestMapper getInstance() {
        if (instance == null) {
            instance = new QuoteDocumentToDetermineRequestMapper();
        }
        return instance;
    }

    @Override
    public Mono<TaxAttributesDeterminationRequest> apply(QuoteDocument quoteDocument) {
        TaxAttributesDeterminationRequest determinationRequest = new TaxAttributesDeterminationRequest();

        Optional.ofNullable(quoteDocument.getId()).filter(StringUtils::isNotEmpty).ifPresent(
                determinationRequest::setId);

        Optional.ofNullable(quoteDocument.getDate()).ifPresent(determinationRequest::setDate);

        Optional.ofNullable(quoteDocument.getTransactionTypeCode())
                .ifPresent(value -> determinationRequest.setTransactionTypeCode(getTransactionType(value)));

        Optional.ofNullable(quoteDocument.getIsTransactionWithinTaxReportingGroup())
                .ifPresent(determinationRequest::setIsTransactionWithinTaxReportingGroup);

        determinationRequest.setCompanyInformation(getCompanyInformation(quoteDocument));
        determinationRequest.setItems(getItemsFromDocument(quoteDocument));
        determinationRequest.setProducts(getProductsFromDocument(quoteDocument));
        determinationRequest.setParties(getPartiesFromDocument(quoteDocument));

        return Mono.just(determinationRequest);
    }

    private CompanyInformation getCompanyInformation(QuoteDocument quoteDocument) {
        CompanyInformation companyInformation = new CompanyInformation();

        Optional.ofNullable(quoteDocument.getCompanyInformation()).ifPresent(value -> {
            Optional.ofNullable(value.getAssignedPartyId()).filter(StringUtils::isNotEmpty).ifPresent(
                    companyInformation::setAssignedPartyId);
            Optional.ofNullable(value.getIsDeferredTaxEnabled()).ifPresent(companyInformation::setIsDeferredTaxEnabled);
        });

        return companyInformation;
    }

    private TaxAttributesDeterminationRequest.TransactionType getTransactionType(TransactionType transactionType) {
        return TaxAttributesDeterminationRequest.TransactionType.fromValue(transactionType.name());
    }

    private List<Item> getItemsFromDocument(QuoteDocument quoteDocument) {
        return convertList(quoteDocument.getItems(), DetermineMapperFunction.convertDocumentItemToDetermineItem);
    }

    private List<Product> getProductsFromDocument(QuoteDocument quoteDocument) {
        return convertList(quoteDocument.getProducts(),
                DetermineMapperFunction.convertDocumentProductToDetermineProduct);
    }

    private List<Party> getPartiesFromDocument(QuoteDocument quoteDocument) {
        return convertList(quoteDocument.getParties(), DetermineMapperFunction.convertDocumentPartyToDetermineParty);
    }

}
