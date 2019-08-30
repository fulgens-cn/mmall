package cn.fulgens.mmall.controller.portal;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.service.IProductService;
import cn.fulgens.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @RequestMapping(value = "/detail.do")
    public ServerResponse<ProductDetailVo> getDetail(Integer productId) {
        return ServerResponse.successWithMsgAndData("获取产品详情信息成功", productService.getProductDetail(productId));
    }

    @RequestMapping(value = "/list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword", required = false)String keyword,
                                         @RequestParam(value = "categoryId", required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize,
                                         @RequestParam(value = "orderBy")String orderBy) {
        return productService.getListByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }

}
