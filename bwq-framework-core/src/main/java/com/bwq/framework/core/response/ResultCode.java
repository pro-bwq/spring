package com.bwq.framework.core.response;

import com.bwq.framework.core.base.IBaseEnum;
import lombok.Getter;

/**
 * @Description http响应状态码
 * @Author bwq
 * @Date 2023/6/6
 */

public enum ResultCode implements IBaseEnum<Integer> {

    /**
     * success
     */
    SUCCESS(200 , "操作成功"),

    /**
     * failure
     */
    FAILURE(500 , "操作失败"),

    /**
     * 请求未授权，请求头中没有提供正确的身份验证信息
     */
    TOKEN_MISSING(401 , "未授权"),

    /**
     * 权限不足，禁止访问
     */
    DENY_ACCESS(403 ,"禁止访问"),

    /**
     * 没找到
     */
    NOT_FOUND(404 ,"资源不存在"),

    /**
     * 方法禁用
     */
    METHOD_FORBIDDEN(405 ,"不支持此种方法调用接口"),

    /**
     * 网络服务错误
     */
    SERVER_ERROR(500 ,"服务器开小差，请稍后再试"),


    /**
     *  code
     *  常规code ： 成功
     *  特殊code构成规则 ：
     *  1、2两位特定数字： 24，
     *  3、4两位填充占位：00 ，占位说明：00（头部）、01（请求参数）、10（异常）、11（其他）
     *  5、6两位类型数字：00，
     * */

    /********************************头部参数（00）*************************************/

    /**
     * 不支持方法
     */
    METHOD_ERROR(240000, "请求方法错误"),
    INVALID_TOKEN(240002, "令牌有误"),
    TOKEN_EXPIRED(240003 , "令牌已过期"),

    GENERATOR_ACCESS_TOKEN_ERROR(240005 , "访问令牌生成失败"),
    GENERATOR_REFRESH_TOKEN_ERROR(240006 , "刷新令牌生成失败"),

    /*****************************常规错误（01）****************************************/
    PARAM_MISSING(240100, "参数缺失"),
    USER_NOT_EXIST(240101,"用户不存在"),
    CAPTCHA_ERROR(240102,"图形验证码错误"),
    CAPTCHA_EXPIRED(240103,"图形验证码已过期"),
    SMS_CODE_EXPIRED(240104,"验证码已过期"),
    SMS_CODE_ERROR(240105,"验证码错误"),
    PASSWORD_ERROR(240106,"密码错误"),
    USER_PHONE_ALREADY_EXIST(240107,"手机号已存在"),
    
    AUTH_ERROR(240110,"认证失败"),

    PARAM_NOT_NULL(240111,"不能为空"),

    PARAM_ERROR(240112, "参数不合法"),
    REPEAT_SUBMIT_ERROR(240113, "请求已提交，请勿重复提交或等待片刻再尝试"),
    REPEAT_REQUEST_ACCESS_TOKEN(240114 , "禁止重复获取令牌，五分钟之后再试"),
    DISABLED_ERROR(240115,"该账户已被禁用!"),
    LOCKED_ERROR(240116, "该账号已被锁定!"),
    ACCOUNT_EXPIRED_ERROR(240117, "该账号已过期!"),


    /*****************************异常相关错误（10）****************************************/

    BUSINESS_ERROR(241007, "异常"),

    /**
     * 系统限流
     */
    TRAFFIC_LIMITING(241008, "哎呀，网络拥挤请稍后再试试"),

    SERVLET_ERROR(241010, "servlet错误"),
    JSON_ERROR(241011, "json错误"),
    TYPE_MATCH_ERROR(241012, "类型不匹配错误"),
    SQL_ERROR(241013, "sql错误"),
    ASYNC_TASK_ERROR(241013, "异步任务错误"),
    FEIGN_ERROR(241013, "Feign错误"),
    CODE_ERROR(241014 ,"编码错误"),



    /********************************其他（11）*************************************/

    INSERT_FAILURE(241100 , "添加失败"),
    REMOVE_FAILURE(241101 , "移除失败"),
    DELETE_FAILURE(241102 , "删除失败"),
    UPDATE_FAILURE(241103 , "更新失败"),
    QUERY_FAILURE(241104 ,"查询失败"),

    /********************************业务异常*************************************/
    CUSTOMER_ALREADY_EXIST(30000, "客户已经存在"),
    FILE_SIZE_EXCEED(30001, "文件大小超过限制"),
    ;

    @Getter
    private Integer value;

    @Getter
    private String label;
    ResultCode(Integer code, String label) {
        this.value = code;
        this.label = label;
    }

}
