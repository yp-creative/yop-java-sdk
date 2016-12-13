package com.yeepay.g3.sdk.yop;

import com.google.common.base.Joiner;
import com.yeepay.g3.sdk.yop.auth.YopCredentials;
import com.yeepay.g3.sdk.yop.client.YopConstants;
import com.yeepay.g3.sdk.yop.http.Protocol;
import com.yeepay.g3.sdk.yop.http.retry.DefaultRetryPolicy;
import com.yeepay.g3.sdk.yop.http.retry.RetryPolicy;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.http.annotation.NotThreadSafe;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: 客户端配置<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 15:48
 */
@NotThreadSafe
public class YopClientConfiguration {

    /**
     * 默认连接超时时间
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT_IN_MILLIS = 50 * 1000;

    /**
     * 默认读取链接超时
     */
    public static final int DEFAULT_SOCKET_TIMEOUT_IN_MILLIS = 50 * 1000;

    /**
     * 默认最大连接数
     */
    public static final int DEFAULT_MAX_CONNECTIONS = 50;

    /**
     * 默认的 User-Agent
     */
    public static final String DEFAULT_USER_AGENT;

    /**
     * 默认重试策略
     */
    public static final DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy();

    /**
     * 默认 region
     */
    public static final Region DEFAULT_REGION = Region.CN_N1;

    /**
     * 默认协议
     */
    public static Protocol DEFAULT_PROTOCOL = Protocol.HTTP;

    /**
     * User-Agent
     */
    private String userAgent = YopClientConfiguration.DEFAULT_USER_AGENT;

    /**
     * 请求失败时的重试策略
     */
    private RetryPolicy retryPolicy = YopClientConfiguration.DEFAULT_RETRY_POLICY;

    /**
     * 绑定本机地址(可选)
     */
    private InetAddress localAddress;

    /**
     * 使用协议 (HTTP/HTTPS)
     */
    private Protocol protocol = Protocol.HTTP;

    /**
     * 定义服务端口(可选)
     */
    private int port;

    /**
     * 最大连接数
     */
    private int maxConnections = YopClientConfiguration.DEFAULT_MAX_CONNECTIONS;

    /**
     * 读取链接超时(SO_TIMEOUT)
     */
    private int socketTimeoutInMillis = YopClientConfiguration.DEFAULT_SOCKET_TIMEOUT_IN_MILLIS;

    /**
     * 连接超时时间
     */
    private int connectionTimeoutInMillis = YopClientConfiguration.DEFAULT_CONNECTION_TIMEOUT_IN_MILLIS;

    /**
     * TCP 缓存大小 (可选)
     */
    private int socketBufferSizeInBytes = 0;

    /**
     * 服务访问点
     */
    private String endpoint = null;

    /**
     * 服务区块
     * <p>
     * 用于在不指定服务访问点的情况下, 自动构建服务访问点
     */
    private Region region = DEFAULT_REGION;

    /**
     * 访问凭证
     */
    private YopCredentials credentials = null;

    // 网络代理配置(可选)

    /**
     * 代理主机地址(可选)
     */
    private String proxyHost = null;

    /**
     * 代理主机端口(可选)
     */
    private int proxyPort = -1;

    /**
     * 代理用户名(可选)
     */
    private String proxyUsername = null;

    /**
     * 代理密码(可选)
     */
    private String proxyPassword = null;

    /**
     * 代理域(可选)
     */
    private String proxyDomain = null;

    /**
     * 代理工作组(可选)
     */
    private String proxyWorkstation = null;

    /**
     * Whether to enable proxy preemptive authentication. If it is true, the client will send the basic authentication
     * response even before the proxy server gives an unauthorized response in certain situations, thus reducing the
     * overhead of making the connection.
     */
    private boolean proxyPreemptiveAuthenticationEnabled;

    static {
        // 初始化 DEFAULT_USER_AGENT
        String language = System.getProperty("user.language");
        if (language == null) {
            language = "";
        }
        String region = System.getProperty("user.region");
        if (region == null) {
            region = "";
        }
        DEFAULT_USER_AGENT = Joiner.on('/').join("yop-sdk-java", YopConstants.CLIENT_VERSION,
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.version"),
                System.getProperty("java.version"), language, region)
                .replace(' ', '_');
    }

    public YopClientConfiguration() {
        // do nothing
    }

    public YopClientConfiguration(YopClientConfiguration other) {
        this.connectionTimeoutInMillis = other.connectionTimeoutInMillis;
        this.maxConnections = other.maxConnections;
        this.retryPolicy = other.retryPolicy;
        this.localAddress = other.localAddress;
        this.protocol = other.protocol;
        this.port = other.port;
        this.socketTimeoutInMillis = other.socketTimeoutInMillis;
        this.userAgent = other.userAgent;
        this.socketBufferSizeInBytes = other.socketBufferSizeInBytes;
        this.endpoint = other.endpoint;
        this.region = other.region;
        this.credentials = other.credentials;

        this.proxyDomain = other.proxyDomain;
        this.proxyHost = other.proxyHost;
        this.proxyPassword = other.proxyPassword;
        this.proxyPort = other.proxyPort;
        this.proxyUsername = other.proxyUsername;
        this.proxyWorkstation = other.proxyWorkstation;
        this.proxyPreemptiveAuthenticationEnabled = other.proxyPreemptiveAuthenticationEnabled;
    }

