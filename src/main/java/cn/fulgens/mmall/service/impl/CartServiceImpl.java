package cn.fulgens.mmall.service.impl;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.dao.CartMapper;
import cn.fulgens.mmall.dao.ProductMapper;
import cn.fulgens.mmall.pojo.Cart;
import cn.fulgens.mmall.pojo.Product;
import cn.fulgens.mmall.service.ICartService;
import cn.fulgens.mmall.utils.BigDecimalUtil;
import cn.fulgens.mmall.vo.CartProductVo;
import cn.fulgens.mmall.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> addProductToCart(Integer userId, Integer productId, Integer count) {
        if (userId == null || productId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.errorWithMsg("产品已下架或已删除");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 购物车中不存在属于userId用户的productId对应产品
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(count);
            cart.setChecked(Const.Cart.CHECKED);
            // 插入数据
            cartMapper.insert(cart);
        }else {
            // 购物车中已存在属于userId用户的productId对应产品,合并购买数量
            cart.setQuantity(cart.getQuantity() + count);
            cart.setChecked(Const.Cart.CHECKED);
            // 更新购买数量
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return ServerResponse.successWithData(getCartVo(userId));
    }

    @Override
    public ServerResponse<CartVo> updateProductCountInCart(Integer userId, Integer productId, Integer count) {
        if (userId == null || productId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            return ServerResponse.errorWithMsg("购物车中没有指定商品");
        }
        cart.setQuantity(count);
        cartMapper.updateByPrimaryKeySelective(cart);
        return ServerResponse.successWithData(getCartVo(userId));
    }

    @Override
    public ServerResponse<CartVo> deleteProductInCart(Integer userId, String productIds) {
        if (userId == null || productIds == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdAndProductIdList(userId, productIdList);
        return ServerResponse.successWithData(getCartVo(userId));
    }

    @Override
    public ServerResponse<CartVo> getProductListInCart(Integer userId) {
        if (userId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return ServerResponse.successWithData(getCartVo(userId));
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, boolean checked, Integer productId) {
        if (userId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if (checked) {
            cartMapper.checkedOrUnCheckedByUserId(userId, Const.Cart.CHECKED, productId);
        } else {
            cartMapper.checkedOrUnCheckedByUserId(userId, Const.Cart.UN_CHECKED, productId);
        }
        return getProductListInCart(userId);
    }

    @Override
    public ServerResponse<Integer> getProductCountInCart(Integer userId) {
        if (userId == null) {
            return ServerResponse.successWithData(0);
        }
        int totolCount = cartMapper.selectProductCountByUserId(userId);
        return ServerResponse.successWithData(totolCount);
    }

    private CartVo getCartVo(Integer userId) {
        if (userId == null) {
            return null;
        }
        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal(0);
        // 根据用户id查询购物车列表
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                // 封装CartProductVo对象
                cartProductVo.setId(cart.getId());
                cart.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());
                cartProductVo.setProductChecked(cart.getChecked());
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    // 设置产品库存
                    cartProductVo.setProductStock(product.getStock());
                    // 获取用户购买产品数量
                    Integer buyNum = cart.getQuantity();
                    if (product.getStock() < buyNum) {
                        // 库存不足
                        buyNum = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        // 更新用户购买数量
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyNum);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }else {
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }
                    cartProductVo.setQuantity(buyNum);
                    cartProductVo.setProductChecked(cart.getChecked());
                    // 设置产品小计总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                }
                // 计算购物车总价
                if (cart.getChecked() == Const.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        // 封装CartVo
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(isAllChecked(userId));
        return cartVo;
    }

    private boolean isAllChecked(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCountNotCheckedByUserId(userId) == 0;
    }
}
