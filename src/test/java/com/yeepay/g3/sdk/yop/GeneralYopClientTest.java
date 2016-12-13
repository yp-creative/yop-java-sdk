package com.yeepay.g3.sdk.yop;

import com.yeepay.g3.sdk.yop.auth.DefaultYopCredentials;
import com.yeepay.g3.sdk.yop.client.YopClient;
import com.yeepay.g3.sdk.yop.client.YopConfig;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.http.HttpMethodName;
import com.yeepay.g3.sdk.yop.internal.InternalRequest;
import com.yeepay.g3.sdk.yop.services.GeneralYopClient;
import com.yeepay.g3.sdk.yop.services.GeneralYopRequest;
import com.yeepay.g3.sdk.yop.services.GeneralYopResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * title: <br/>
 * description: 描述<br/>
 * Copyright: Copyright (c)2014<br/>
 * Company: 易宝支付(YeePay)<br/>
 *
 * @author baitao.ji
 * @version 1.0.0
 * @since 16/2/23 15:10
 */
public class GeneralYopClientTest {

    @Test
    public void testName1() throws Exception {
        YopRequest request = new YopRequest(null,
                "8intulgnqibv77f1t8q9j0hhlkiy6ei6c82sknv63vib3zhgyzl8uif9ky7a",
                "http://open.yeepay.com:8064/yop-center");
        request.setEncrypt(true);
        request.setSignRet(true);
        request.addParam("customerNo", "10040011444");
        request.addParam("requestId", "YOP-SDK-" + System.currentTimeMillis());
//		request.addParam("platformUserNo","YOP-USERNO-" + System.currentTimeMillis());
        request.addParam("platformUserNo", "8880222");
//		request.addParam("platformUserNo","YOP-USERNO-1435560994654");
        System.out.println(request.toQueryString());
        YopResponse response = YopClient.post("/rest/v1.0/member/queryAccount", request);
        System.out.println(response.toString());
    }

    @Test
    public void testName2() throws Exception {
        // 凭证配置
        final String ACCESS_KEY_ID = "openSmsApi";
        final String SECRET_ACCESS_KEY = "yUiAtCenJRW8jyXWX8cjeA==";
        DefaultYopCredentials yopCredentials = new DefaultYopCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY);

        // 获取客户端
        YopClientConfiguration config = new YopClientConfiguration()
                .withCredentials(yopCredentials)
                .withPort(8064);
        GeneralYopClient yopClient = new GeneralYopClient(config);

        // 封装请求
        GeneralYopRequest yopRequest = new GeneralYopRequest();
        yopRequest.addParam("customerNo", "10040011444");
        yopRequest.addParam("requestId", "YOP-SDK-" + System.currentTimeMillis());
        yopRequest.addParam("platformUserNo", "8880222");
        yopRequest.addParam("name", "123");
        yopRequest.addParam("name", "456");

        // 发起调用
        InternalRequest internalRequest = yopClient.createRequest("/rest/v1.0/member/", yopRequest, HttpMethodName.POST, "queryAccount");
        GeneralYopResponse response = yopClient.invokeHttpClient(internalRequest, GeneralYopResponse.class);

        System.out.println(response);
    }

    @Test
    public void testSendSmsProduct4() {
        YopConfig.setAppKey("ypo2o");//yop应用
        YopConfig.setAesSecretKey("tpcY6k2RSpEod7hsJIp33Q==");//yop应用密钥，需要和短信通知应用的密钥保持一致才行，否则验证签名不通过
//        YopConfig.setServerRoot("https://open.yeepay.com/yop-center");//生产环境
        YopConfig.setServerRoot("http://open.yeepay.com:8064/yop-center");
        YopRequest request = new YopRequest();
        request.setSignAlg("MD5");//具体看api签名算法而定
        // request.setEncrypt(true);
        String notifyRule = "EGOU_VERIFY";//通知规则
        List recipients = new ArrayList();//接收人
        recipients.add(0, "18253166342");
        String content = "{message1:123445}";//json字符串，code，mctName为消息模板变量
        String extNum = "52";//扩展码
        String feeSubject = "0.01";//计费主体
        request.addParam("notifyRule", notifyRule);
        request.addParam("recipients", recipients);
        request.addParam("content", content);
        request.addParam("extNum", extNum);
        request.addParam("feeSubject", feeSubject);
        YopResponse response = YopClient.post("/rest/v1.0/notifier/send", request);
        System.out.println(response);
    }

}