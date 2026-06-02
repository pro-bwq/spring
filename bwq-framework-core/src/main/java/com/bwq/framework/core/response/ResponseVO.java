package com.bwq.framework.core.response;

import com.bwq.framework.core.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


/**
 * @author BWQ
 * @description: API响应对象
 * @date 2024/2/22 17:40
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "响应返回数据对象")
public class ResponseVO<T> extends BaseVO{

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码" , format = "int32")
    private  Integer code;

    @Schema(description = "文字描述信息")
    private  String message;

    @Schema(description = "响应内容")
    private T data;

    @Schema(description = "时间戳")
    private Long timestamp;


    /***
     * 构造函数
     */
    public ResponseVO(){
        this.setCode(ResultCode.SUCCESS.getValue());
        this.setMessage(ResultCode.SUCCESS.getLabel());
        this.setTimestamp(System.currentTimeMillis());
    }

    /**
     * @description: 成功响应
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> success() {
        return new ResponseVO<>();
    }
    /**
     * @description: 成功响应
     * @param data 响应的data数据
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> success(T data) {

        ResponseVO<T>  response = new ResponseVO<>();
        if (data != null){
            response.setData(data);
        }
        return response;
    }




    /**
     * @description: 根据状态status判断返回结果
     * @param status 状态值
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> judge(boolean status){
        if (status){
            return success();
        }else {
            return failure(ResultCode.FAILURE);
        }
    }

    /**
     * @description: 根据状态status判断返回结果
     * @param status 状态值
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> judge(int status){
        if (status > 0){
            return  success();
        }else {
            return failure(ResultCode.FAILURE);
        }
    }

    /**
     * @description: 失败响应
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> failure() {

        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(ResultCode.FAILURE.getValue());
        response.setMessage(ResultCode.FAILURE.getLabel());
        return response;
    }

    /**
     * @description: 失败响应带自定义消息
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> failure(String message) {

        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(ResultCode.FAILURE.getValue());
        response.setMessage(message);
        return response;
    }


    /**
     * @description: 失败响应
     * @param code 错误码
     * @param message 错误信息
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> failure(int code, String message) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * @description: 失败响应
     *
     * @param en 枚举类型
     * @param extraMsg 额外的文字描述
     * @return ResponseVO对象
     */
    public static <T> ResponseVO<T> failure(ResultCode en , String extraMsg) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(en.getValue());
        String message = en.getLabel();
        if (extraMsg != null && !extraMsg.isBlank()){
            if (en == ResultCode.BUSINESS_ERROR){
                message = extraMsg.trim();
            }else
            if (en == ResultCode.PARAM_NOT_NULL){
                message = extraMsg + message;
            }
            else {
                message += "，" + extraMsg.trim();
            }
        }
        log.info("CORE======message======={}",message);
        response.setMessage(message);
        return response;
    }

    /**
     * @description: 失败响应
     *
     * @param en 枚举类型
     * @return ResponseVO 对象
     */
    public static <T> ResponseVO<T> failure(ResultCode en) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(en.getValue());
        response.setMessage(en.getLabel());
        return response;
    }

    public static <T> boolean isSuccess(ResponseVO<T> result) {
        return result != null && ResultCode.SUCCESS.getValue().equals(result.getCode());
    }
}
