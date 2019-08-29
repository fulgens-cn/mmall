package cn.fulgens.mmall.common;

/**
* @Author: fulgens
* @Description: 响应码枚举类
* @Date: Created in 2018/2/4 22:01
* @Modified by:
*/
public enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND");

    private final int code;

    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