    public YopClientConfiguration(YopClientConfiguration other, String endpoint) {
        this(other);
        this.endpoint = endpoint;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        if (userAgent == null) {
            this.userAgent = DEFAULT_USER_AGENT;
        } else if (userAgent.endsWith(DEFAULT_USER_AGENT)) {
            this.userAgent = userAgent;
        } else {
            this.userAgent = userAgent + ", " + DEFAULT_USER_AGENT;
        }
    }

    public YopClientConfiguration withUserAgent(String userAgent) {
        setUserAgent(userAgent);
        return this;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(InetAddress localAddress) {
        this.localAddress = localAddress;
    }

    public YopClientConfiguration withLocalAddress(InetAddress localAddress) {
        this.setLocalAddress(localAddress);
        return this;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = null == protocol ? YopClientConfiguration.DEFAULT_PROTOCOL : protocol;
    }

    public YopClientConfiguration withProtocol(Protocol protocol) {
        this.setProtocol(protocol);
        return this;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public YopClientConfiguration withPort(int port) {
        this.setPort(port);
        return this;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        checkArgument(maxConnections >= 0, "maxConnections should not be negative.");
        this.maxConnections = maxConnections;
    }

    public YopClientConfiguration withMaxConnections(int maxConnections) {
        this.setMaxConnections(maxConnections);
        return this;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = null == retryPolicy ? DEFAULT_RETRY_POLICY : retryPolicy;
    }

    public YopClientConfiguration withRetryPolicy(RetryPolicy retryPolicy) {
        this.setRetryPolicy(retryPolicy);
        return this;
    }

    public int getSocketTimeoutInMillis() {
        return socketTimeoutInMillis;
    }

    public void setSocketTimeoutInMillis(int socketTimeoutInMillis) {
        checkArgument(socketTimeoutInMillis >= 0, "socketTimeoutInMillis should not be negative.");
        this.socketTimeoutInMillis = socketTimeoutInMillis;
    }

    public YopClientConfiguration withSocketTimeoutInMillis(int socketTimeoutInMillis) {
        this.setSocketTimeoutInMillis(socketTimeoutInMillis);
        return this;
    }

    public int getConnectionTimeoutInMillis() {
        return connectionTimeoutInMillis;
    }

    public void setConnectionTimeoutInMillis(int connectionTimeoutInMillis) {
        checkArgument(connectionTimeoutInMillis >= 0, "connectionTimeoutInMillis should not be negative.");
        this.connectionTimeoutInMillis = connectionTimeoutInMillis;
    }

    public YopClientConfiguration withConnectionTimeoutInMillis(int connectionTimeoutInMillis) {
        this.setConnectionTimeoutInMillis(connectionTimeoutInMillis);
        return this;
    }

    public int getSocketBufferSizeInBytes() {
        return socketBufferSizeInBytes;
    }

    public void setSocketBufferSizeInBytes(int socketBufferSizeInBytes) {
        this.socketBufferSizeInBytes = socketBufferSizeInBytes;
    }

    public YopClientConfiguration withSocketBufferSizeInBytes(int socketBufferSizeInBytes) {
        this.setSocketBufferSizeInBytes(socketBufferSizeInBytes);
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        checkNotNull(endpoint, "endpoint should not be null.");

        try {
            new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid endpoint.", e);
        }
        this.endpoint = endpoint;
    }

    public YopClientConfiguration withEndpoint(String endpoint) {
        this.setEndpoint(endpoint);
        return this;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = null == region ? DEFAULT_REGION : region;
    }

    public YopClientConfiguration withRegion(Region region) {
        this.setRegion(region);
        return this;
    }

    public YopCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(YopCredentials credentials) {
        checkNotNull(credentials, "credentials should not be null.");
        this.credentials = credentials;
    }

    public YopClientConfiguration withCredentials(YopCredentials credentials) {
        this.setCredentials(credentials);
        return this;
    }

    // 代理

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public YopClientConfiguration withProxyHost(String proxyHost) {
        this.setProxyHost(proxyHost);
        return this;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public YopClientConfiguration withProxyPort(int proxyPort) {
        this.setProxyPort(proxyPort);
        return this;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public YopClientConfiguration withProxyUsername(String proxyUsername) {
        this.setProxyUsername(proxyUsername);
        return this;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public YopClientConfiguration withProxyPassword(String proxyPassword) {
        this.setProxyPassword(proxyPassword);
        return this;
    }

    public String getProxyDomain() {
        return proxyDomain;
    }

    public void setProxyDomain(String proxyDomain) {
        this.proxyDomain = proxyDomain;
    }

    public YopClientConfiguration withProxyDomain(String proxyDomain) {
        this.setProxyDomain(proxyDomain);
        return this;
    }

    public String getProxyWorkstation() {
        return proxyWorkstation;
    }

    public void setProxyWorkstation(String proxyWorkstation) {
        this.proxyWorkstation = proxyWorkstation;
    }

    public YopClientConfiguration withProxyWorkstation(String proxyWorkstation) {
        this.setProxyWorkstation(proxyWorkstation);
        return this;
    }

    public boolean isProxyPreemptiveAuthenticationEnabled() {
        return proxyPreemptiveAuthenticationEnabled;
    }

    public void setProxyPreemptiveAuthenticationEnabled(boolean proxyPreemptiveAuthenticationEnabled) {
        this.proxyPreemptiveAuthenticationEnabled = proxyPreemptiveAuthenticationEnabled;
    }

    public YopClientConfiguration withProxyPreemptiveAuthenticationEnabled(boolean proxyPreemptiveAuthenticationEnabled) {
        this.setProxyPreemptiveAuthenticationEnabled(proxyPreemptiveAuthenticationEnabled);
        return this;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
