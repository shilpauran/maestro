package com.sap.slh.tax.maestro.security.mock;

import com.sap.cloud.security.xsuaa.mock.XsuaaRequestDispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.http.HttpStatus;

public class XsuaaMockRequestDispatcher extends XsuaaRequestDispatcher {
    private static int callCount = 0;

    public static int getCallCount() {
        return callCount;
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        callCount++;
        if ("/testdomain/token_keys".equals(request.getPath())) {
            return getTokenKeyForKeyId(PATH_TOKEN_KEYS_TEMPLATE, "legacy-token-key");
        }
        if (request.getPath().endsWith("/token_keys")) {
            return getTokenKeyForKeyId(PATH_TOKEN_KEYS_TEMPLATE, "legacy-token-key");
        }
        return getResponse(RESPONSE_404, HttpStatus.NOT_FOUND);
    }
}
