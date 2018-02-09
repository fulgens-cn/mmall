package cn.fulgens.mmall.service;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.vo.CartVo;

public interface ICartService {

    /**
     * 添加超到购物车
     * @param userId    用户id
     * @param productId 产品id
     * @param count     购买数量
     * @return
     */
    ServerResponse<CartVo> addProductToCart(Integer userId, Integer productId, Integer count);

    /**
     * 更新购物车中产品数量
     * @param userId    用户id
     * @param productId 产品id
     * @param count     产品数量
     * @return
     */
    ServerResponse<CartVo> updateProductCountInCart(Integer userId, Integer productId, Integer count);

    /**
     * 从购物车中删除指定产品
     * @param userId        用户id
     * @param productIds    多个产品id,以","分割
     * @return
     */
    ServerResponse<CartVo> deleteProductInCart(Integer userId, String productIds);

    /**
     * 根据用户id获取对应购物车中产品列表
     * @param userId    用户id
     * @return
     */
    ServerResponse<CartVo> getProductListInCart(Integer userId);

    /**
     * 选择或取消选择购物车中产品
     * @param userId    用户id
     * @param checked   是否选择
     * @param productId 产品id,非必需,null时代表全选或取消全选
     * @return
     */
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, boolean checked, Integer productId);

    /**
     * 获取购物车中产品总数（相同产品重复计数）
     * @param userId    用户id
     * @return
     */
    ServerResponse<Integer> getProductCountInCart(Integer userId);
}
