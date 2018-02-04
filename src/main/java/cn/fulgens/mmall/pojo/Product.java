package cn.fulgens.mmall.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class Product {
    private Integer id;

    private String title;

    private String subtitle;

    private String mainImage;

    private String subImages;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String detail;

    private Integer categoryId;

    private Date createTime;

    private Date updateTime;

    public Product(Integer id, String title, String subtitle, String mainImage, String subImages, BigDecimal price, Integer stock, Integer status, String detail, Integer categoryId, Date createTime, Date updateTime) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.mainImage = mainImage;
        this.subImages = subImages;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.detail = detail;
        this.categoryId = categoryId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Product() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle == null ? null : subtitle.trim();
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage == null ? null : mainImage.trim();
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages == null ? null : subImages.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}