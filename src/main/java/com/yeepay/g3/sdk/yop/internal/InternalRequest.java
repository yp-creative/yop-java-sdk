package com.yeepay.g3.sdk.yop.internal;

import com.google.common.collect.Maps;
import com.yeepay.g3.sdk.yop.auth.YopCredentials;
import com.yeepay.g3.sdk.yop.auth.YopSignOptions;
import com.yeepay.g3.sdk.yop.http.HttpMethodName;
import com.yeepay.g3.sdk.yop.utils.Assert;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.http.annotation.NotThreadSafe;

import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * title: 内部请求<br/>
 * description: 代表发送到 Yop 平台的一次请求,包含请求参数,报文头,加密方式等信息<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 10:16
 */
@NotThreadSafe
public class InternalRequest {

    /**
     * 报文头
     */
    protected Map<String, String> headers = Maps.newHashMap();

    /**
     * 请求参数
     */
    protected Map<String, String[]> parameters = Maps.newHashMap();

    /**
     * endpoint uri
     */
    protected URI uri;

    /**
     * http method
     */
    protected HttpMethodName httpMethod;

    /**
     * An optional stream from which to read the request payload.
     */
    protected RestartableInputStream content;

    protected YopCredentials credentials;

    protected YopSignOptions signOptions;

    protected boolean expectContinueEnabled;

    protected InternalRequest() {
        // do nothing
        // just for YopRequest.class
    }

    public InternalRequest(HttpMethodName httpMethod, URI uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public Map<String, String> addHeader(String name, String value) {
        this.headers.put(name, value);
        return this.headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public void setParameter(String name, String value) {
        this.parameters.put(name, new String[]{value});
    }

    public void setParameter(String name, String[] values) {
        Assert.notNull(name, "Parameter name must not be null");
        this.parameters.put(name, values);
    }

    public void addParameter(String name, String value) {
        addParameter(name, new String[]{value});
    }

    public void addParameter(String name, String[] values) {
        Assert.notNull(name, "Parameter name must not be null");
        String[] oldArr = this.parameters.get(name);
        if (oldArr != null) {
            String[] newArr = new String[oldArr.length + values.length];
            System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
            System.arraycopy(values, 0, newArr, oldArr.length, values.length);
            this.parameters.put(name, newArr);
        } else {
            this.parameters.put(name, values);
        }
    }

    public void removeParameter(String name) {
        org.springframework.util.Assert.notNull(name, "Parameter name must not be null");
        this.parameters.remove(name);
    }

    public void removeAllParameters() {
        this.parameters.clear();
    }

    public String getParameter(String name) {
        String[] arr = (name != null ? this.parameters.get(name) : null);
        return (arr != null && arr.length > 0 ? arr[0] : null);
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    public String[] getParameterValues(String name) {
        return (name != null ? this.parameters.get(name) : null);
    }

    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(this.parameters);
    }

    public URI getUri() {
        return uri;
    }

    public HttpMethodName getHttpMethod() {
        return httpMethod;
    }

    public RestartableInputStream getContent() {
        return content;
    }

    public void setContent(RestartableInputStream content) {
        this.content = content;
    }

    public YopCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(YopCredentials credentials) {
        this.credentials = credentials;
    }

    public YopSignOptions getSignOptions() {
        return signOptions;
    }

    public void setSignOptions(YopSignOptions signOptions) {
        this.signOptions = signOptions;
    }

    public boolean isExpectContinueEnabled() {
        return expectContinueEnabled;
    }

    public void setExpectContinueEnabled(boolean expectContinueEnabled) {
        this.expectContinueEnabled = expectContinueEnabled;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
