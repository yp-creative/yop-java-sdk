package com.yeepay.g3.sdk.yop;

import com.TrustAllHttpsCertificates;
import com.yeepay.g3.sdk.yop.client.YopClient;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * title: <br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 15/7/8 10:23
 */
public class DevDemo {

    private static final String BASE_URL = "http://172.17.102.173:8064/yop-center/";

    private static final String[] APP_KEYS = {"yop-boss","jinkela2"};
    private static final String[] APP_SECRETS = {"yop-boss",""};

    @Before
    public void setUp() throws Exception {
        TrustAllHttpsCertificates.setTrue();
    }

    @Test
    public void testIdCard() throws Exception {
        YopRequest request = new YopRequest(null, "0/ZoyfKku0tunPunw7dbfA==", BASE_URL);
//        YopRequest request = new YopRequest(null, "cAFj+DxhpeMo8afn7s0z5w==", "http://10.151.30.88:8064/yop-center/");
        request.setEncrypt(true);
        request.setSignRet(true);
        request.setSignAlg("sha-256");
        request.addParam("appKey", "jinkela2");
        request.addParam("requestFlowId", "test123456");//请求流水标识
        request.addParam("name", "张文康");
        request.addParam("idCardNumber", "370982199101186692");
        request.addParam("testDate", "2012-10-09 12:13:14");
        System.out.println(request.toQueryString());
        YopResponse response = YopClient.post("/rest/v2.0/auth/idcard", request);
        System.out.println(response.toString());
    }

    @Test
    public void testEnterprise() throws Exception {
        YopRequest request = new YopRequest("yop-boss", "QFdODaBYBiVuLpP+sbyH+g==", "http://open.yeepay.com:8064/yop-center/");
        request.setEncrypt(false);
        request.setSignRet(true);
        request.addParam("appKey", "yop-boss");//这个写YOP就可以了
//        request.addParam("requestSystem", "YOP");//这个写YOP就可以了
        request.addParam("corpName", "青海韩都忆餐饮管理有限公司");//企业名称
        request.addParam("regNo", "630104063037404");//工商注册号
        request.addParam("requestCustomerId", "jinkela");//子商户编号
        request.addParam("requestFlowId", "test-" + System.currentTimeMillis() + RandomStringUtils.randomNumeric(3));//请求流水标识
        request.addParam("requestIdentification", "wenkang.zhang");//请求者标识
        System.out.println(request.toQueryString());
        YopResponse response = YopClient.post("/rest/v1.2/auth/authenterprise", request);
        System.out.println(response.toString());
    }

    @Test
    public void testWhiteList() throws Exception {
        YopRequest request = new YopRequest(null, "cGB2CeC3YmwSWGoVz0kAvQ==", BASE_URL);
        request.setEncrypt(false);
        request.setSignRet(true);
        request.addParam("appKey", "yop-boss");
        request.setSignAlg("sha-256");
        request.addParam("requestFlowId", "test123456");//请求流水标识
        request.addParam("name", "张文康");
        request.addParam("idCardNumber", "370982199101186691");
        System.out.println(request.toQueryString());
        YopResponse response = YopClient.post("/rest/v2.0/auth/idcard", request);
        System.out.println(response.toString());
    }

    @Test
    public void testCreateToken() {
        YopRequest request = new YopRequest(null, "cGB2CeC3YmwSWGoVz0kAvQ==", BASE_URL);
        request.setEncrypt(true);
        request.setSignRet(true);
//        request.setSignAlg("SHA1");
//        request.addParam("customerNo", "10040011444");
        request.addParam("appKey", "yop-boss");

        request.addParam("grant_type", "password");//请求流水标识
        request.addParam("client_id", "appKey");
        request.addParam("authenticated_user_id", "wenkang.zhang");
        request.addParam("scope", "test");
        System.out.println(request.toQueryString());
        YopResponse response = YopClient.post("/rest/v1.0/oauth2/token", request);
        System.out.println(response.toString());
    }
}
