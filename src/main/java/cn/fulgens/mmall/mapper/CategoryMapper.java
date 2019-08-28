package cn.fulgens.mmall.mapper;

import cn.fulgens.mmall.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    int selectCountByPrimaryKey(Integer id);

    List<Category> selectChildrenByParentId(Integer parentId);

    int checkCategoryExist(@Param("parentId") Integer parentId, @Param("categoryName") String categoryName);
}