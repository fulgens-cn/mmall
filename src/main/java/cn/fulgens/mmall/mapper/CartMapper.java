package cn.fulgens.mmall.mapper;

import cn.fulgens.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int selectCountNotCheckedByUserId(Integer userId);

    void deleteByUserIdAndProductIdList(@Param("userId") Integer userId,
                                        @Param("productIdList") List<String> productIdList);

    void checkedOrUnCheckedByUserId(@Param("userId") Integer userId, @Param("checked") Integer checked,
                                    @Param("productId") Integer productId);

    int selectProductCountByUserId(Integer userId);

    List<Cart> selectCheckedByUserId(Integer userId);
}