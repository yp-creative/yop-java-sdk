package com.yeepay.g3.sdk.yop.http;

import com.yeepay.g3.sdk.yop.YopClientConfiguration;
import com.yeepay.g3.sdk.yop.auth.YopCredentials;
import com.yeepay.g3.sdk.yop.auth.YopSigner;
import com.yeepay.g3.sdk.yop.exception.YopClientException;
import com.yeepay.g3.sdk.yop.http.handler.HttpResponseHandler;
import com.yeepay.g3.sdk.yop.http.retry.RetryPolicy;
import com.yeepay.g3.sdk.yop.internal.InternalRequest;
import com.yeepay.g3.sdk.yop.model.AbstractYopResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: Yop HttpClient<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 11:40
 */
@ThreadSafe
public class YopHttpClient {

    private static final Logger LOGGER = Logger.getLogger(YopHttpClient.class);

    private static final Logger requestLogger = Logger.getLogger("com.yeepay.yop.request");

    protected CloseableHttpClient httpClient;

    protected YopClientConfiguration config;

    protected YopSigner signer;

    private HttpClientConnectionManager connectionManager;

    private RequestConfig.Builder requestConfigBuilder;
    private CredentialsProvider credentialsProvider;
    private HttpHost proxyHttpHost;

    private boolean isHttpAsyncPutEnabled = false;

