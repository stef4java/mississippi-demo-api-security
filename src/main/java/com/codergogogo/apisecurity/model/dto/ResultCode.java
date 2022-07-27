package com.codergogogo.apisecurity.model.dto;

/**
 * 出自: http://www.ballcat.cn/
 * @author stef
 * @version 1.0
 * @date 2020/3/20 14:45
 */
public interface ResultCode {

	/**
	 * 获取业务码
	 * @return 业务码
	 */
	Integer getCode();

	/**
	 * 获取信息
	 * @return 返回结构体中的信息
	 */
	String getMessage();

}