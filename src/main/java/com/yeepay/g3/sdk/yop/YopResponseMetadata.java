package com.yeepay.g3.sdk.yop;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

/**
 * title: 响应元数据<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/22 15:55
 */
public class YopResponseMetadata {

    private String yopRequestId;

    private String yopContentSha256;

    private String contentDisposition;

    private String transferEncoding;

    private String contentEncoding;

    private long contentLength = -1;

    private String contentMd5;

    private String contentRange;

    private String contentType;

    private Date date;

    private String eTag;

    private Date expires;

    private Date lastModified;

    private String server;

    private String location;

    public String getYopRequestId() {
        return this.yopRequestId;
    }

    public void setYopRequestId(String yopRequestId) {
        this.yopRequestId = yopRequestId;
    }

    public String getYopContentSha256() {
        return this.yopContentSha256;
    }

    public void setYopContentSha256(String yopContentSha256) {
        this.yopContentSha256 = yopContentSha256;
    }

    public String getContentDisposition() {
        return this.contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentMd5() {
        return this.contentMd5;
    }

    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }

    public String getContentRange() {
        return this.contentRange;
    }

    public void setContentRange(String contentRange) {
        this.contentRange = contentRange;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getETag() {
        return this.eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public Date getExpires() {
        return this.expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTransferEncoding() {
        return transferEncoding;
    }

    public void setTransferEncoding(String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
