package cn.fulgens.mmall.service.impl;

import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.dao.ShippingMapper;
import cn.fulgens.mmall.pojo.Shipping;
import cn.fulgens.mmall.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int count = shippingMapper.insert(shipping);
        if (count > 0) {
            Map<String, Integer> map = Maps.newHashMap();
            map.put("shippingId", shipping.getId());
            return ServerResponse.successWithMsgAndData("新增收货地址成功", map);
        }
        return ServerResponse.errorWithMsg("新增收货地址失败");
    }

    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        if (userId == null || shippingId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int count = shippingMapper.deleteByIdAndUserId(shippingId, userId);
        if (count > 0) {
            return ServerResponse.successWithMsg("删除收货地址成功");
        }
        return ServerResponse.errorWithMsg("删除收货地址失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        if (userId == null || shipping == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);
        int count = shippingMapper.updateByShipping(shipping);
        if (count > 0) {
            return ServerResponse.successWithMsg("更新收货地址成功");
        }
        return ServerResponse.errorWithMsg("更新收货地址失败");
    }

    @Override
    public ServerResponse<Shipping> get(Integer userId, Integer shippingId) {
        if (userId == null || shippingId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByIdAndUserId(shippingId, userId);
        if (shipping != null) {
            return ServerResponse.successWithData(shipping);
        }
        return ServerResponse.errorWithMsg("未能查询到地址");
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.successWithData(pageInfo);
    }
}
