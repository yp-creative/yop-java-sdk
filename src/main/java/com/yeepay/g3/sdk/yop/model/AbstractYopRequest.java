package com.yeepay.g3.sdk.yop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeepay.g3.sdk.yop.auth.YopCredentials;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.http.annotation.NotThreadSafe;

/**
 * title: Yop 请求 基类<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 15:30
 */
@NotThreadSafe
public abstract class AbstractYopRequest {

    /**
     * 请求凭证(可选)
     */
    private YopCredentials credentials;

    @JsonIgnore
    public YopCredentials getRequestCredentials() {
        return this.credentials;
    }

    @JsonIgnore
    public void setRequestCredentials(YopCredentials credentials) {
        this.credentials = credentials;
    }

    public abstract AbstractYopRequest withRequestCredentials(YopCredentials credentials);

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