    public YopHttpClient(YopClientConfiguration config, YopSigner signer) {
        checkNotNull(config, "config should not be null.");
        checkNotNull(signer, "signer should not be null.");
        this.config = config;
        this.signer = signer;
        this.connectionManager = this.createHttpClientConnectionManager();
        this.httpClient = this.createHttpClient(this.connectionManager);
        IdleConnectionReaper.registerConnectionManager(this.connectionManager);

        this.requestConfigBuilder = RequestConfig.custom();
        this.requestConfigBuilder.setConnectTimeout(config.getConnectionTimeoutInMillis());
        this.requestConfigBuilder.setStaleConnectionCheckEnabled(true);
        if (config.getLocalAddress() != null) {
            this.requestConfigBuilder.setLocalAddress(config.getLocalAddress());
        }

        // Proxy config
        String proxyHost = config.getProxyHost();
        int proxyPort = config.getProxyPort();
        if (proxyHost != null && proxyPort > 0) {
            this.proxyHttpHost = new HttpHost(proxyHost, proxyPort);
            this.requestConfigBuilder.setProxy(this.proxyHttpHost);

            this.credentialsProvider = new BasicCredentialsProvider();
            String proxyUsername = config.getProxyUsername();
            String proxyPassword = config.getProxyPassword();
            String proxyDomain = config.getProxyDomain();
            String proxyWorkstation = config.getProxyWorkstation();
            if (proxyUsername != null && proxyPassword != null) {
                this.credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
                        new NTCredentials(proxyUsername, proxyPassword,
                                proxyWorkstation, proxyDomain));
            }
        }
    }

    public YopHttpClient(YopClientConfiguration config, YopSigner signer, boolean isHttpAsyncPutEnabled) {
        this(config, signer);
        this.isHttpAsyncPutEnabled = isHttpAsyncPutEnabled;
    }

    /**
     * 执行请求并返回结果
     *
     * @param request          请求
     * @param responseClass    返回结果类
     * @param responseHandlers 返回结果处理器
     * @throws YopClientException  客户端发起请求或者处理返回结果时抛出的异常
     * @throws YopServiceException YOP 平台处理请求时抛出的异常
     */
    public <T extends AbstractYopResponse> T execute(InternalRequest request, Class<T> responseClass,
                                                     HttpResponseHandler[] responseHandlers) {
        // Apply whatever request options we know how to handle, such as user-agent.
        request.addHeader(Headers.USER_AGENT, this.config.getUserAgent());
        YopCredentials credentials = config.getCredentials();
        if (request.getCredentials() != null) {
            credentials = request.getCredentials();
        }

        long delayForNextRetryInMillis = 0;
        for (int attempt = 1; ; ++attempt) {
            HttpRequestBase httpRequest = null;
            CloseableHttpResponse httpResponse = null;
            CloseableHttpAsyncClient httpAsyncClient = null;
            try {
                if (credentials != null) {
                    this.signer.sign(request, credentials);
                }

                requestLogger.debug("Sending Request: " + request);

                httpRequest = this.createHttpRequest(request);

                HttpContext httpContext = this.createHttpContext(request);

                if (this.isHttpAsyncPutEnabled && httpRequest.getMethod().equals("PUT")) {
                    httpAsyncClient = this.createHttpAsyncClient(this.createNHttpClientConnectionManager());
                    httpAsyncClient.start();
                    Future<HttpResponse> future = httpAsyncClient.execute(HttpAsyncMethods.create(httpRequest),
                            new BasicAsyncResponseConsumer(),
                            httpContext, null);
                    httpResponse = new YopCloseableHttpResponse(future.get());
                } else {
                    httpResponse = this.httpClient.execute(httpRequest, httpContext);
                }
                HttpUtils.printRequest(httpRequest);
                YopHttpResponse yopHttpResponse = new YopHttpResponse(httpResponse);

                T response = responseClass.newInstance();
                for (HttpResponseHandler handler : responseHandlers) {
                    if (handler.handle(yopHttpResponse, response)) {
                        break;
                    }
                }

                return response;
            } catch (Exception e) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Unable to execute HTTP request", e);
                }

                YopClientException yop;
                if (e instanceof YopClientException) {
                    yop = (YopClientException) e;
                } else {
                    yop = new YopClientException("Unable to execute HTTP request", e);
                }
                delayForNextRetryInMillis =
                        this.getDelayBeforeNextRetryInMillis(httpRequest, yop, attempt, this.config.getRetryPolicy());
                if (delayForNextRetryInMillis < 0) {
                    throw yop;
                }

                LOGGER.debug("Retriable error detected, will retry in " + delayForNextRetryInMillis + " ms, attempt number: " + attempt);
                try {
                    Thread.sleep(delayForNextRetryInMillis);
                } catch (InterruptedException e1) {
                    throw new YopClientException("Delay interrupted", e1);
                }
                if (request.getContent() != null) {
                    request.getContent().restart();
                }
                if (httpResponse != null) {
                    try {
                        HttpEntity entity = httpResponse.getEntity();
                        if (entity != null && entity.isStreaming()) {
                            final InputStream inputStream = entity.getContent();
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        }
                    } catch (IOException e1) {
                        LOGGER.debug("Fail to consume entity.", e1);
                        try {
                            httpResponse.close();
                        } catch (IOException e2) {
                            LOGGER.debug("Fail to close connection.", e2);
                        }
                    }
                }
            } finally {
                try {
                    if (httpAsyncClient != null) {
                        httpAsyncClient.close();
                    }
                } catch (IOException e) {
                    LOGGER.debug("Fail to close HttpAsyncClient", e);
                }
            }
        }
    }

    public void shutdown() {
        IdleConnectionReaper.removeConnectionManager(this.connectionManager);
        this.connectionManager.shutdown();
    }

    protected long getDelayBeforeNextRetryInMillis(HttpRequestBase method, YopClientException exception, int attempt,
                                                   RetryPolicy retryPolicy) {
        int retries = attempt - 1;
        int maxErrorRetry = retryPolicy.getMaxErrorRetry();
        if (retries >= maxErrorRetry) {
            return -1;
        }

        // Never retry on requests containing non-repeatable entity
        if (method instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) method).getEntity();
            if (entity != null && !entity.isRepeatable()) {
                LOGGER.debug("Entity not repeatable, stop retrying");
                return -1;
            }
        }

        return Math.min(retryPolicy.getMaxDelayInMillis(),
                retryPolicy.getDelayBeforeNextRetryInMillis(exception, retries));
    }

    /**
     * 创建同步连接管理器
     *
     * @return 同步连接管理器
     */
    private HttpClientConnectionManager createHttpClientConnectionManager() {
        ConnectionSocketFactory socketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory;
        try {
            sslSocketFactory = new SSLConnectionSocketFactory(SSLContext.getDefault(),
                    SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException e) {
            throw new YopClientException("Fail to create SSLConnectionSocketFactory", e);
        }

        Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create().register(Protocol.HTTP.toString(), socketFactory)
                        .register(Protocol.HTTPS.toString(), sslSocketFactory).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setDefaultMaxPerRoute(this.config.getMaxConnections());
        connectionManager
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(this.config.getSocketTimeoutInMillis())
                        .setTcpNoDelay(true).build());
        connectionManager.setMaxTotal(this.config.getMaxConnections());
        return connectionManager;
    }

    /**
     * 创建异步连接管理器
     *
     * @return 异步连接管理器
     * @throws IOReactorException
     */
    protected NHttpClientConnectionManager createNHttpClientConnectionManager() throws IOReactorException {
        ConnectingIOReactor ioReactor =
                new DefaultConnectingIOReactor(IOReactorConfig.custom()
                        .setSoTimeout(this.config.getSocketTimeoutInMillis()).setTcpNoDelay(true).build());
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connectionManager.setDefaultMaxPerRoute(this.config.getMaxConnections());
        connectionManager.setMaxTotal(this.config.getMaxConnections());
        return connectionManager;
    }

    private CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager) {
        HttpClientBuilder builder =
                HttpClients.custom().setConnectionManager(connectionManager).disableAutomaticRetries();

        int socketBufferSizeInBytes = this.config.getSocketBufferSizeInBytes();
        if (socketBufferSizeInBytes > 0) {
            builder.setDefaultConnectionConfig(
                    ConnectionConfig.custom().setBufferSize(socketBufferSizeInBytes).build());
        }

        return builder.build();
    }

    protected CloseableHttpAsyncClient createHttpAsyncClient(NHttpClientConnectionManager connectionManager) {
        HttpAsyncClientBuilder builder = HttpAsyncClients.custom().setConnectionManager(connectionManager);

        int socketBufferSizeInBytes = this.config.getSocketBufferSizeInBytes();
        if (socketBufferSizeInBytes > 0) {
            builder.setDefaultConnectionConfig(
                    ConnectionConfig.custom().setBufferSize(socketBufferSizeInBytes).build());
        }
        return builder.build();
    }

    protected HttpRequestBase createHttpRequest(InternalRequest request) {
        String uri = request.getUri().toASCIIString();
        String encodedParams = HttpUtils.getCanonicalQueryString(request.getParameterMap(), false);

        if (encodedParams.length() > 0) {
            uri += "?" + encodedParams;
        }

        // http method
        HttpRequestBase httpRequest;
        long contentLength = -1;
        String contentLengthString = request.getHeaders().get(Headers.CONTENT_LENGTH);
        if (contentLengthString != null) {
            contentLength = Long.parseLong(contentLengthString);
        }
        if (request.getHttpMethod() == HttpMethodName.GET) {
            httpRequest = new HttpGet(uri);
        } else if (request.getHttpMethod() == HttpMethodName.PUT) {
            HttpPut putMethod = new HttpPut(uri);
            httpRequest = putMethod;
            if (request.getContent() != null) {
                putMethod.setEntity(new InputStreamEntity(request.getContent(), contentLength));
            }
        } else if (request.getHttpMethod() == HttpMethodName.POST) {
            HttpPost postMethod = new HttpPost(uri);
            httpRequest = postMethod;
            if (request.getContent() != null) {
                postMethod.setEntity(new InputStreamEntity(request.getContent(), contentLength));
            }
        } else if (request.getHttpMethod() == HttpMethodName.DELETE) {
            httpRequest = new HttpDelete(uri);
        } else if (request.getHttpMethod() == HttpMethodName.HEAD) {
            httpRequest = new HttpHead(uri);
        } else {
            throw new YopClientException("Unknown HTTP method name: " + request.getHttpMethod());
        }

        // http header
        httpRequest.addHeader(Headers.HOST, HttpUtils.generateHostHeader(request.getUri()));
        for (Entry<String, String> entry : request.getHeaders().entrySet()) {
            /*
             * HttpClient4 fills in the Content-Length header and complains if it's already present, so we skip it here.
             * We also skip the Host header to avoid sending it twice, which will interfere with some signing schemes.
             */
            if (entry.getKey().equalsIgnoreCase(Headers.CONTENT_LENGTH)
                    || entry.getKey().equalsIgnoreCase(Headers.HOST)) {
                continue;
            }

            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }

        checkNotNull(httpRequest.getFirstHeader(Headers.CONTENT_TYPE), Headers.CONTENT_TYPE + " not set");
        return httpRequest;
    }

    protected HttpClientContext createHttpContext(InternalRequest request) {
        HttpClientContext context = HttpClientContext.create();
        context.setRequestConfig(this.requestConfigBuilder.setExpectContinueEnabled(request.isExpectContinueEnabled())
                .build());
        if (this.credentialsProvider != null) {
            context.setCredentialsProvider(this.credentialsProvider);
        }
        if (this.config.isProxyPreemptiveAuthenticationEnabled()) {
            AuthCache authCache = new BasicAuthCache();
            authCache.put(this.proxyHttpHost, new BasicScheme());
            context.setAuthCache(authCache);
        }
        return context;
    }

    @Override
    protected void finalize() throws Throwable {
        this.shutdown();
        super.finalize();
    }

}
