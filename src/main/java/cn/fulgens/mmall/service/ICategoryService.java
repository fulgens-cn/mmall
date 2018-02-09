package cn.fulgens.mmall.service;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.Category;

import java.util.List;

/**
* @Author: fulgens
* @Description: 商品品类服务接口
* @Date: Created in 2018/2/5 22:52
* @Modified by:
*/
public interface ICategoryService {

    /**
     * 添加商品品类
     * @param parentId  父品类id
     * @param categoryName  品类名称
     * @return
     */
    ServerResponse<String> addCategory(Integer parentId, String categoryName);

    /**
     * 根据商品品类id修改品类名称
     * @param categoryId    品类id
     * @param categoryName  品类名称
     * @return
     */
    ServerResponse<String> updateCategoryNameById(Integer categoryId, String categoryName);

    /**
     * 根据父品类id获取其下一级子节点
     * @return
     */
    ServerResponse<List<Category>> getChildrenByParentId(Integer parentId);

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param id
     * @return
     */
    ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer id);
}
