package com.codergogogo.apisecurity.exception;


import com.codergogogo.apisecurity.model.dto.R;
import com.codergogogo.apisecurity.model.dto.SystemResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

/**
 * 出自: http://www.ballcat.cn/
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    /**
     * 生产环境
     */
    public static final String ENV_PROD = "prod";

    @Value("${spring.profiles.active:prod}")
    private String profile;

    public static final String PROD_ERR_MSG = "系统异常，请联系管理员";

    public static final String NLP_MSG = "空指针异常!";

    /**
     * 全局异常捕获
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(Exception.class)
    public R<String> handleGlobalException(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);
        // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
        String errorMessage = ENV_PROD.equals(profile) ? PROD_ERR_MSG : e.getLocalizedMessage();
        return R.failed(SystemResultCode.SERVER_ERROR, errorMessage);
    }

    /**
     * 空指针异常捕获
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(NullPointerException.class)
    public R<String> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常 ex={}", e.getMessage(), e);
        // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
        String errorMessage = ENV_PROD.equals(profile) ? PROD_ERR_MSG : NLP_MSG;
        return R.failed(SystemResultCode.SERVER_ERROR, errorMessage);
    }

    /**
     * MethodArgumentTypeMismatchException 参数类型转换异常
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public R<String> handleMethodArgumentTypeMismatchException(Exception e) {
        log.error("请求入参异常 ex={}", e.getMessage());
        String errorMessage = ENV_PROD.equals(profile) ? PROD_ERR_MSG : e.getMessage();
        return R.failed(SystemResultCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 请求方式有问题 - MediaType 异常 - Method 异常
     *
     * @return R
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class, HttpRequestMethodNotSupportedException.class})
    public R<String> requestNotSupportedException(Exception e) {
        log.error("请求方式异常 ex={}", e.getMessage());
        return R.failed(SystemResultCode.BAD_REQUEST, e.getLocalizedMessage());
    }

    /**
     * IllegalArgumentException 异常捕获，主要用于Assert
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法数据输入 ex={}", e.getMessage());
        return R.failed(SystemResultCode.BAD_REQUEST, e.getMessage());
    }

    /**
     * validation Exception
     *
     * @param exception e
     * @return R
     */
    @ExceptionHandler(BindException.class)
    public R<String> handleBodyValidException(BindException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        String errorMsg = bindingResult.getErrorCount() > 0 ? bindingResult.getAllErrors().get(0).getDefaultMessage()
                : "未获取到错误信息!";
        log.error("参数绑定异常,ex = {}", errorMsg);
        return R.failed(SystemResultCode.BAD_REQUEST, errorMsg);
    }

    /**
     * 单体参数校验异常 validation Exception
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(ValidationException.class)
    public R<String> handleValidationException(ValidationException e) {
        log.error("参数校验异常 ex={}", e.getMessage());
        return R.failed(SystemResultCode.BAD_REQUEST, e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public R<String> handleValidationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        log.error("参数校验异常 ex={}", message);
        return R.failed(SystemResultCode.BAD_REQUEST, message);
    }

    /**
     * 自定义业务异常捕获 业务异常响应码推荐使用200 用 result 结构中的code做为业务错误码标识
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(BusinessException.class)
    public R<String> handleBusinessException(BusinessException e) {
        log.error("业务异常信息 ex={}", e.getMessage());
        return R.failed(e.getCode(), e.getMessage());
    }
}