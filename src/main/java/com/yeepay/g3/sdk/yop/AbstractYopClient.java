package com.yeepay.g3.sdk.yop;

import com.yeepay.g3.sdk.yop.auth.YopSignOptions;
import com.yeepay.g3.sdk.yop.auth.YopV2Signer;
import com.yeepay.g3.sdk.yop.exception.YopClientException;
import com.yeepay.g3.sdk.yop.http.Headers;
import com.yeepay.g3.sdk.yop.http.HttpMethodName;
import com.yeepay.g3.sdk.yop.http.HttpUtils;
import com.yeepay.g3.sdk.yop.http.YopHttpClient;
import com.yeepay.g3.sdk.yop.http.handler.HttpResponseHandler;
import com.yeepay.g3.sdk.yop.internal.InternalRequest;
import com.yeepay.g3.sdk.yop.internal.RestartableInputStream;
import com.yeepay.g3.sdk.yop.model.AbstractYopRequest;
import com.yeepay.g3.sdk.yop.model.AbstractYopResponse;
import com.yeepay.g3.sdk.yop.utils.DateUtils;
import com.yeepay.g3.sdk.yop.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.annotation.ThreadSafe;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * title: YOP 平台客户端<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 11:38
 */
@ThreadSafe
public abstract class AbstractYopClient {

    public static final String DEFAULT_SERVICE_DOMAIN = "api.yeepay.com";

    public static final String URL_PREFIX = "yop-center";

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";

    private String serviceId;

    private URI endpoint;

    private YopHttpClient client;

    protected YopClientConfiguration config;

    /**
     * 返回结果处理器列表
     *
     * @see YopHttpClient#execute(InternalRequest, Class, HttpResponseHandler[])
     */
    private HttpResponseHandler[] responseHandlers;

    public AbstractYopClient(String serviceId, YopClientConfiguration config, HttpResponseHandler[] responseHandlers,
                             boolean isHttpAsyncPutEnabled) {
        this.serviceId = serviceId;
        this.config = config;
        this.endpoint = this.computeEndpoint();
        this.client = new YopHttpClient(config, new YopV2Signer(), isHttpAsyncPutEnabled);
        this.responseHandlers = responseHandlers;
    }

    public AbstractYopClient(String serviceId, YopClientConfiguration config, HttpResponseHandler[] responseHandlers) {
        this(serviceId, config, responseHandlers, false);
    }

    /**
     * 暂时还不支持指定机房的特性
     *
     * @return
     */
    public boolean isRegionSupported() {
        return false;
    }

    public String getServiceId() {
        return serviceId;
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public YopHttpClient getClient() {
        return client;
    }

    public void setClient(YopHttpClient client) {
        this.client = client;
    }

    public void shutdown() {
        this.client.shutdown();
    }

    protected InternalRequest createRequest(String resourceKey, AbstractYopRequest yopRequest,
                                            HttpMethodName httpMethod, String... pathVariables) {
        List<String> pathComponents = new ArrayList<String>();
        pathComponents.add(URL_PREFIX);

        // 追加 resourceKeys,pathVariables
        // 例如:/resourcekey1/resourcekey2/../pathVariable1/pathVariable2
        assertStringNotNullOrEmpty(resourceKey, "String resourceKey should not be null or empty");
        pathComponents.add(resourceKey);
        if (pathVariables != null) {
            for (String pathVariable : pathVariables) {
                pathComponents.add(pathVariable);
            }
        }

        // 构造请求实例
        InternalRequest internalRequest = new InternalRequest(httpMethod, HttpUtils.appendUri(this.getEndpoint(),
                pathComponents.toArray(new String[pathComponents.size()])));
        internalRequest.setCredentials(yopRequest.getRequestCredentials());

        // 设置待签名的请求头
        YopSignOptions options = YopSignOptions.DEFAULT_SIGN_OPTIONS;
        Set<String> headersToSign = new HashSet<String>();
        // headersToSign.add("content-type");
        headersToSign.add("host");
        headersToSign.add("x-yop-date");
        headersToSign.add("x-yop-request-id");
        options.setHeadersToSign(headersToSign);

        new YopV2Signer().sign(internalRequest, internalRequest.getCredentials(), options);

        if (httpMethod == HttpMethodName.POST
                || httpMethod == HttpMethodName.PUT) {
            fillRequestPayload(internalRequest, yopRequest);
        }

        return internalRequest;
    }

    protected InternalRequest fillRequestPayload(InternalRequest internalRequest, AbstractYopRequest yopRequest) {
        String strJson = JsonUtils.toJsonString(yopRequest);
        byte[] requestJson;
        try {
            requestJson = strJson.getBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new YopClientException("Unsupported encode.", e);
        }

        internalRequest.addHeader(Headers.CONTENT_LENGTH, String.valueOf(requestJson.length));
        internalRequest.addHeader(Headers.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        internalRequest.setContent(RestartableInputStream.wrap(requestJson));

        return internalRequest;
    }

    /**
     * 子类需要调用该方法实际发起远程服务调用
     */
    protected <T extends AbstractYopResponse> T invokeHttpClient(InternalRequest request, Class<T> responseClass) {
        if (!request.getHeaders().containsKey(Headers.CONTENT_TYPE)) {
            request.addHeader(Headers.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        }

        if (!request.getHeaders().containsKey(Headers.DATE)) {
            request.addHeader(Headers.DATE, DateUtils.formatRfc822Date(new Date()));
        }

        return this.client.execute(request, responseClass, this.responseHandlers);
    }

    /**
     * 计算服务访问点, 格式: http(s)://<Service ID>[.<Region>].api.yeepay.com
     */
    private URI computeEndpoint() {
        String endpoint = this.config.getEndpoint();
        try {
            if (endpoint == null) {
                String servicePrefix = StringUtils.isBlank(this.serviceId) ? "" : this.serviceId + ".";
                String port = 0 == config.getPort() ? "" : ":" + config.getPort();
                if (this.isRegionSupported()) {
                    endpoint = String.format("%s://%s%s.%s%S", this.config.getProtocol(), servicePrefix,
                            this.config.getRegion(), AbstractYopClient.DEFAULT_SERVICE_DOMAIN, port);
                } else {
                    endpoint = String.format("%s://%s%s%s", this.config.getProtocol(), servicePrefix,
                            AbstractYopClient.DEFAULT_SERVICE_DOMAIN, port);
                }
            }
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid endpoint." + endpoint, e);
        }
    }

    protected void assertStringNotNullOrEmpty(String parameterValue, String errorMessage) {
        if (parameterValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (parameterValue.isEmpty() || parameterValue.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }

    }

    protected void assertStringArrayNotNullOrEmpty(String[] parameterValue, String errorMessage) {
        if (parameterValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (parameterValue.length <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    protected void assertListNotNullOrEmpty(List<?> parameterValue, String errorMessage) {
        if (parameterValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (parameterValue.size() <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}
