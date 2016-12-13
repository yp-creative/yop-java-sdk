package com.yeepay.g3.sdk.yop.auth;

import com.yeepay.g3.sdk.yop.internal.InternalRequest;

/**
 * title: 请求报文的签名器<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 16:14
 */
public interface YopSigner {

    /**
     * 使用默认签名选项进行签名
     *
     * @param request     请求报文
     * @param credentials 访问凭证
     */
    void sign(InternalRequest request, YopCredentials credentials);

    /**
     * 使用自定义签名选项进行签名
     *
     * @param request     请求报文
     * @param credentials 访问凭证
     * @param options     自定义签名选项
     */
    void sign(InternalRequest request, YopCredentials credentials, YopSignOptions options);

}
