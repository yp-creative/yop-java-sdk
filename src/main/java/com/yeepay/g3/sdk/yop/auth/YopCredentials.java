package com.yeepay.g3.sdk.yop.auth;

/**
 * title: 访问凭证<br/>
 * description: 需要定时刷新, 用于每次请求的合法性鉴权<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 15:29
 */
public interface YopCredentials {

    /**
     * 获取访问标示
     *
     * @return
     */
    String getAccessKeyId();

    /**
     * 获取安全密钥
     *
     * @return
     */
    String getSecretKey();

}
