package com.sap.slh.tax.maestro.api.v0.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ErrorType {
    @JsonProperty("bad_request")
    BAD_REQUEST,

    @JsonProperty("unsupported_media")
    UNSUPPORTED_MEDIA,

    @JsonProperty("invalid_input")
    INVALID_INPUT,

    @JsonProperty("invalid_content")
    INVALID_CONTENT,

    @JsonProperty("server_exception")
    SERVER_EXCEPTION,

    @JsonProperty("invalid_input_Location")
    INVALID_INPUT_LOCATION,

    @JsonProperty("problem_in_cloud_connector")
    PROBLEM_IN_CLOUD_CONNECTOR,

    @JsonProperty("request_not_valid")
    REQUEST_NOT_VALID,

    @JsonProperty("request_data_not_valid")
    REQUEST_DATA_NOT_VALID,

    @JsonProperty("unable_to_determine_tax_engine")
    UNABLE_TO_DETERMINE_TAX_ENGINE,

    @JsonProperty("partner_did_not_resolve_taxes")
    PARTNER_DID_NOT_RESOLVE_TAXES,

    @JsonProperty("taxConfiguration_connection_exception")
    TAXCONFIGURATION_CONNECTION_EXCEPTION,

    @JsonProperty("unable_to_determine_tax_configuration_destination")
    UNABLE_TO_DETERMINE_TAX_CONFIGURATION_DESTINATION,

    @JsonProperty("no_content")
    NO_CONTENT,

    @JsonProperty("partial_content")
    PARTIAL_CONTENT;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
