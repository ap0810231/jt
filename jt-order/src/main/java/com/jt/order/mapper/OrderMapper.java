package com.jt.order.mapper;

import java.util.Date;

import com.jt.order.pojo.Order;

public interface OrderMapper {
	public void orderCreate(Order order);
	public Order queryOrderByOrderId(String orderId);
	
	public void paymentOrderScan(Date date);
}
