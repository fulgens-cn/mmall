package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.SimditorServerResponse;
import cn.fulgens.mmall.pojo.Product;
import cn.fulgens.mmall.service.IFileService;
import cn.fulgens.mmall.service.IProductService;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.common.utils.PropertiesUtil;
import cn.fulgens.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
* @Author: fulgens
* @Description: 后台商品管理Controller
* @Date: Created in 2018/2/6 13:55
* @Modified by:
*/
@RestController
@RequestMapping(value = "/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IFileService fileService;

    private static String FTP_SERVER_HTTP_PREFIX = PropertiesUtil.getProperty("ftp.server.http.prefix");

    @RequestMapping(value = "/save.do")
    public ServerResponse saveProduct(Product product, HttpServletRequest request) {
        return productService.saveOrUpdate(product);
    }

    /**
     * 产品上下架
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "/set_sale_status.do")
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        return productService.updateStatusById(productId, status);
    }

    /**
     * 产品详情
     * @param productId
     * @return
     */
    @RequestMapping(value = "/detail.do")
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        return productService.getManageProductDetail(productId);
    }

    /**
     * 产品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list.do")
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return productService.getProductList(pageNum, pageSize);
    }

    /**
     * 产品搜索（根据产品名称或产品id）
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/search.do")
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return productService.searchProduct(productName, productId, pageNum, pageSize);
    }

    /**
     * 图片上传
     * @param file
     * @param request
     * @return
     */
    @PostMapping(value = "/upload.do")
    public ServerResponse uploadImg(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                    HttpServletRequest request) {
        // 图片上传
        String path = request.getSession().getServletContext().getRealPath("/upload");
        String targetFileName = fileService.uploadFile(file, path);
        if (targetFileName == null) {
            return ServerResponse.errorWithMsg("文件上传失败");
        }
        HashMap<String, String> resultMap = Maps.newHashMap();
        resultMap.put("uri", targetFileName);
        resultMap.put("url", FTP_SERVER_HTTP_PREFIX + targetFileName);
        return ServerResponse.successWithData(resultMap);
    }

    /**
     * 使用Simditor富文本上传图片
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/richtext_img_upload.do")
    public SimditorServerResponse uploadImg(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                            HttpServletRequest request, HttpServletResponse response) {
        // 图片上传
        String path = request.getSession().getServletContext().getRealPath("/upload");
        String targetFileName = fileService.uploadFile(file, path);
        if (targetFileName == null) {
            response.addHeader("Access-Control-Allow-Headers", "X-File_Name");
            return SimditorServerResponse.error("上传图片失败", null);
        }
        return SimditorServerResponse.success("上传图片成功", FTP_SERVER_HTTP_PREFIX + targetFileName);
    }

}
