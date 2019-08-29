package cn.fulgens.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品实体类
 *
 * @author fulgens
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Integer id;

    private String name;

    private String subtitle;

    private String mainImage;

    private String subImages;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String detail;

    private Integer categoryId;

    private Date createTime;

    private Date updateTime;
}