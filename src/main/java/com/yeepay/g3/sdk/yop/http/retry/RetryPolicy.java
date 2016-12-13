package com.yeepay.g3.sdk.yop.http.retry;

import com.yeepay.g3.sdk.yop.exception.YopClientException;

/**
 * title: 重试策略<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 16:31
 */
public interface RetryPolicy {

    /**
     * 默认最大重试次数
     */
    int DEFAULT_MAX_ERROR_RETRY = 3;

    /**
     * 重试之前的默认最大等待时间
     */
    int DEFAULT_MAX_DELAY_IN_MILLIS = 20 * 1000;

    /**
     * 获取最大尝试次数
     */
    int getMaxErrorRetry();

    /**
     * 获取最大重试间隔
     */
    long getMaxDelayInMillis();

    /**
     * 获取距离下次重试的时间间隔
     */
    long getDelayBeforeNextRetryInMillis(YopClientException exception, int retriesAttempted);

}
