package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.Category;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.ICategoryService;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.common.utils.LoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @Author: fulgens
* @Description: 后台商品分类Controller
* @Date: Created in 2018/2/5 22:35
* @Modified by:
*/
@RestController
@RequestMapping(value = "/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping(value = "/add_category.do")
    public ServerResponse<String> addCategory(@RequestParam(value = "parentId", defaultValue = "0") Integer parentId,
                                              String categoryName) {
        return categoryService.addCategory(parentId, categoryName);
    }

    @GetMapping(value = "/set_category_name.do")
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        return categoryService.updateCategoryNameById(categoryId, categoryName);
    }

    @GetMapping(value = "/get_category.do")
    public ServerResponse<List<Category>> getCategory(@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId) {
        return categoryService.getChildrenByParentId(categoryId);
    }

    @GetMapping(value = "/get_deep_category.do")
    public ServerResponse<List<Integer>> getDeepCategory(@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId) {
        return categoryService.getCategoryAndChildrenById(categoryId);
    }
}
