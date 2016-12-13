package com.yeepay.g3.sdk.yop.error;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author wang.bao
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class YopSubError {

	private String code;

	private String message;

	public YopSubError() {
	}

	public YopSubError(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
