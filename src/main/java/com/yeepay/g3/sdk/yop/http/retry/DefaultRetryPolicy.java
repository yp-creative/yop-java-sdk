package com.yeepay.g3.sdk.yop.http.retry;

import com.yeepay.g3.sdk.yop.ErrorCode;
import com.yeepay.g3.sdk.yop.YopServiceException;
import com.yeepay.g3.sdk.yop.exception.YopClientException;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * title: 默认的重试策略<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 16:10
 */
public class DefaultRetryPolicy implements RetryPolicy {

    private static Logger logger = Logger.getLogger(DefaultRetryPolicy.class);

    /**
     * 一般异常延迟基准
     */
    protected static final int SCALE_FACTOR = 300;

    /**
     * 最大重试次数
     */
    private int maxErrorRetry;

    /**
     * 重试之前的最大等待时间
     */
    private long maxDelayInMillis;

    public DefaultRetryPolicy() {
        this(RetryPolicy.DEFAULT_MAX_ERROR_RETRY, RetryPolicy.DEFAULT_MAX_DELAY_IN_MILLIS);
    }

    public DefaultRetryPolicy(int maxErrorRetry, long maxDelayInMillis) {
        checkArgument(maxErrorRetry >= 0, "maxErrorRetry should be a non-negative.");
        checkArgument(maxDelayInMillis >= 0, "maxDelayInMillis should be a non-negative.");

        this.maxErrorRetry = maxErrorRetry;
        this.maxDelayInMillis = maxDelayInMillis;
    }

    @Override
    public int getMaxErrorRetry() {
        return this.maxErrorRetry;
    }

    @Override
    public long getMaxDelayInMillis() {
        return this.maxDelayInMillis;
    }

    /**
     * 计算距离下次重试需要的等待时间. 负数表示不需要重试
     *
     * @param exception        失败请求返回的异常
     * @param retriesAttempted 当前请求已经重试的次数
     */
    @Override
    public long getDelayBeforeNextRetryInMillis(YopClientException exception, int retriesAttempted) {
        if (!this.shouldRetry(exception, retriesAttempted)) {
            return -1;
        }
        if (retriesAttempted < 0) {
            return 0;
        }

        return calDelayBeforeNextRetryInMillis(retriesAttempted);
    }

    /**
     * Capped exponential backoff means
     *
     * @param retriesAttempted
     * @return
     */
    protected long calDelayBeforeNextRetryInMillis(int retriesAttempted) {
        return (1 << (retriesAttempted + 1)) * SCALE_FACTOR;
    }

    /**
     * 根据请求上下文判断是否需要重试. 下列情况将直接失败:
     * <ul>
     * <li>已经到达最大重试限制
     * <li>不包含可重试内容
     * <li>抛出任何的 RuntimeException 或者 Error
     * </ul>
     *
     * @param exception        失败请求返回的异常
     * @param retriesAttempted 当前请求已经重试的次数
     */
    protected boolean shouldRetry(YopClientException exception, int retriesAttempted) {
        // Always retry on client exceptions caused by IOException
        if (exception.getCause() instanceof IOException) {
            logger.debug("Retry for IOException.");
            return true;
        }

        if (exception instanceof YopServiceException) {
            YopServiceException e = (YopServiceException) exception;

            if (e.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                logger.debug("Retry for internal server error.");
                return true;
            }
            if (e.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                logger.debug("Retry for service unavailable.");
                return true;
            }

            String errorCode = e.getErrorCode();
            if (ErrorCode.REQUEST_EXPIRED.equals(errorCode)) {
                logger.debug("Retry for request expired.");
                return true;
            }
        }

        return false;
    }

}
