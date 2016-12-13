package com.yeepay.g3.sdk.yop.auth;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yeepay.g3.sdk.yop.encrypt.Digest;
import com.yeepay.g3.sdk.yop.http.Headers;
import com.yeepay.g3.sdk.yop.http.HttpUtils;
import com.yeepay.g3.sdk.yop.internal.InternalRequest;
import com.yeepay.g3.sdk.yop.utils.DateUtils;
import org.apache.log4j.Logger;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * title: 发行版本为 1 的请求报文签名器<br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/18 16:19
 */
public class YopV2Signer implements YopSigner {

    private static final Logger LOGGER = Logger.getLogger(YopV2Signer.class);

    private static final String YOP_AUTH_VERSION = "yop-auth-v2";

    // YOP signing protocol.
    private static final Set<String> defaultHeadersToSign = Sets.newHashSet();
    private static final Joiner headerJoiner = Joiner.on('\n');
    private static final Joiner signedHeaderStringJoiner = Joiner.on(';');

    static {
        YopV2Signer.defaultHeadersToSign.add(Headers.HOST.toLowerCase());
        YopV2Signer.defaultHeadersToSign.add(Headers.CONTENT_LENGTH.toLowerCase());
        YopV2Signer.defaultHeadersToSign.add(Headers.CONTENT_TYPE.toLowerCase());
        YopV2Signer.defaultHeadersToSign.add(Headers.CONTENT_MD5.toLowerCase());
    }

    @Override
    public void sign(InternalRequest request, YopCredentials credentials) {
        sign(request, credentials, null);
    }

    @Override
    public void sign(InternalRequest request, YopCredentials credentials, YopSignOptions options) {
        checkNotNull(request, "request should not be null.");

        if (credentials == null) {
            return;
        }

        if (options == null) {
            if (request.getSignOptions() != null) {
                options = request.getSignOptions();
            } else {
                options = YopSignOptions.DEFAULT_SIGN_OPTIONS;
            }
        }

        String accessKeyId = credentials.getAccessKeyId();
        String secretAccessKey = credentials.getSecretKey();

        request.addHeader(Headers.HOST, HttpUtils.generateHostHeader(request.getUri()));
        if (credentials instanceof YopSessionCredentials) {
            request.addHeader(Headers.YOP_SECURE_TOKEN, ((YopSessionCredentials) credentials).getSessionToken());
        }

        Date timestamp = options.getTimestamp();
        if (timestamp == null) {
            timestamp = new Date();
        }

        String authorizationHeader = sign(YopV2Signer.YOP_AUTH_VERSION, accessKeyId, secretAccessKey,
                request.getHttpMethod().name(), request.getUri().getPath(),
                request.getHeaders(), request.getParameterMap(),
                options.getHeadersToSign(),
                DateUtils.formatAlternateIso8601Date(timestamp),
                options.getExpirationInSeconds());
        request.addHeader(Headers.AUTHORIZATION, authorizationHeader);
    }

    public String sign(String version, String accessKeyId, String secretAccessKey,
                       String httpMethod, String path,
                       Map<String, String> headers, Map<String, String[]> parameters,
                       Set<String> headersToSignSet,
                       String timestamp, long expirationInSeconds) {
        String authString = version + "/" + accessKeyId + "/"
                + timestamp + "/" + expirationInSeconds;

        String signingKey = Digest.sha256Hex(secretAccessKey, authString);
        // Formatting the URL with signing protocol.
        String canonicalURI = HttpUtils.getCanonicalURIPath(path);
        // Formatting the query string with signing protocol.
        String canonicalQueryString = HttpUtils.getCanonicalQueryString(parameters, true);
        // Sorted the headers should be signed from the request.
        SortedMap<String, String> headersToSign =
                this.getHeadersToSign(headers, headersToSignSet);
        // Formatting the headers from the request based on signing protocol.
        String canonicalHeader = this.getCanonicalHeaders(headersToSign);
        String signedHeaders = "";
        if (headersToSignSet != null) {
            signedHeaders = YopV2Signer.signedHeaderStringJoiner.join(headersToSign.keySet());
            signedHeaders = signedHeaders.trim().toLowerCase();
        }

        String canonicalRequest = httpMethod + "\n" + canonicalURI + "\n" + canonicalQueryString + "\n" + canonicalHeader;

        // Signing the canonical request using key with sha-256 algorithm.
        String signature = Digest.sha256Hex(signingKey, canonicalRequest);

        String authorizationHeader = authString + "/" + signedHeaders + "/" + signature;
        LOGGER.debug("CanonicalRequest:" + canonicalRequest.replace("\n", "[\\n]") + "\tAuthorization:" + authorizationHeader);
        return authorizationHeader;
    }

    private String getCanonicalHeaders(SortedMap<String, String> headers) {
        if (headers.isEmpty()) {
            return "";
        }

        List<String> headerStrings = Lists.newArrayList();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (key == null) {
                continue;
            }
            String value = entry.getValue();
            if (value == null) {
                value = "";
            }
            headerStrings.add(HttpUtils.normalize(key.trim().toLowerCase()) + ':' + HttpUtils.normalize(value.trim()));
        }
        Collections.sort(headerStrings);

        return headerJoiner.join(headerStrings);
    }

    private SortedMap<String, String> getHeadersToSign(Map<String, String> headers, Set<String> headersToSign) {
        SortedMap<String, String> ret = Maps.newTreeMap();
        if (headersToSign != null) {
            Set<String> tempSet = Sets.newHashSet();
            for (String header : headersToSign) {
                tempSet.add(header.trim().toLowerCase());
            }
            headersToSign = tempSet;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if ((headersToSign == null && this.isDefaultHeaderToSign(key))
                        || (headersToSign != null && headersToSign.contains(key.toLowerCase())
                        && !Headers.AUTHORIZATION.equalsIgnoreCase(key))) {
                    ret.put(key, entry.getValue());
                }
            }
        }
        return ret;
    }

    private boolean isDefaultHeaderToSign(String header) {
        header = header.trim().toLowerCase();
        return header.startsWith(Headers.YOP_PREFIX) || defaultHeadersToSign.contains(header);
    }

}
