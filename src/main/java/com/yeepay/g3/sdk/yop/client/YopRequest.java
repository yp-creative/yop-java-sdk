package com.yeepay.g3.sdk.yop.client;

import com.yeepay.g3.sdk.yop.enums.FormatType;
import com.yeepay.g3.sdk.yop.internal.InternalRequest;
import com.yeepay.g3.sdk.yop.utils.Assert;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <pre>
 * 每个请求对应一个ClientRequest对象
 * </pre>
 *
 * @author wang.bao
 * @version 1.0
 */
public class YopRequest extends InternalRequest {
	private Logger logger = Logger.getLogger(getClass());

	private FormatType format = FormatType.json;

	private String method;

	private String locale = "zh_CN";

	private String version = "1.0";

	private String signAlg = YopConstants.ALG_SHA1;

	/**
	 * 商户编号，易宝商户可不注册开放应用(获取appKey)也可直接调用API
	 */
	private String customerNo;

	private MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();

    private List<String> ignoreSignParams = new ArrayList<String>(Arrays.asList(YopConstants.SIGN));

	/**
	 * 报文是否加密，如果请求加密，则响应也加密，需做解密处理
	 */
	private boolean encrypt = false;

	/**
	 * 业务结果是否签名，默认不签名
	 */
	private boolean signRet = false;

    /**
     * 连接超时时间
     */
    private Integer connectTimeout = 30000;

    /**
     * 读取返回结果超时
     */
    private Integer readTimeout = 60000;

	/**
	 * 临时变量，避免多次判断
	 */
	private transient boolean isRest = true;

	/**
	 * 可支持不同请求使用不同的appKey及secretKey
	 */
	private String appKey;

	/**
	 * 可支持不同请求使用不同的appKey及secretKey,secretKey只用于本地签名，不会被提交
	 */
	private String secretKey;

	/**
	 * 可支持不同请求使用不同的appKey及secretKey、serverRoot,secretKey只用于本地签名，不会被提交
	 */
	private String serverRoot;

	/**
	 * 临时变量，请求绝对路径
	 */
	private String absoluteURL;

