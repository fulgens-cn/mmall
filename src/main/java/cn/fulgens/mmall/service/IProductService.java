package cn.fulgens.mmall.service;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.Product;
import cn.fulgens.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

/**
* @Author: fulgens
* @Description: 商品服务接口
* @Date: Created in 2018/2/6 14:01
* @Modified by:
*/
public interface IProductService {

    /**
     * 保存或更新商品
     * @param product
     * @return
     */
    ServerResponse saveOrUpdate(Product product);

    /**
     * 根据产品id更新产品销售状态（在售、下架、删除）
     * @param productId 产品id
     * @param status    产品状态
     * @return
     */
    ServerResponse<String> updateStatusById(Integer productId, Integer status);

    /**
     * 根据产品id获取后台需要的产品详情
     * @param productId
     * @return
     */
    ServerResponse<ProductDetailVo> getManageProductDetail(Integer productId);

    /**
     * 获取产品分页列表
     * @param pageNum   当前页页码
     * @param pageSize  每页显示条数
     * @return
     */
    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    /**
     * 根据产品名称或产品id搜索商品
     * @param productName   产品名称
     * @param productId     产品id
     * @param pageNum       当前页页码
     * @param pageSize      每页显示条数
     * @return
     */
    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize);

    /**
     * 根据产品id获取前台需要的产品详情
     * @param productId
     * @return
     */
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    /**
     * 根据关键词（产品名）、产品品类id获取产品列表，并排序
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    ServerResponse<PageInfo> getListByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);
}
