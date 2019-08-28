package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.Product;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IFileService;
import cn.fulgens.mmall.service.IProductService;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.utils.LoginUtil;
import cn.fulgens.mmall.utils.PropertiesUtil;
import cn.fulgens.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
* @Author: fulgens
* @Description: 后台商品管理Controller
* @Date: Created in 2018/2/6 13:55
* @Modified by:
*/
@RestController
@RequestMapping(value = "/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IFileService fileService;

    private static String FTP_SERVER_HTTP_PREFIX = PropertiesUtil.getProperty("ftp.server.http.prefix");

    @RequestMapping(value = "save.do")
    public ServerResponse saveProduct(Product product, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 保存或更新产品
            return productService.saveOrUpdate(product);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 产品上下架
     * @param productId
     * @param status
     * @param request
     * @return
     */
    @RequestMapping(value = "set_sale_status.do")
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 保存或更新产品
            return productService.updateStatusById(productId, status);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 产品详情
     * @param productId
     * @param request
     * @return
     */
    @RequestMapping(value = "detail.do")
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 获取产品详情
            return productService.getManageProductDetail(productId);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 产品列表
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "list.do")
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 获取产品列表
            return productService.getProductList(pageNum, pageSize);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 产品搜索（根据产品名称或产品id）
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "search.do")
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 产品搜索
            return productService.searchProduct(productName, productId, pageNum, pageSize);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 图片上传
     * @param file
     * @param request
     * @param request
     * @return
     */
    @PostMapping(value = "upload.do")
    public ServerResponse uploadImg(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                    HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
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
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 使用Simditor富文本上传图片
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping("richtext_img_upload.do")
    public Map uploadImg(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                    HttpServletRequest request, HttpServletResponse response) {
        Map simditorResultMap = Maps.newHashMap();
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            simditorResultMap.put("success", false);
            simditorResultMap.put("msg", "请登录管理员账户");
            simditorResultMap.put("file_path", null);
            return simditorResultMap;
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 图片上传
            String path = request.getSession().getServletContext().getRealPath("/upload");
            String targetFileName = fileService.uploadFile(file, path);
            if (targetFileName == null) {
                simditorResultMap.put("success", false);
                simditorResultMap.put("msg", "上传图片失败");
                simditorResultMap.put("file_path", null);
                response.addHeader("Access-Control-Allow-Headers", "X-File_Name");
                return simditorResultMap;
            }
            simditorResultMap.put("success", true);
            simditorResultMap.put("msg", "上传图片成功");
            simditorResultMap.put("file_path", FTP_SERVER_HTTP_PREFIX + targetFileName);
            return simditorResultMap;
        }else {
            simditorResultMap.put("success", false);
            simditorResultMap.put("msg", "无权限执行操作，需要管理员权限");
            simditorResultMap.put("file_path", null);
            return simditorResultMap;
        }
    }

}
