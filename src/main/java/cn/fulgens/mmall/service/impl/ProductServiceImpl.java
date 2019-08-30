package cn.fulgens.mmall.service.impl;

import cn.fulgens.mmall.common.Constants;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.exception.ValidationFailureException;
import cn.fulgens.mmall.mapper.CategoryMapper;
import cn.fulgens.mmall.mapper.ProductMapper;
import cn.fulgens.mmall.pojo.Category;
import cn.fulgens.mmall.pojo.Product;
import cn.fulgens.mmall.service.ICategoryService;
import cn.fulgens.mmall.service.IProductService;
import cn.fulgens.mmall.common.utils.JodaTimeUtil;
import cn.fulgens.mmall.common.utils.PropertiesUtil;
import cn.fulgens.mmall.vo.ProductDetailVo;
import cn.fulgens.mmall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @Author: fulgens
* @Description: 商品服务实现类
* @Date: Created in 2018/2/6 14:03
* @Modified by:
*/
@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService categoryService;

    @Override
    public ServerResponse saveOrUpdate(Product product) {
        if (product == null) {
            return ServerResponse.errorWithMsg("保存或更新商品参数错误");
        }
        if (product.getSubImages() != null) {
            String[] subImages = product.getSubImages().split(",");
            // 设置商品主图
            product.setMainImage(subImages[0]);
        }
        if (product.getId() == null) {
            // 保存商品
            int count = productMapper.insert(product);
            if (count > 0) {
                return ServerResponse.successWithMsg("新增产品成功");
            }
            return ServerResponse.errorWithMsg("新增产品失败");
        }else {
            // 更新商品
            int count = productMapper.updateByPrimaryKeySelective(product);
            if (count > 0) {
                return ServerResponse.successWithMsg("更新产品成功");
            }
            return ServerResponse.errorWithMsg("更新产品失败");
        }
    }

    @Override
    public ServerResponse<String> updateStatusById(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.successWithMsg("修改产品状态参数错误");
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count > 0) {
            return ServerResponse.successWithMsg("修改产品状态成功");
        }
        return ServerResponse.errorWithMsg("修改产品状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> getManageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.errorWithMsg("产品已下架或已删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.successWithData(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(JodaTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(JodaTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        // 取分页信息
        PageInfo pageInfo = new PageInfo(productList);
        return ServerResponse.successWithData(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setStatus(product.getStatus());
        productListVo.setPrice(product.getPrice());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameOrId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        // 取分页信息
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.successWithData(pageInfo);
    }

    @Cacheable(value = "product", key = "#productId", unless = "#result == null")
    @Override
    public ProductDetailVo getProductDetail(Integer productId) {
        if (productId == null) {
            throw new ValidationFailureException("产品id不能为空");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus() != Constants.ProductStatusEnum.ON_SALE.getCode()) {
            throw new ValidationFailureException("产品已下架或已删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getListByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (keyword == null && categoryId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.successWithData(pageInfo);
            }
            categoryIdList = categoryService.getCategoryAndChildrenById(categoryId).getData();
        }
        if (keyword != null) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        if (orderBy != null) {
            if (Constants.orderBySet.contains(orderBy)) {
                String[] split = orderBy.split("_");
                PageHelper.orderBy(split[0] + " " + split[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIdList(keyword == null ? null : keyword,
                categoryIdList == null ? null : categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo =  assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.successWithData(pageInfo);
    }
}
