package com.yeepay.g3.sdk.yop.auth;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: 访问凭证 的默认实现<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 15:35
 */
public class DefaultYopCredentials implements YopCredentials {

    /**
     * 获取访问标示
     */
    private final String accessKeyId;

    /**
     * 获取安全密钥
     */
    private final String secretKey;

    public DefaultYopCredentials(String accessKeyId, String secretKey) {
        checkNotNull(accessKeyId, "accessKeyId should not be null.");
        checkArgument(!accessKeyId.isEmpty(), "accessKeyId should not be empty.");
        checkNotNull(secretKey, "secretKey should not be null.");
        checkArgument(!secretKey.isEmpty(), "secretKey should not be empty.");

        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
    }

    @Override
    public String getAccessKeyId() {
        return accessKeyId;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
