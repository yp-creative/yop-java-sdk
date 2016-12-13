# yop-java-sdk
YOP 平台 Java版 SDK

# SDK使用说明
易宝开放平台的SDK是由程序自动化生成的代码包，其中包含了构建请求、加密、返回解析等一些必要的功能。目前支持的开发环境如下：

java sdk支持1.5及以上（目前仅支持j2se标准java平台使用，不适合andriod平台）；且需依赖spring框架及fastxml包

## 1.开始
前提已注册成为开放平台开发者，并创建应用。如果未注册，请访问[开放平台](http//open.yeepay.com)注册并创建应用。

如果是易宝商户，也可以不创建应用，直接以商户身份可登录开放平台、申请API权限、并直接发起调用

## 2. 开发准备
普通java工程请将sdk内各jar包加入classpath（若工程已有相同jar或有其他版本jar可不添加）
易宝子公司使用YOP可通过maven依赖：

```xml
<dependency>
	<groupId>com.yeepay.g3.yop</groupId>
	<artifactId>yop-sdk</artifactId>
	<version>1.0</version>
</dependency>
```

## 3. 示例&详细说明
```java
YopConfig.setAppKey("lanmao");
YopConfig.setAesSecretKey("O2GR4japDSlewTwpW55WmA==");
YopRequest request = new YopRequest();
request.addParam("address", "13812345678");
YopResponse response = YopClient.get("/rest/v1.0/notifier/blacklist/add", request);
```

### 3.1. 应用-密钥配置
默认使用`YopConfig.setXXX`初始化`appKey`及`AES密钥`（商户身份仅需设置Hmac密钥）
开放平台默认调用地址为[https://open.yeepay.com/yop-center](https://open.yeepay.com/yop-center)

如需非生产环境联调，需手动设置调用地址：
```java
YopConfig.setServerRoot("http://qa.yeepay.com:18083/yop-center");
```
如需同时使用多个appKey则通过
```java
YopRequest(String appKey, String secretKey)初始化请求对象
```

### 3.2. 请求
传参：
```java
request.addParam("address", "13812345678");
```
>相同参数名自动构建为数组参数

签名：使用YopClient发起请求自动签名
签名逻辑
(1) 所有请求参数按参数名升序排序，数组/列表值排序;
(2) 按 请 求 参 数 名 及 参 数 值 相 互 连 接 组 成 一 个 字 符 串 :<paramName1><paramValue1><paramName2><paramValue2>...;  （注：restful接口自动将URI作为method参数参与签名）
(3) 将应用密钥分别添加到以上请求参数串的头部和尾部:<secret><请求参数字符串><secret>;
(4) 对该字符串按指定算法（默认为SHA1） 签名
(5) 该签名值使用sign系统级参数一起和其它请求参数一起发送给服务开放平台

发起请求：
```java
YopResponse response = YopClient.get("/rest/v1.0/notifier/blacklist/add", request);
```

### 3.3. 响应
`YopResponse`主要内容是状态、业务结果、错误/子错误
业务结果被解析为Map，可直接取值；

若觉得Map型结果不便于使用，可通过`response.unmarshal(xxx.class)`反序列化为自定义java对象；

若不想自定义java对象，`response.getStringResult()`即为字符串形式的业务结果，格式为request指定格式（json/xml)，可直接做json/xml解析。

SDK已提供工具类方法`YopMarshallerUtils.parse`支持json解析，具体解析方法可自选。

### 3.4. 验签
请求是必须做签名验证的，SDK&YOP自动完成

响应结果也可签名，通过`request.setSignRet(true)`指定（默认不做结果签名），YopResponse自动验签

某些API指使用非SHA1签名算法，请求对象需明确指定，示例：`request.setSignAlg("SHA");`


### 3.5. 加解密
某些API参数使用明文参数有安全风险，如支付接口可能需要传信用卡CVV码，这些信息明确要求不能明文传输，为此需要对请求进行加密

SDK中，通过`request.setEncrypt(true)`指定此请求需加密，当前支持AES及Blowfish加解密算法，开放应用调用使用AES、商户身份调用使用Blowfish

请求加密则响应默认加密，YopResponse自动解密、验签、反序列化
