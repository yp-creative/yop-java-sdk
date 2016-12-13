package com.yeepay.g3.sdk.yop.internal;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: <br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 10:38
 */
public class RestartableMultiByteArrayInputStream extends RestartableInputStream {

    private List<byte[]> byteArrayList;

    private long pos = 0;
    private int blockSize;
    private long length;

    public RestartableMultiByteArrayInputStream(List<byte[]> byteArrayList, long length) {
        checkNotNull(byteArrayList, "byteArrayList should not be null.");
        checkArgument(!byteArrayList.isEmpty(), "byteArrayList should not be empty.");
        long total = 0;
        for (byte[] byteArray : byteArrayList) {
            checkNotNull(byteArray, "byteArrayList should not contain null element.");
            checkArgument(byteArray.length > 0, "byteArrayList should not contain empty byte array.");
            total += byteArray.length;
        }
        checkArgument(total >= length,
                "The specified length(%s) is greater than the total length(%s) of elements in byteArrayList.",
                length, total);
        this.blockSize = byteArrayList.get(0).length;
        for (int i = 1; i < byteArrayList.size() - 1; ++i) {
            int len = byteArrayList.get(i).length;
            checkArgument(len == this.blockSize,
                    "All elements in byteArrayList except the last one should have the same length. " +
                            "The first element's length is %s but the %sth element's length is %s.",
                    this.blockSize, i, len);
        }
        this.byteArrayList = byteArrayList;
        this.length = length;
    }

    @Override
    public void restart() {
        this.pos = 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkNotNull(b, "b should not be null.");
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (this.pos == this.length) {
            return -1;
        }
        int count = 0;
        while (len > 0 && this.pos < this.length) {
            int index = (int) (this.pos / this.blockSize);
            int offset = (int) (this.pos % this.blockSize);
            byte[] byteArray = this.byteArrayList.get(index);
            int copyLength = byteArray.length - offset;
            if (copyLength > len) {
                copyLength = len;
            }
            System.arraycopy(byteArray, offset, b, off, copyLength);
            this.pos += copyLength;
            off += copyLength;
            len -= copyLength;
            count += copyLength;
        }
        return count;
    }

    @Override
    public int read() {
        if (this.pos == this.length) {
            return -1;
        }
        int index = (int) (this.pos / this.blockSize);
        int offset = (int) (this.pos % this.blockSize);
        ++this.pos;
        return this.byteArrayList.get(index)[offset] & 0xff;
    }

}
