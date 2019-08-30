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

    public Category(Integer id, Integer parentId, String name, Integer status, Integer sortOrder, Date createTime, Date updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.status = status;
        this.sortOrder = sortOrder;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}