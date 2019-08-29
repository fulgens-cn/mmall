package cn.fulgens.mmall.common;

/**
 * Simditor富文本组件响应对象
 *
 * @author fulgens
 */
public class SimditorServerResponse {

    private boolean success;

    private String msg;

    private String file_path;

    public SimditorServerResponse(boolean success, String msg, String file_path) {
        this.success = success;
        this.msg = msg;
        this.file_path = file_path;
    }

    public static SimditorServerResponse success(String msg, String file_path) {
        return new SimditorServerResponse(Boolean.TRUE, msg, file_path);
    }

    public static SimditorServerResponse error(String msg, String file_path) {
        return new SimditorServerResponse(Boolean.TRUE, msg, file_path);
    }
}
