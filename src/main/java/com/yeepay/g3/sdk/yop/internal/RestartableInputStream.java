package com.yeepay.g3.sdk.yop.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * title: 可重复读取的 InputStream<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/19 10:24
 */
public abstract class RestartableInputStream extends InputStream {

    public abstract void restart();

    public static RestartableInputStream wrap(byte[] b) {
        ByteArrayInputStream input = new ByteArrayInputStream(b);
        input.mark(b.length);
        return new RestartableResettableInputStream(input);
    }

}
