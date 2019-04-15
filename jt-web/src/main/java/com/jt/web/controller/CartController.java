package com.jt.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.web.pojo.Cart;
import com.jt.web.service.CartService;
import com.jt.web.threadlocal.UserThreadLocal;

@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private CartService cartService;
	
	//我的购物车	/cart/show.html
	@RequestMapping("/show")
	public String myCart(Model model) throws Exception{
		//Long userId = 1L;
		Long userId = UserThreadLocal.getUserId();
		List<Cart> cartList = cartService.queryCartList(userId);
		model.addAttribute("cartList", cartList);
		
		return "cart";
	}
	
	//添加商品到购物车 http://www.jt.com/cart/add/${item.id}.html
	@RequestMapping("/add/{itemId}")
	public String addCart(@PathVariable Long itemId, Integer num) throws Exception{
		//Long userId = 1L;
		Long userId = UserThreadLocal.getUserId();
		cartService.saveCart(userId, itemId, num);
		
		return "redirect:/cart/show.html";	//重新定向，相当于浏览器输入内容
	}
	
	//+-数量，"/service/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val()
	@RequestMapping("/update/num/{itemId}/{num}")
	@ResponseBody	//凑结构，
	public String updateNum(@PathVariable Long itemId, @PathVariable Integer num) throws Exception{
		//Long userId = 1L;
		Long userId = UserThreadLocal.getUserId();
		cartService.updateNum(userId, itemId, num);
		
		return "";
	}
	
	//删除	/cart/delete/1474391948.html
	@RequestMapping("/delete/{itemId}")
	public String delete(@PathVariable Long itemId) throws Exception{
		//Long userId = 1L;
		Long userId = UserThreadLocal.getUserId();
		cartService.delete(userId, itemId);
		
		return "redirect:/cart/show.html";
	}
	
}
