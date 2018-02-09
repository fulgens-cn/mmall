package cn.fulgens.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {

    // 购物车中产品集合
    private List<CartProductVo> cartProductVoList;

    // 购物车中产品是否处于全选状态
    private boolean allChecked;

    // 购物车中已勾选产品总价
    private BigDecimal cartTotalPrice;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }
}
