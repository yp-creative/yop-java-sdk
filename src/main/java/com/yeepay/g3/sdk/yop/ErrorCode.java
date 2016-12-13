package com.yeepay.g3.sdk.yop;

/**
 * title: 客户端错误码<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 09:06
 */
public enum ErrorCode {

    ACCESS_DENIED("AccessDenied"),

    INTERNAL_ERROR("InternalError"),
    INVALID_ACCESS_KEY_ID("InvalidAccessKeyId"),
    INVALID_HTTP_AUTH_HEADER("InvalidHTTPAuthHeader"),
    INVALID_HTTP_REQUEST("InvalidHTTPRequest"),
    INVALID_URI("InvalidURI"),
    INVALID_VERSION("InvalidVersion"),

    PRECONDITION_FAILED("PreconditionFailed"),
    REQUEST_EXPIRED("RequestExpired"),
    SIGNATURE_DOES_NOT_MATCH("SignatureDoesNotMatch"),

    INAPPROPRIATE_JSON("InappropriateJSON"),
    MALFORMED_JSON("MalformedJSON"),

    OPT_IN_REQUIRED("OptInRequired");

    private String code;

    ErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

    public boolean equals(String code) {
        return this.code.equals(code);
    }

}
