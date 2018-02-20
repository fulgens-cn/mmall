package cn.fulgens.mmall.service;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

public interface IShippingService {

    /**
     * 添加收货地址
     * @param userId    用户id
     * @param shipping  收货地址对象
     * @return
     */
    ServerResponse add(Integer userId, Shipping shipping);

    /**
     * 删除收货地址
     * @param userId        用户id
     * @param shippingId    收货地址id
     * @return
     */
    ServerResponse delete(Integer userId, Integer shippingId);

    /**
     * 更新收货地址
     * @param userId    用户id
     * @param shipping  收货地址对象
     * @return
     */
    ServerResponse update(Integer userId, Shipping shipping);

    /**
     * 获取收货地址
     * @param userId        用户id
     * @param shippingId    收货地址id
     * @return
     */
    ServerResponse<Shipping> get(Integer userId, Integer shippingId);

    /**
     * 获取收货地址分页列表
     * @param userId    用户id
     * @param pageNum   当前页页数
     * @param pageSize  每页显示条数
     * @return
     */
    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
}