	public YopRequest() {
		this.appKey = YopConfig.getAppKey();
		this.secretKey = YopConfig.getSecret();
		this.serverRoot = YopConfig.getServerRoot();
		paramMap.set(YopConstants.APP_KEY, YopConfig.getAppKey());
		paramMap.set(YopConstants.FORMAT, format.name());
		paramMap.set(YopConstants.VERSION, version);
		paramMap.set(YopConstants.LOCALE, locale);
		paramMap.set(YopConstants.TIMESTAMP,
				String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * 同一个工程内部可支持多个开放应用发起调用
	 */
	public YopRequest(String appKey, String secretKey) {
		this();
		this.appKey = appKey;
		this.secretKey = secretKey;
		paramMap.set(YopConstants.APP_KEY, appKey);
	}

	/**
	 * 同一个工程内部可支持多个开放应用发起调用，且支持调不同的服务器
	 */
	public YopRequest(String appKey, String secretKey, String serverRoot) {
		this(appKey, secretKey);
		this.serverRoot = serverRoot;
	}

	public YopRequest addParam(String paramName, Object paramValue) {
		addParam(paramName, paramValue, false);
		return this;
	}

	/**
	 * 添加参数
	 *
	 * @param paramName
	 *            参数名
	 * @param paramValue
	 *            参数值：如果为集合或数组类型，则自动拆解，最终想作为数组提交到服务端
	 * @param ignoreSign
	 *            是否忽略签名
	 * @return
	 */
	public YopRequest addParam(String paramName, Object paramValue,
			boolean ignoreSign) {
		Assert.hasText(paramName, "参数名不能为空");
		if (paramValue == null
				|| ((paramValue instanceof String) && StringUtils
						.isBlank((String) paramValue))
				|| ((paramValue instanceof Collection<?>) && ((Collection<?>) paramValue)
						.isEmpty())) {
			logger.warn("参数" + paramName + "为空，忽略");
			return this;
		}
		if (YopConstants.isProtectedKey(paramName)) {
			paramMap.set(paramName, paramValue.toString().trim());
			return this;
		}
		if (paramValue instanceof Collection<?>) {
			// 集合类
			for (Object o : (Collection<?>) paramValue) {
				if (o != null) {
					paramMap.add(paramName, o.toString().trim());
				}
			}
		} else if (paramValue.getClass().isArray()) {
			// 数组
			int len = Array.getLength(paramValue);
			for (int i = 0; i < len; i++) {
				Object o = Array.get(paramValue, i);
				if (o != null) {
					paramMap.add(paramName, o.toString().trim());
				}
			}
		} else {
			paramMap.add(paramName, paramValue.toString().trim());
		}

		if (ignoreSign) {
			ignoreSignParams.add(paramName);
		}
		return this;
	}

	public List<String> getParam(String key) {
		return paramMap.get(key);
	}

	public String getParamValue(String key) {
		return StringUtils.join(paramMap.get(key), ",");
	}

	public String removeParam(String key) {
		return StringUtils.join(paramMap.remove(key), ",");
	}

	public MultiValueMap<String, String> getParams() {
		return paramMap;
	}

	public List<String> getIgnoreSignParams() {
		return ignoreSignParams;
	}

	public void setFormat(FormatType format) {
		Assert.notNull(format);
		this.format = format;
		paramMap.set(YopConstants.FORMAT, this.format.name());
	}

	public void setLocale(String locale) {
		this.locale = locale;
		paramMap.set(YopConstants.LOCALE, this.locale);
	}

	public void setVersion(String version) {
		this.version = version;
		paramMap.set(YopConstants.VERSION, this.version);
	}

	public void setMethod(String method) {
		this.method = method;
		paramMap.set(YopConstants.METHOD, this.method);
	}

	public String getMethod() {
		return method;
	}

	public FormatType getFormat() {
		return format;
	}

	public String getLocale() {
		return locale;
	}

	public String getVersion() {
		return version;
	}

	public String getSignAlg() {
		return signAlg;
	}

	public void setSignAlg(String signAlg) {
		this.signAlg = signAlg;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
		paramMap.set(YopConstants.CUSTOMER_NO, this.customerNo);
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public boolean isSignRet() {
		return signRet;
	}

	public void setSignRet(boolean signRet) {
		this.signRet = signRet;
		paramMap.set(YopConstants.SIGN_RETURN, String.valueOf(this.signRet));
	}

	public boolean isRest() {
		return isRest;
	}

	public void setRest(boolean isRest) {
		this.isRest = isRest;
	}

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getAppKey() {
        return appKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setServerRoot(String serverRoot) {
		this.serverRoot = serverRoot;
	}

	public String getServerRoot() {
		if (StringUtils.isBlank(serverRoot)) {
			serverRoot = YopConfig.getServerRoot();
		}
		return serverRoot;
	}

	public void encoding() {
		try {
			for (String key : this.paramMap.keySet()) {
				List<String> values = this.paramMap.get(key);
				List<String> encoded = new ArrayList<String>(values.size());
				for (String value : values) {
					if (StringUtils.isBlank(value)) {
						continue;
					}
					encoded.add(URLEncoder.encode(value, YopConstants.ENCODING));
				}
				values.clear();
				values.addAll(encoded);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getAbsoluteURL() {
		return absoluteURL;
	}

	public void setAbsoluteURL(String absoluteURL) {
		this.absoluteURL = absoluteURL;
	}

	/**
	 * 将参数转换成k=v拼接的形式
	 *
	 * @return
	 */
	public String toQueryString() {
		StringBuilder builder = new StringBuilder();
		for (String key : this.paramMap.keySet()) {
			List<String> values = this.paramMap.get(key);
			for (String value : values) {
				builder.append(builder.length() == 0 ? "" : "&");
				builder.append(key);
				builder.append("=");
				builder.append(value);
			}
		}
		return builder.toString();
	}
}
