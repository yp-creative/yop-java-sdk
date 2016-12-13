package com.yeepay.g3.sdk.yop.http;

import com.google.common.collect.Maps;
import com.yeepay.g3.sdk.yop.utils.DateUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * title: Yop HttpResponse<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 12:03
 */
public class YopHttpResponse {

    private static final Logger LOGGER = Logger.getLogger(YopHttpResponse.class);

    private CloseableHttpResponse httpResponse;

    private InputStream content;

    public YopHttpResponse(CloseableHttpResponse httpResponse) throws IOException {
        this.httpResponse = httpResponse;

        HttpEntity entity = httpResponse.getEntity();
        if (null != entity && entity.isStreaming()) {
            this.content = entity.getContent();
        }
    }

    public String getHeader(String name) {
        Header header = this.httpResponse.getFirstHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    public long getHeaderAsLong(String name) {
        String value = this.getHeader(name);
        if (value == null) {
            return -1;
        }
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            LOGGER.warn("Invalid " + name + ":" + value, e);
            return -1;
        }
    }

    public Date getHeaderAsRfc822Date(String name) {
        String value = this.getHeader(name);
        if (value == null) {
            return null;
        }
        try {
            return DateUtils.parseRfc822Date(value);
        } catch (Exception e) {
            LOGGER.warn("Invalid " + name + ":" + value, e);
            return null;
        }
    }

    public InputStream getContent() {
        return this.content;
    }

    public String getStatusText() {
        return this.httpResponse.getStatusLine().getReasonPhrase();
    }

    public int getStatusCode() {
        return this.httpResponse.getStatusLine().getStatusCode();
    }

    public CloseableHttpResponse getHttpResponse() {
        return this.httpResponse;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = Maps.newHashMap();
        for (Header header : this.httpResponse.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        return headers;
    }

}
