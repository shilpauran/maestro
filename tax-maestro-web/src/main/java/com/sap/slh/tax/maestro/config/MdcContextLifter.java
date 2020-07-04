package com.sap.slh.tax.maestro.config;

import static com.sap.hcp.cf.logging.common.Fields.CORRELATION_ID;
import static com.sap.hcp.cf.logging.common.Fields.TENANT_ID;

import java.util.Optional;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;

import com.sap.slh.tax.maestro.context.RequestContextService;

import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

class MdcContextLifter<T> implements CoreSubscriber<T> {

    private CoreSubscriber<T> coreSubscriber;
    private RequestContextService requestContextService;

    MdcContextLifter(CoreSubscriber<T> coreSubscriber, RequestContextService requestContextService) {
        this.coreSubscriber = coreSubscriber;
        this.requestContextService = requestContextService;
    }

    @Override
    public void onNext(T element) {
        Context context = coreSubscriber.currentContext();

        synchronizeMdc(CORRELATION_ID, requestContextService.getCorrelationId(context));
        synchronizeMdc(TENANT_ID, requestContextService.getTenantId(context));

        coreSubscriber.onNext(element);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        coreSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onComplete() {
        coreSubscriber.onComplete();
    }

    @Override
    public void onError(Throwable throwable) {
        coreSubscriber.onError(throwable);
    }

    @Override
    public Context currentContext() {
        return coreSubscriber.currentContext();
    }

    private void synchronizeMdc(String key, Optional<String> value) {
        if (value.isPresent()) {
            MDC.put(key, value.get());
        } else {
            MDC.remove(key);
        }
    }

}
