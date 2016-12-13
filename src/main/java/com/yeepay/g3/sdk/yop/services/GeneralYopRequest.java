package com.yeepay.g3.sdk.yop.services;

import com.yeepay.g3.sdk.yop.auth.YopCredentials;
import com.yeepay.g3.sdk.yop.model.AbstractYopRequest;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * title: <br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/23 16:30
 */
public class GeneralYopRequest extends AbstractYopRequest {

    private Map<String, List<String>> paramMap = new LinkedMap<String, List<String>>();

    @Override
    public GeneralYopRequest withRequestCredentials(YopCredentials credentials) {
        setRequestCredentials(credentials);
        return this;
    }

    public GeneralYopRequest addParam(String paramName, String paramValue) {
        List<String> values = paramMap.get(paramName);
        if (null == values) {
            values = new LinkedList<String>();
            paramMap.put(paramName, values);
        }

        values.add(paramValue);
        return this;
    }

    public Object getParam(String paramName) {
        return paramMap.get(paramName);
    }

    public Map<String, List<String>> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, List<String>> paramMap) {
        this.paramMap = paramMap;
    }

}
