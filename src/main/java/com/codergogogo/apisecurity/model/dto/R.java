package com.codergogogo.apisecurity.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;

    private String message;

    private T data;

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return new R<T>().setCode(SystemResultCode.SUCCESS.getCode()).setData(data)
                .setMessage(SystemResultCode.SUCCESS.getMessage());
    }

    public static <T> R<T> ok(T data, String message) {
        return new R<T>().setCode(SystemResultCode.SUCCESS.getCode()).setData(data).setMessage(message);
    }

    public static <T> R<T> failed(int code, String message) {
        return new R<T>().setCode(code).setMessage(message);
    }

    public static <T> R<T> failed(ResultCode failMsg) {
        return new R<T>().setCode(failMsg.getCode()).setMessage(failMsg.getMessage());
    }

    public static <T> R<T> failed(ResultCode failMsg, String message) {
        return new R<T>().setCode(failMsg.getCode()).setMessage(message);
    }

}