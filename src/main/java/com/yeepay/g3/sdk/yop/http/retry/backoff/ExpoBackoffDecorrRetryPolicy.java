package com.yeepay.g3.sdk.yop.http.retry.backoff;

import com.yeepay.g3.sdk.yop.http.retry.DefaultRetryPolicy;

import java.util.Random;

/**
 * title: 去除相关性的指数推迟重试策略(类似于 FullJitter, 但可有有效避免等待时间过短的问题.)<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 10:36
 */
public final class ExpoBackoffDecorrRetryPolicy extends ExpoBackoffRetryPolicy {

    private static final Random rand = new Random();

    @Override
    protected long calDelayBeforeNextRetryInMillis(int retriesAttempted) {
        long temp = super.calDelayBeforeNextRetryInMillis(retriesAttempted);
        return DefaultRetryPolicy.SCALE_FACTOR + rand.nextInt((int) temp * 3 - DefaultRetryPolicy.SCALE_FACTOR + 1);
    }

}
