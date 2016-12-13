package com.yeepay.g3.sdk.yop.http.handler;

import com.google.common.base.CharMatcher;
import com.yeepay.g3.sdk.yop.YopResponseMetadata;
import com.yeepay.g3.sdk.yop.http.Headers;
import com.yeepay.g3.sdk.yop.http.YopHttpResponse;
import com.yeepay.g3.sdk.yop.model.AbstractYopResponse;

/**
 * title: http 响应结果元数据处理器<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 16:10
 */
public class YopMetadataResponseHandler implements HttpResponseHandler {

    @Override
    public boolean handle(YopHttpResponse httpResponse, AbstractYopResponse response) throws Exception {
        YopResponseMetadata metadata = response.getMetadata();
        metadata.setYopRequestId(httpResponse.getHeader(Headers.YOP_REQUEST_ID));
        metadata.setYopContentSha256(httpResponse.getHeader(Headers.YOP_CONTENT_SHA256));
        metadata.setContentDisposition(httpResponse.getHeader(Headers.CONTENT_DISPOSITION));
        metadata.setContentEncoding(httpResponse.getHeader(Headers.CONTENT_ENCODING));
        metadata.setContentLength(httpResponse.getHeaderAsLong(Headers.CONTENT_LENGTH));
        metadata.setContentMd5(httpResponse.getHeader(Headers.CONTENT_MD5));
        metadata.setContentRange(httpResponse.getHeader(Headers.CONTENT_RANGE));
        metadata.setContentType(httpResponse.getHeader(Headers.CONTENT_TYPE));
        metadata.setDate(httpResponse.getHeaderAsRfc822Date(Headers.DATE));
        metadata.setTransferEncoding(httpResponse.getHeader(Headers.TRANSFER_ENCODING));
        String eTag = httpResponse.getHeader(Headers.ETAG);
        if (eTag != null) {
            metadata.setETag(CharMatcher.is('"').trimFrom(eTag));
        }
        metadata.setExpires(httpResponse.getHeaderAsRfc822Date(Headers.EXPIRES));
        metadata.setLastModified(httpResponse.getHeaderAsRfc822Date(Headers.LAST_MODIFIED));
        metadata.setServer(httpResponse.getHeader(Headers.SERVER));
        return false;
    }

}
