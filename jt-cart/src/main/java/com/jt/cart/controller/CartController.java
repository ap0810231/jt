package com.jt.cart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.cart.pojo.Cart;
import com.jt.cart.service.CartService;
import com.jt.common.vo.SysResult;

@Controller
@RequestMapping("/cart")
public class CartController{
	@Autowired
	private CartService cartService;
	
	//我的购物车 http://cart.jt.com/cart/query/{userId}
	@RequestMapping("/query/{userId}")
	@ResponseBody
	public SysResult queryCartList(@PathVariable Long userId){
		List<Cart> cartList = cartService.queryCartList(userId);
		return SysResult.ok(cartList);
	}
	
	//保存商品到购物车	http://cart.jt.com/cart/save
	@RequestMapping("/save")
	@ResponseBody
	public SysResult saveCart(Cart cart) throws Exception{
		cartService.saveCart(cart.getUserId(), cart.getItemId(), cart.getNum());
		return SysResult.ok();
	}
	
	//更新商品数量 http://cart.jt.com/cart/update/num/{userId}/{itemId}/{num}
	@RequestMapping("update/num/{userId}/{itemId}/{num}")
	@ResponseBody
	public SysResult updateCart(@PathVariable Long userId,@PathVariable Long itemId,@PathVariable Integer num){
		Cart param = new Cart();
		param.setUserId(userId);
		param.setItemId(itemId);
		param.setNum(num);
		
		cartService.updateNum(param);
		return SysResult.ok();
	}
	
	//商品删除 http://cart.jt.com/cart/delete/{userId}/{itemId}
	@RequestMapping("delete/{userId}/{itemId}")
	@ResponseBody
	public SysResult deleteCart(@PathVariable Long userId,@PathVariable Long itemId){
		cartService.deleteCart(userId, itemId);
		return SysResult.ok();
	}
}
