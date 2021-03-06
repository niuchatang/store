package com.zhanghang.store.service.imp;


import com.zhanghang.store.dao.GoodsDao;
import com.zhanghang.store.dao.OrderDao;
import com.zhanghang.store.dao.OrderLineItemDao;
import com.zhanghang.store.dao.imp.GoodsDaoImpJdbc;
import com.zhanghang.store.dao.imp.OrderDaoImpJdbc;
import com.zhanghang.store.dao.imp.OrderLineItemDaoImpJdbc;
import com.zhanghang.store.domain.Goods;
import com.zhanghang.store.domain.OrderLineItem;
import com.zhanghang.store.domain.Orders;
import com.zhanghang.store.service.OrdersService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrdersServiceImp implements OrdersService {

    GoodsDao goodsDao = new GoodsDaoImpJdbc();
    OrderDao orderDao = new OrderDaoImpJdbc();
    OrderLineItemDao orderLineItemDao = new OrderLineItemDaoImpJdbc();

    @Override
    public String submitOrders(List<Map<String, Object>> cart) {

        Orders orders = new Orders();
        Date date = new Date();
        // 订单Id
        String ordersid = String.valueOf(date.getTime())
                + String.valueOf((int) (Math.random() * 100));
        orders.setId(ordersid);
        orders.setOrderDate(date);
        orders.setStatus(1);
        orders.setTotal(0.0f);
        // 将订单信息插入到数据库中
        orderDao.create(orders);
        // 商品总金额
        double total = 0.0;

        for (Map item : cart) {
            // item结构 [商品id， 数量]
            Long goodsid = (Long) item.get("goodsid");
            Integer quantity = (Integer) item.get("quantity");
            Goods goods = goodsDao.findByPk(goodsid);
            // 小计金额
            double subtotal = quantity * goods.getPrice();
            total += subtotal;

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setQuantity(quantity);
            lineItem.setGoods(goods);
            lineItem.setOrders(orders);
            lineItem.setSubTotal(subtotal);

            // 将订单详细插入到数据库
            orderLineItemDao.create(lineItem);
        }

        orders.setTotal(total);
        // 更新订单数据库
        orderDao.modify(orders);

        return ordersid;
    }
}
