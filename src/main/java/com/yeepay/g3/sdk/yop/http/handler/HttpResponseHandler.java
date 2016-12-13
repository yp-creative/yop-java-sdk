package com.yeepay.g3.sdk.yop.http.handler;

import com.yeepay.g3.sdk.yop.http.YopHttpResponse;
import com.yeepay.g3.sdk.yop.model.AbstractYopResponse;

/**
 * title: http 响应结果处理器<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 16:08
 */
public interface HttpResponseHandler {

    boolean handle(YopHttpResponse httpResponse, AbstractYopResponse response) throws Exception;

}
