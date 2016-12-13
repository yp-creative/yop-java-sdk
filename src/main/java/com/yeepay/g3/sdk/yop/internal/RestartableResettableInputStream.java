package com.yeepay.g3.sdk.yop.internal;

import com.yeepay.g3.sdk.yop.exception.YopClientException;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: 可重复读取且可重置的 InputStream<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 10:30
 */
public class RestartableResettableInputStream extends RestartableInputStream {

    private InputStream input;

    public RestartableResettableInputStream(InputStream input) {
        checkNotNull(input, "input should not be null.");
        checkArgument(input.markSupported(), "input does not support mark.");
        this.input = input;
    }

    @Override
    public void restart() {
        try {
            this.input.reset();
        } catch (IOException e) {
            throw new YopClientException("Fail to reset the underlying input stream.", e);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.input.read(b, off, len);
    }

    @Override
    public int read() throws IOException {
        return this.input.read();
    }

    @Override
    public void close() throws IOException {
        this.input.close();
    }

}
