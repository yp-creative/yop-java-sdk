package com.yeepay.g3.sdk.yop.error;

import java.util.ArrayList;
import java.util.List;

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
public class YopError {

	private String code;

	private String message;

	private String solution;

	private List<YopSubError> subErrors = new ArrayList<YopSubError>();

	public YopError() {
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getSolution() {
		return solution;
	}

	public List<YopSubError> getSubErrors() {
		return this.subErrors;
	}

	public void setSubErrors(List<YopSubError> subErrors) {
		this.subErrors = subErrors;
	}

	public YopError addSubError(YopSubError subError) {
		this.subErrors.add(subError);
		return this;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
