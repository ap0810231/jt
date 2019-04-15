package com.jt.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.order.mapper.OrderMapper;
import com.jt.order.pojo.Order;

@Service
public class OrderService {
	@Autowired
	private OrderMapper orderMapper;

	public void create(Order order) {
		orderMapper.orderCreate(order);
	}

	public Order query(String orderId) {
		return orderMapper.queryOrderByOrderId(orderId);
	}
}
