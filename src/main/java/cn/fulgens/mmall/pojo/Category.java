package cn.fulgens.mmall.pojo;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 产品分类实体类
 *
 * @author fulgens
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Category {

    private Integer id;

    private Integer parentId;

    private String name;

    private Integer status;

    private Integer sortOrder;

    private List<Category> children;

    private Date createTime;

    private Date updateTime;
}