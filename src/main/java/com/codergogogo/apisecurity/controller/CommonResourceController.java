package com.codergogogo.apisecurity.controller;

import com.codergogogo.apisecurity.model.dto.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/no-auth")
public class CommonResourceController {


    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳
     */
    @GetMapping("/ts")
    public R<Long> timestamp() {
        return R.ok(System.currentTimeMillis());
    }

}