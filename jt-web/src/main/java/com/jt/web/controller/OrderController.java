package com.jt.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.web.pojo.Cart;
import com.jt.web.pojo.Order;
import com.jt.web.service.CartService;
import com.jt.web.service.OrderService;
import com.jt.web.threadlocal.UserThreadLocal;

@Controller
@RequestMapping("order")
public class OrderController {
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	
	//去结算页面，http://www.jt.com/order/create.html
	@RequestMapping("create")
	public String create(HttpServletRequest request, Model model) throws Exception{
		Long userId = UserThreadLocal.getUserId();
		
		List<Cart> cartList = cartService.queryCartList(userId);
		model.addAttribute("carts", cartList);
		
		return "order-cart";		//order-cart.jsp
	}
	
	//提交订单，/service/order/submit
	@RequestMapping("submit")
	@ResponseBody
	public SysResult submit(Order order, HttpServletRequest request){
		Long userId = UserThreadLocal.getUserId();
		order.setUserId(userId);
		Long orderId = orderService.create(order);
		
		return SysResult.ok(orderId);
	}
	
	//转向订单成功页面"/order/success.html?id="+result.data;
	@RequestMapping("success")
	public String success(@RequestParam(value="id") String orderId, Model model){
		Order _order = orderService.queryByOrderId(orderId);
		model.addAttribute("order", _order);
		
		return "success";		//success.jsp
	}
	
	//我的订单 /order/myOrder.html
	@RequestMapping("myOrder")
	public String myOrder(){
		Long userId = UserThreadLocal.getUserId();
		return "my-orders";
	}
}
