package cn.fulgens.mmall.pojo;

import lombok.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;

/**
 * 购物车实体类
 *
 * @author fulgens
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;
}