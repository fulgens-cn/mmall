package cn.fulgens.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
* @Author: fulgens
* @Description: mmall常量类
* @Date: Created in 2018/2/4 23:04
* @Modified by:
*/
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String USERANME = "username";
    public static final String EMAIL = "email";

    public static final Set<String> orderBySet = Sets.newHashSet("price_desc", "price_asc");

    public interface Cart {
        int CHECKED = 1;    // 勾选状态

        int UN_CHECKED = 0; // 未勾选状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";

        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface Role {
        int ROLE_ADMIN = 0;

        int ROLE_USER = 1;
    }

    public enum ProductStatusEnum {
        // 1-在售，2-下架，3-删除
        ON_SALE(1, "在售"),
        UNDER_SHELF(2, "下架"),
        DELETED(3, "删除");

        private int code;

        private String value;

        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
