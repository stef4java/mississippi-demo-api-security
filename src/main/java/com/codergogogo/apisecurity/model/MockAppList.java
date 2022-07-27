package com.codergogogo.apisecurity.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "mock-data")
public class MockAppList {
    /**
     * 应用列表
     */
    private List<AppInfo> apps;

}