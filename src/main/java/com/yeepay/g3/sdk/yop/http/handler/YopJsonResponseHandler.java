package com.yeepay.g3.sdk.yop.http.handler;

import com.yeepay.g3.sdk.yop.http.YopHttpResponse;
import com.yeepay.g3.sdk.yop.model.AbstractYopResponse;
import com.yeepay.g3.sdk.yop.utils.JsonUtils;

import java.io.InputStream;

/**
 * title: http 响应结果 Json 数据处理器<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 16:12
 */
public class YopJsonResponseHandler implements HttpResponseHandler {

    @Override
    public boolean handle(YopHttpResponse httpResponse, AbstractYopResponse response) throws Exception {
        InputStream content = httpResponse.getContent();
        if (content != null) {
            if (response.getMetadata().getContentLength() > 0
                    || "chunked".equalsIgnoreCase(response.getMetadata().getTransferEncoding())) {
                JsonUtils.load(content, response);
            }
            content.close();
        }
        return true;
    }

}
