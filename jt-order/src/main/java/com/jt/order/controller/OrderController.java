package com.jt.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.order.pojo.Order;
import com.jt.order.service.OrderService;

@Controller
@RequestMapping("order")
public class OrderController {
	@Autowired
	private OrderService orderService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	//创建订单	http://order.jt.com/order/create
	//@RequestBody 将json参数的值写入order对象中和关联对象中
	@RequestMapping("create")
	@ResponseBody
	public String create(@RequestBody Order _orderx){
		try {
			String json="";
			Order _order = MAPPER.readValue(json, Order.class);
			//订单号：userId+当前时间
			String orderId = _order.getUserId()+""+System.currentTimeMillis();
			_order.setOrderId(orderId);
			orderService.create(_order);
			
			return orderId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//查询某个订单 http//order.jt.com/order/query/81425700649826
	@RequestMapping("query/{orderId}")
	@ResponseBody
	public Order query(@PathVariable String orderId){
		return orderService.query(orderId);
	}
}
