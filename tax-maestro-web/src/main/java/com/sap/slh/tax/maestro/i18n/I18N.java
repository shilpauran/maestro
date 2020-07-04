package com.sap.slh.tax.maestro.i18n;

public enum I18N {
    MESSAGE_TENANT_NOT_RETRIEVED("message.tenantNotRetrievedException"),
    MESSAGE_UNEXPECTED_EXCEPTION("message.unexpectedException"),
    MESSAGE_INVALID_REQUEST_EXCEPTION("message.invalidRequestException"),
    MESSAGE_INVALID_FORMAT_EXCEPTION("message.invalidFormatException"),
    MESSAGE_INVALID_PROPERTY_MANDATORY("message.invalidPropertyMandatory"),
    MESSAGE_INVALID_PROPERTY_MANDATORY_VALUE("message.invalidPropertyMandatoryValueMissing"),
    MESSAGE_INVALID_PROPERTY_OUT_OF_BOUNDS("message.invalidPropertyOutOfBounds"),
    MESSAGE_INVALID_PROPERTY_MISSING_REFERENCE("message.invalidPropertyMissingReference"),
    MESSAGE_INVALID_PROPERTY_REFERENCE("message.invalidPropertyReference"),
    MESSAGE_INVALID_PROPERTY_REFERENCE_WITH_ID("message.invalidPropertyReferenceWithId"),
    MESSAGE_MUTUAL_PROPERTY_EXCLUSION("message.mutualExclusionProperty"),
    MESSAGE_INVALID_JSON_REQUEST("message.invalidJsonRequestException"),
    MESSAGE_INVALID_JSON_ENUM_TYPE("message.invalidJSONEnumType"),
    MESSAGE_INVALID_JSON_VALUE_TYPE("message.invalidJSONValueType"),
    MESSAGE_NO_RELEVANT_COUNTRY_FOR_DESTINATION("message.noRelevantCountryForDestination"),
    MESSAGE_DIRECT_PAYLOAD_NOT_SUPPORTED("message.directPayloadNotSupported"),
    MESSAGE_MULTIPLE_VALUES_FOR_PROPERTY("message.invalidMultipleValuesForProperty"),
    MESSAGE_JWT_NOT_PROVIDED("message.jwtNotProvided"),
    MESSAGE_PARTNER_UNAUTHORIZED_HTTP_RESPONSE("message.partnerUnauthorizedHttpResponse"),
    MESSAGE_PARTNER_NOT_FOUND_HTTP_RESPONSE("message.partnerNotFoundHttpResponse"),
    MESSAGE_PARTNER_SERVER_ERROR("message.partnerServerError"),
    MESSAGE_PARTNER_GATEWAY_TIMEOUT("message.partnerGatewayTimeout"),
    MESSAGE_PARTNER_REQUEST_TIMEOUT("message.partnerRequestTimeout"),
    MESSAGE_PARTNER_UNAVAILABLE("message.partnerUnavailable"),
    MESSAGE_PAYLOAD_TOO_LARGE("message.payloadTooLarge"),
    MESSAGE_PARTNER_RESPONSE_TOO_LARGE("message.partnerResponseTooLarge");

    private final String value;

    I18N(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
