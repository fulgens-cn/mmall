package cn.fulgens.mmall.common;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 服务端通用响应对象
 * 由于使用FastJson作为消息转换器故不使用Jackson @JsonInclude(value = JsonInclude.Include.NON_NULL)
 *
 * @param <T>
 * @author fulgens
 */
public class ServerResponse<T> implements Serializable {

    /** 响应状态码 */
    private Integer code;

    /** 响应信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    private ServerResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * jackson可使用@JsonIgnore注解忽略
     *
     * @return
     */
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return this.code == ResponseCode.SUCCESS.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> ServerResponse<T> success() {
        return build(ResponseCode.SUCCESS.getCode(), StringUtils.EMPTY, null);
    }

    public static <T> ServerResponse<T> successWithMsg(String msg) {
        return build(ResponseCode.SUCCESS.getCode(), msg, null);
    }

    public static <T> ServerResponse<T> successWithData(T data) {
        return build(ResponseCode.SUCCESS.getCode(), StringUtils.EMPTY, data);
    }

    public static <T> ServerResponse<T> successWithMsgAndData(String msg, T data) {
        return build(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> error() {
        return build(ResponseCode.ERROR.getCode(), StringUtils.EMPTY, null);
    }

    public static <T> ServerResponse<T> errorWithMsg(String errorMsg) {
        return build(ResponseCode.ERROR.getCode(), errorMsg, null);
    }

    public static <T> ServerResponse<T> errorWithMsg(int errorCode, String errorMsg) {
        return build(errorCode, errorMsg, null);
    }

    public static <T> ServerResponse<T> buildWithResponseCode(ResponseCode responseCode) {
        return build(responseCode.getCode(), responseCode.getDesc(), null);
    }

    public static <T> ServerResponse<T> build(Integer status, String msg, T data) {
        return new ServerResponse<>(status, msg, data);
    }
}
