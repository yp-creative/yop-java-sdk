package com.yeepay.g3.sdk.yop.http.retry.backoff;

/**
 * title: 基于指数的推迟重试策略 基类<br/>
 * description: 任务堆积方面: FullJitter ~ EqualJitter > Decorr, 但竞争时间方面 Decorr > FullJitter >> EqualJitter<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 11:08
 */
public abstract class ExpoBackoffRetryPolicy extends BackoffRetryPolicy {

}
