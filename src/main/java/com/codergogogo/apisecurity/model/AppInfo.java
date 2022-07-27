package com.codergogogo.apisecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {
    private String appId;
    private String appSecret;
    private String appDesc;
}