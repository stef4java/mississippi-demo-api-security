# API安全示例[API Security Demo]
## 关键词: 
timestamp、nonce、sign、Signature、API签名、防重放、Replay Attacks

## 快速开始
把`doc/API-Security-Demo.postman_collection.json`导入`postman`即可。

## 场景
```
#原始请求(改造前)
https://www.example.com/orders/api/v1/{uid}?page=1&size=10
```

## 风险
* 1.非法请求者:应用/终端/用户
* 2.请求参数改动
## 解决方法
### 1.合法性请求者:合法应用、合法终端、合法用户? appId，uid保证
- token是什么: 认证后的凭证，凭证具有时效性，临时性。 eg:皇宫里的认证、授权、时效性，临时性:尚方宝剑，你拿明朝的剑来斩清朝的官？
- token分类，使用场景
  - 应用凭证: 不需要请求者登陆，多用于企业间，如 微信支付、各开放平台第三方登陆等
  - 用户凭证: 需要用户登录，拿用户名/密码来换取凭证。带上凭证可获取各个资源或者划定范围内的资源(组织树)
### 2.参数防篡改? 加密算法保证
### 3.请求时效性？timestamp保证
### 4.请求唯一性，不能二次请求？nonce(number once)保证

## 签名核心点
* 签名算法
* 签名数据组装规则(非空参数按照ASCII升续排序)
* 秘钥

```
#改造后
https://example.com/orders/api/v1/{uid}?page=1&size=10
请求头:
appId
timestamp
nonce
sign:签名算法(秘钥,签名数据)

请求体
真实数据
```

* eg
```crul
curl --location --request POST 'http://127.0.0.1:18080/api-security/api/employees/all/2222222?name=lisi' \
--header 'sign: 460a7492a119127852961c27c91f87567208502cdf1dbae00e4277142cd5962f' \
--header 'appId: app1' \
--header 'timestamp: 1659356207348' \
--header 'nonce: 1234567891234567' \
--header 'Content-Type: application/json' \
--data-raw '{
    "age": 20,
    "deptName": "产品部"
}'

加密内容: {    "age": 20,    "deptName": "产品部"}#name=lisi#2222222#1659356207348#1234567891234567, 加密结果: 604d478d75be6345d0830f1125cf88be534aed49323b844715fd28e0f3643239
```

** 参考文章 **
[四连问：前后端分离接口应该如何设计？如何保证安全？如何签名？如何防重？]https://mp.weixin.qq.com/s?__biz=MzI1NDY0MTkzNQ==&mid=2247489024&idx=1&sn=1e93fa961a7826705b9f0b6dc1bf5ea6

