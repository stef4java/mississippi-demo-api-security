package com.codergogogo.apisecurity.utils.sign;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * 来源: https://www.cnblogs.com/hujunzheng/p/10178584.html#autoid-8-0-0
 **/
@Slf4j
public class SignatureUtils {

    public static String sign(String body, Map<String, String[]> params, String[] paths, String timestamp, String nonce, String appSecret) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(body)) {
            sb.append(body).append('#');
        }

        if (!CollectionUtils.isEmpty(params)) {
            params.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(paramEntry -> {
                        String paramValue = String.join(",", Arrays.stream(paramEntry.getValue()).sorted().toArray(String[]::new));
                        sb.append(paramEntry.getKey()).append("=").append(paramValue).append('#');
                    });
        }

        if (ArrayUtils.isNotEmpty(paths)) {
            String pathValues = String.join(",", Arrays.stream(paths).sorted().toArray(String[]::new));
            sb.append(pathValues);
        }

        sb.append(timestamp).append(nonce);
        String createSign = HmacUtils.hmacSha256Hex(appSecret, sb.toString());
        log.info("加密内容: {}, 加密结果: {}", sb.toString(),createSign);
        return createSign;
    }

}