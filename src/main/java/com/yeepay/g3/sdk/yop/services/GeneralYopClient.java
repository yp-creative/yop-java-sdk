package com.yeepay.g3.sdk.yop.services;

import com.yeepay.g3.sdk.yop.AbstractYopClient;
import com.yeepay.g3.sdk.yop.YopClientConfiguration;
import com.yeepay.g3.sdk.yop.http.handler.HttpResponseHandler;
import com.yeepay.g3.sdk.yop.http.handler.YopErrorResponseHandler;
import com.yeepay.g3.sdk.yop.http.handler.YopJsonResponseHandler;
import com.yeepay.g3.sdk.yop.http.handler.YopMetadataResponseHandler;

/**
 * title: Yop 客户端的一般实现<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/23 14:36
 */
public class GeneralYopClient extends AbstractYopClient {

    protected static final HttpResponseHandler[] GENERAL_HANDLERS = new HttpResponseHandler[]{
            new YopMetadataResponseHandler(), new YopErrorResponseHandler(), new YopJsonResponseHandler()};

    public GeneralYopClient(YopClientConfiguration config) {
        super("", config, GENERAL_HANDLERS);
    }

    public GeneralYopClient(String serviceId, YopClientConfiguration config) {
        super(serviceId, config, GENERAL_HANDLERS);
    }

    public GeneralYopClient(String serviceId, YopClientConfiguration config, HttpResponseHandler[] responseHandlers) {
        super(serviceId, config, responseHandlers);
    }

}
