package com.yeepay.g3.sdk.yop.http.handler;

import com.yeepay.g3.sdk.yop.YopErrorResponse;
import com.yeepay.g3.sdk.yop.YopServiceException;
import com.yeepay.g3.sdk.yop.YopServiceException.ErrorType;
import com.yeepay.g3.sdk.yop.http.YopHttpResponse;
import com.yeepay.g3.sdk.yop.model.AbstractYopResponse;
import com.yeepay.g3.sdk.yop.utils.JsonUtils;
import org.apache.http.HttpStatus;

import java.io.InputStream;

/**
 * title: http 响应结果报错处理器<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 16:18
 */
public class YopErrorResponseHandler implements HttpResponseHandler {

    @Override
    public boolean handle(YopHttpResponse httpResponse, AbstractYopResponse response) throws Exception {
        if (httpResponse.getStatusCode() / 100 == HttpStatus.SC_OK / 100) {
            // not an error
            return false;
        }

        YopServiceException yse = null;
        InputStream content = httpResponse.getContent();
        if (content != null) {
            YopErrorResponse bceErrorResponse = JsonUtils.loadFrom(content, YopErrorResponse.class);
            if (bceErrorResponse.getMessage() != null) {
                yse = new YopServiceException(bceErrorResponse.getMessage());
                yse.setErrorCode(bceErrorResponse.getCode());
                yse.setRequestId(bceErrorResponse.getRequestId());
            }
            content.close();
        }
        if (yse == null) {
            yse = new YopServiceException(httpResponse.getStatusText());
            yse.setRequestId(response.getMetadata().getYopRequestId());
        }
        yse.setStatusCode(httpResponse.getStatusCode());
        if (yse.getStatusCode() >= 500) {
            yse.setErrorType(ErrorType.Service);
        } else {
            yse.setErrorType(ErrorType.Client);
        }
        throw yse;
    }

}
