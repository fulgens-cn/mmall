package cn.fulgens.mmall.service.impl;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.mapper.CategoryMapper;
import cn.fulgens.mmall.pojo.Category;
import cn.fulgens.mmall.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
* @Author: fulgens
* @Description: 商品品类服务实现类
* @Date: Created in 2018/2/5 22:55
* @Modified by:
*/
@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.errorWithMsg("添加商品品类参数错误");
        }
        int count = categoryMapper.checkCategoryExist(parentId, categoryName);
        if (count > 0) {
            return ServerResponse.errorWithMsg("该品类已存在，请勿重复添加");
        }
        Category category = new Category();
        // 设置品类名称
        category.setName(categoryName);
        // 设置父品类id
        category.setParentId(parentId);
        // 设置品类状态，1-正常，2-已废弃'
        category.setStatus(1);
        // 插入数据
        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.successWithMsg("添加商品品类成功");
        }
        return ServerResponse.errorWithMsg("添加商品品类失败");
    }

    @Override
    public ServerResponse<String> updateCategoryNameById(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.errorWithMsg("修改商品品类名称参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.successWithMsg("更新品类名字成功");
        }
        return ServerResponse.errorWithMsg("更新品类名字失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenByParentId(Integer parentId) {
        if (parentId != 0) {
            int count = categoryMapper.selectCountByPrimaryKey(parentId);
            if (count == 0) {
                return ServerResponse.errorWithMsg("未找到该品类");
            }
        }
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(parentId);
        return ServerResponse.successWithData(categoryList);
    }

    @Override
    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer id) {
        if (id != 0) {
            int count = categoryMapper.selectCountByPrimaryKey(id);
            if (count == 0) {
                return ServerResponse.errorWithMsg("未找到该品类");
            }
        }
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategories(categorySet, id);
        List<Integer> categoryIdList = Lists.newArrayList();
        for (Category category : categorySet) {
            categoryIdList.add(category.getId());
        }
        return ServerResponse.successWithData(categoryIdList);
    }

    // 递归获取当前分类及递归子节点
    public Set<Category> findChildrenCategories(Set<Category> categorySet, Integer id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category != null) {
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(id);
        for (Category child : categoryList) {
            findChildrenCategories(categorySet, child.getId());
        }
        return categorySet;
    }

    // 也可以在Category中维护private List<Category> children;属性形成递归树
    public Category findChildrenCategories(Integer id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(id);
        for (Category child : categoryList) {
            Category childCategory = findChildrenCategories(child.getId());
            category.getChildren().add(childCategory);
        }
        return category;
    }
}
