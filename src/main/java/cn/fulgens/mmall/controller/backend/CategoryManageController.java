package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.Category;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.ICategoryService;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.utils.LoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
* @Author: fulgens
* @Description: 后台商品分类Controller
* @Date: Created in 2018/2/5 22:35
* @Modified by:
*/
@RestController
@RequestMapping(value = "/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping(value = "add_category.do")
    public ServerResponse<String> addCategory(@RequestParam(value = "parentId", defaultValue = "0") Integer parentId,
                                              String categoryName, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 添加商品品类
            return categoryService.addCategory(parentId, categoryName);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    @GetMapping(value = "set_category_name.do")
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName,
                                                     HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 更新商品品类名称
            return categoryService.updateCategoryNameById(categoryId, categoryName);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    @GetMapping(value = "get_category.do")
    public ServerResponse<List<Category>> getCategory(@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId,
                                                      HttpServletRequest request
    ) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 根据父品类id获取下一级品类子节点
            return categoryService.getChildrenByParentId(categoryId);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    @GetMapping(value = "get_deep_category.do")
    public ServerResponse<List<Integer>> getDeepCategory(@RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId,
                                                         HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 获取当前分类id及递归子节点categoryId
            return categoryService.getCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }
}
