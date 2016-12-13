package com.yeepay.g3.sdk.yop.http.retry.backoff;

import java.util.Random;

/**
 * title: 带完全抖动的指数推迟重试策略<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 10:15
 */
public final class ExpoBackoffFullJitterRetryPolicy extends ExpoBackoffRetryPolicy {

    private Random rand = new Random();

    @Override
    protected long calDelayBeforeNextRetryInMillis(int retriesAttempted) {
        return rand.nextInt((int) super.calDelayBeforeNextRetryInMillis(retriesAttempted) + 1);
    }

}
