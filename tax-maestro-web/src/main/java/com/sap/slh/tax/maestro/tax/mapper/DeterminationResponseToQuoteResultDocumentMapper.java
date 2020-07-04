package com.sap.slh.tax.maestro.tax.mapper;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.attributes.determination.model.response.ResponseItem;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.attributes.determination.model.response.TaxLine;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.v1.domain.DueCategory;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteResultDocument;
import com.sap.slh.tax.maestro.api.v1.schema.ResultDocumentItem;
import com.sap.slh.tax.maestro.api.v1.schema.TaxResultItem;
import com.sap.slh.tax.maestro.tax.exceptions.OutOfBoundsEnumMappingException;

public final class DeterminationResponseToQuoteResultDocumentMapper
        implements Function<TaxAttributesDeterminationResponseModel, QuoteResultDocument> {
    private static final Logger logger = LoggerFactory
            .getLogger(DeterminationResponseToQuoteResultDocumentMapper.class);

    private static DeterminationResponseToQuoteResultDocumentMapper instance;

    private DeterminationResponseToQuoteResultDocumentMapper() {
    }

    /**
     * Static singleton constructor for {@link DeterminationResponseToQuoteResultDocumentMapper}.
     *
     * @return
     */
    public static DeterminationResponseToQuoteResultDocumentMapper getInstance() {
        if (instance == null) {
            instance = new DeterminationResponseToQuoteResultDocumentMapper();
        }
        return instance;
    }

    @Override
    public QuoteResultDocument apply(TaxAttributesDeterminationResponseModel determinationResponse) {
        QuoteResultDocument.Builder builder = QuoteResultDocument.builder();

        if (determinationResponse != null && determinationResponse.getResult() != null) {
            TaxAttributesDeterminationResponse detResult = determinationResponse.getResult();

            if (!CollectionUtils.isEmpty(detResult.getItems())) {
                builder.withItems(detResult.getItems().stream().map(this::convertItems).collect(
                        Collectors.toList()));
            }
        }

        return builder.build();
    }

    private ResultDocumentItem convertItems(ResponseItem determinationItem) {
        ResultDocumentItem.Builder builder = ResultDocumentItem.builder();

        builder.withId(determinationItem.getId());
        builder.withCountryRegionCode(enumValueOfString(determinationItem.getCountryRegionCode(), CountryCode.class));
        builder.withSubdivisionCode(determinationItem.getSubdivisionCode());
        builder.withTaxEventCode(determinationItem.getTaxEventCode());
        builder.withTaxEventLegalPhrase(determinationItem.getTaxEventLegalPhrase());

        if (!CollectionUtils.isEmpty(determinationItem.getTaxes())) {
            builder.withTaxes(
                    determinationItem.getTaxes().stream().map(this::convertTaxes).collect(Collectors.toList()));
        }

        if (determinationItem.getTaxCode() != null) {
            builder.addAdditionalInformation("taxCode", determinationItem.getTaxCode());
        }

        return builder.build();
    }

    private TaxResultItem convertTaxes(TaxLine taxLine) {
        TaxResultItem.Builder builder = TaxResultItem.builder();

        builder.withId(taxLine.getId());
        builder.withTaxTypeCode(taxLine.getTaxTypeCode());
        builder.withTaxRate(taxLine.getTaxRate());
        builder.withDueCategoryCode(enumValueOfString(taxLine.getDueCategoryCode(), DueCategory.class));

        builder.withIsTaxDeferred(taxLine.getIsTaxDeferred() == null ? Boolean.FALSE : taxLine.getIsTaxDeferred());
        builder.withNonDeductibleTaxRate(taxLine.getNonDeductibleTaxRate());

        return builder.build();
    }

    private <E extends Enum<E>> E enumValueOfString(String value, Class<E> klazz) {
        E enumValue = null;
        if (value != null) {
            try {
                enumValue = Enum.valueOf(klazz, value);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
                throw new OutOfBoundsEnumMappingException(klazz, value);
            }
        }
        return enumValue;
    }
}
