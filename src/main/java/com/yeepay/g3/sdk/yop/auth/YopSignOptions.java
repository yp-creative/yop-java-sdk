package com.yeepay.g3.sdk.yop.auth;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;
import java.util.Set;

/**
 * title: 请求报文的签名选项<br/>
 * description: <p>
 * There are 3 options available:
 * <table>
 * <tr>
 * <th>Option</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>headersToSign</td>
 * <td>The set of headers to be signed. If this option is not set or set to null, only the following headers are signed
 * <ul>
 * <li>Host</li>
 * <li>Content-Length</li>
 * <li>Content-Type</li>
 * <li>Content-MD5</li>
 * <li>All headers starts with "x-yop-"</li>
 * </ul>
 * </td>
 * </tr>
 * <tr>
 * <td>timestamp</td>
 * <td>The time when the signature was created. If this option is not set or set to null, the signer will use the time
 * when the sign method is invoked.</td>
 * </tr>
 * <tr>
 * <td>expirationInSeconds</td>
 * <td>The time until the signature will expire, which starts from the timestamp. By default, it is set to 1800 (half an
 * hour). </td>
 * </tr>
 * *
 * </table><br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 16:15
 */
public class YopSignOptions {

    /**
     * 默认签名选项: {headersToSign:null, timestamp:null, expirationInSeconds:1800}
     */
    public static final YopSignOptions DEFAULT_SIGN_OPTIONS = new YopSignOptions();

    public static final int DEFAULT_EXPIRATION_IN_SECONDS = 1800;

    /**
     * 待签名的报文头
     */
    private Set<String> headersToSign = null;

    /**
     * 签名创建时间
     */
    private Date timestamp = null;

    /**
     * 访问凭证有效期
     */
    private int expirationInSeconds = DEFAULT_EXPIRATION_IN_SECONDS;

    public Set<String> getHeadersToSign() {
        return headersToSign;
    }

    public void setHeadersToSign(Set<String> headersToSign) {
        this.headersToSign = headersToSign;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getExpirationInSeconds() {
        return expirationInSeconds;
    }

    public void setExpirationInSeconds(int expirationInSeconds) {
        this.expirationInSeconds = expirationInSeconds;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
