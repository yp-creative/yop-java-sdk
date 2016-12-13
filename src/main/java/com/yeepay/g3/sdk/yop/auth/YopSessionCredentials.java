package com.yeepay.g3.sdk.yop.auth;

/**
 * title: 基于 Session 的访问凭证<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 15:34
 */
public interface YopSessionCredentials {

    /**
     * 获取 Session token
     *
     * @return
     */
    String getSessionToken();

}
