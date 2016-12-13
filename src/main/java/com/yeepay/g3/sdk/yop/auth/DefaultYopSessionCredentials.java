package com.yeepay.g3.sdk.yop.auth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: 基于 Session 的访问凭证 的默认实现<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 16:09
 */
public class DefaultYopSessionCredentials extends DefaultYopCredentials implements YopSessionCredentials {

    /**
     * session token
     */
    private final String sessionToken;

    public DefaultYopSessionCredentials(String accessKeyId, String secretKey, String sessionToken) {
        super(accessKeyId, secretKey);

        checkNotNull(sessionToken, "session token should not be null.");
        checkArgument(!sessionToken.isEmpty(), "session token should not be empty.");
        this.sessionToken = sessionToken;
    }

    @Override
    public String getSessionToken() {
        return this.sessionToken;
    }

}
