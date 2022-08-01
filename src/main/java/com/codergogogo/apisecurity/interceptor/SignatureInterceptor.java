package com.codergogogo.apisecurity.interceptor;

import com.codergogogo.apisecurity.exception.BusinessException;
import com.codergogogo.apisecurity.model.AppInfo;
import com.codergogogo.apisecurity.model.MockAppList;
import com.codergogogo.apisecurity.utils.sign.SignatureUtils;
import com.codergogogo.apisecurity.web.RequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SignatureInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MockAppList apps;

    /**
     * 超时时间
     */
    public static final long expireTime = 5 * 60 * 1000;

    /**
     * @param request
     * @param response
     * @param handler  访问的目标方法
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        String sign = request.getHeader("sign");
        String appId = request.getHeader("appId");
        // 0 请求空参数非空，长度校验
        Assert.isTrue(!StringUtils.isEmpty(appId) && !StringUtils.isEmpty(timestamp) && !StringUtils.isEmpty(sign), "参数错误");
        Assert.isTrue(nonce.length() == 16, "随机串nonce长度需要16位");

        // 1. 拒绝重复调用(第一次访问时存储，过期时间和请求超时时间保持一致)
        ValueOperations<String, Integer> nonceRedis = redisTemplate.opsForValue();
        boolean exists = redisTemplate.hasKey(nonce);
        Assert.isTrue(!exists, "重复请求,nonce非法");

        // 2. 请求时间间隔
        long requestInterval = System.currentTimeMillis() - Long.parseLong(timestamp);
        Assert.isTrue(requestInterval < expireTime, "非法时间，请重新请求");

        // 3. 根据appId查询数据库获取appSecret
        AppInfo appInfo = apps.getApps().stream().filter(app -> app.getAppId().equals(appId)).findAny().orElseThrow(() -> new BusinessException(
                404, "应用不存在"));

        // 4. 校验签名(将所有的参数加进来，防止别人篡改参数) 所有参数看参数名升续排序拼接成url
        // 4.1 获取body（对应@RequestBody）
        String body = null;
        if (request instanceof RequestWrapper) {
            body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        }

        // 4.2 获取parameters（对应@RequestParam）
        Map<String, String[]> params = null;
        if (!CollectionUtils.isEmpty(request.getParameterMap())) {
            params = request.getParameterMap();
        }

        //  4.3 获取path variable（对应@PathVariable）
        String[] paths = null;
        ServletWebRequest webRequest = new ServletWebRequest(request, null);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (!CollectionUtils.isEmpty(uriTemplateVars)) {
            paths = uriTemplateVars.values().toArray(new String[]{});
        }

        log.info("appId: {}, AppSecret: {}", appInfo.getAppId(), appInfo.getAppSecret());
        String signature = SignatureUtils.sign(body, params, paths, timestamp, nonce, appInfo.getAppSecret());
        Assert.isTrue(signature.equals(sign), "签名错误");

        nonceRedis.set(nonce, 0, expireTime, TimeUnit.MILLISECONDS);
        return super.preHandle(request, response, handler);
    }

}