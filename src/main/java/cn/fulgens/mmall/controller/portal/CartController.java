package cn.fulgens.mmall.controller.portal;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.ICartService;
import cn.fulgens.mmall.utils.LoginUtil;
import cn.fulgens.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/cart/")
public class CartController {

    @Autowired
    private ICartService cartService;

    @RequestMapping(value = "add.do")
    public ServerResponse<CartVo> addProductToCart(Integer productId, Integer count, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.addProductToCart(user.getId(), productId, count);
    }

    @RequestMapping(value = "update.do")
    public ServerResponse<CartVo> update(Integer productId, Integer count, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.updateProductCountInCart(user.getId(), productId, count);
    }

    @RequestMapping(value = "delete_product.do")
    public ServerResponse<CartVo> deleteProduct(String productIds, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.deleteProductInCart(user.getId(), productIds);
    }

    @RequestMapping(value = "list.do")
    public ServerResponse<CartVo> list(HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.getProductListInCart(user.getId());
    }

    @RequestMapping(value = "select.do")
    public ServerResponse<CartVo> select(Integer productId, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectOrUnSelect(user.getId(), true, productId);
    }

    @RequestMapping(value = "un_select.do")
    public ServerResponse<CartVo> unSelect(Integer productId, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectOrUnSelect(user.getId(), false, productId);
    }

    @RequestMapping(value = "select_all.do")
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectOrUnSelect(user.getId(), true, null);
    }

    @RequestMapping(value = "un_select_all.do")
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectOrUnSelect(user.getId(), false, null);
    }

    @RequestMapping(value = "get_cart_product_count.do")
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return cartService.getProductCountInCart(user.getId());
    }
}
