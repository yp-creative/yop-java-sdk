package com.yeepay.g3.sdk.yop.internal;

import com.yeepay.g3.sdk.yop.exception.YopClientException;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: 可重复读取但不可重置的 InputStream<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 10:37
 */
public class RestartableNonResettableInputStream extends RestartableInputStream {

    private byte[] buffer;

    private int offset = 0;

    private int length = 0;

    private boolean eof = false;

    private InputStream input;

    public RestartableNonResettableInputStream(InputStream input, int bufferSize) {
        checkNotNull(input, "input should not be null.");
        checkArgument(bufferSize >= 0, "bufferSize should not be negative: " + bufferSize);
        this.buffer = new byte[bufferSize];
        this.input = input;
        while (this.length < bufferSize) {
            int count;
            try {
                count = this.input.read(this.buffer, this.length, bufferSize - this.length);
            } catch (IOException e) {
                throw new YopClientException("Fail to read data from input.", e);
            }
            if (count < 0) {
                this.eof = true;
                break;
            }
            this.length += count;
        }
    }

    @Override
    public void restart() {
        if (this.buffer == null) {
            throw new IllegalStateException("Fail to restart. Input buffer exhausted.");
        }
        this.offset = 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkNotNull(b, "b should not be null.");
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.offset < this.length) {
            int copyLength = this.length - this.offset;
            if (copyLength > len) {
                copyLength = len;
            }
            System.arraycopy(this.buffer, this.offset, b, off, copyLength);
            this.offset += copyLength;
            return copyLength;
        }
        if (this.eof) {
            return -1;
        }
        int result = this.input.read(b, off, len);
        if (result < 0) {
            this.eof = true;
            return -1;
        }
        this.buffer = null;
        return result;
    }

    @Override
    public int read() throws IOException {
        if (this.offset < this.length) {
            return this.buffer[this.offset++] & 0xff;
        }
        if (this.eof) {
            return -1;
        }
        int result = this.input.read();
        if (result < 0) {
            this.eof = true;
            return -1;
        }
        this.buffer = null;
        return result;
    }

    @Override
    public void close() throws IOException {
        this.input.close();
    }

}
