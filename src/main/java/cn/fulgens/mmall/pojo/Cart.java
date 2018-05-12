package cn.fulgens.mmall.pojo;

import lombok.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
//@Data包含了@Setter、@Getter、@ToString、@EqualsAndHashCode
//Lombok实际使用需要注意：
//1.在类需要序列化、反序列化详细控制字段时如Jackson json序列化时有些字段无需序列化
//2.使用Lombok虽然可以省去手动创建Setter、Getters方法等繁琐操作，但却减低了源码的可读性与完整性
//3.使用@Slf4j还是@Log4j需要看项目使用的日志框架如本项目使用logback则需要使用@Slf4j注解
//4.选择合适的地方使用Lombok例如Pojo，因为Pojo很单纯
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;

}