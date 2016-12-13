package com.yeepay.g3.sdk.yop.model;

import com.yeepay.g3.sdk.yop.YopResponseMetadata;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * title: Yop 响应 基类<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 15:31
 */
public class AbstractYopResponse {

    protected YopResponseMetadata metadata = new YopResponseMetadata();

    public YopResponseMetadata getMetadata() {
        return this.metadata;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
