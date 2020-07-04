package com.sap.slh.tax.maestro.config;

import static com.sap.hcp.cf.logging.common.Fields.CORRELATION_ID;
import static com.sap.hcp.cf.logging.common.Fields.TENANT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;

import com.sap.slh.tax.maestro.context.RequestContextService;

import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

@RunWith(MockitoJUnitRunner.class)
public class MdcContextLifterTest {

    private static final String DUMMY_CORRELATION_ID = "38fc2962-ceaa-4e32-b4d3-5e66cbd25790";
    private static final String DUMMY_TENANT_ID = "23a7192a-fa3a-4a22-8694-fa2d5c4e5791";
    private static final Context CONTEXT_WITH_CORRELATION_ID_AND_TENANT_ID = Context.of(CORRELATION_ID,
            DUMMY_CORRELATION_ID, TENANT_ID, DUMMY_TENANT_ID);

    private MdcContextLifter<Object> mdcContextLifter;

    @Mock
    private CoreSubscriber<Object> coreSubscriber;

    @Mock
    private RequestContextService requestContextService;

    @Mock
    private Object object;

    @Before
    public void init() {
        mdcContextLifter = new MdcContextLifter<>(coreSubscriber, requestContextService);
    }

    @Test
    public void shouldPutCorrelationIdAndTenantIdInMdc() {
        MDC.clear();
        when(coreSubscriber.currentContext()).thenReturn(CONTEXT_WITH_CORRELATION_ID_AND_TENANT_ID);
        when(requestContextService.getCorrelationId(CONTEXT_WITH_CORRELATION_ID_AND_TENANT_ID))
                .thenReturn(Optional.of(DUMMY_CORRELATION_ID));
        when(requestContextService.getTenantId(CONTEXT_WITH_CORRELATION_ID_AND_TENANT_ID))
                .thenReturn(Optional.of(DUMMY_TENANT_ID));

        mdcContextLifter.onNext(object);

        assertEquals(DUMMY_CORRELATION_ID, MDC.get(CORRELATION_ID));
        assertEquals(DUMMY_TENANT_ID, MDC.get(TENANT_ID));
    }

    @Test
    public void shouldRemoveCorrelationIdAndTenantIdFromMdc() {
        MDC.put(CORRELATION_ID, DUMMY_CORRELATION_ID);
        MDC.put(CORRELATION_ID, TENANT_ID);
        when(coreSubscriber.currentContext()).thenReturn(Context.empty());
        when(requestContextService.getCorrelationId(Context.empty())).thenReturn(Optional.empty());
        when(requestContextService.getTenantId(Context.empty())).thenReturn(Optional.empty());

        mdcContextLifter.onNext(object);

        assertNull(MDC.get(CORRELATION_ID));
        assertNull(MDC.get(TENANT_ID));
    }

}
