package com.jt.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.spring.exetend.PropertyConfig;
import com.jt.web.pojo.Cart;

@Service
public class CartService {
	@Autowired
	private HttpClientService httpClientService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	@PropertyConfig
	private String CART_URL;
	
	//http://cart.jt.com/cart/query/{userId}
	public List<Cart> queryCartList(Long userId) throws Exception{
		String url = CART_URL+"/cart/query/"+userId;
		String jsonData = httpClientService.doGet(url);
		JsonNode jsonNode = MAPPER.readTree(jsonData);
		JsonNode cartListJsonNode = jsonNode.get("data");
		
		List<Cart> cartList = MAPPER.readValue(cartListJsonNode.traverse(),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
		return cartList;
	}
	
	public void saveCart(Long userId, Long itemId, Integer num) throws Exception{
		String url = CART_URL+"/cart/save/";
		Map<String,String> params = new HashMap<String,String>();
		params.put("userId", userId+"");
		params.put("itemId", itemId+"");
		params.put("num", num+"");
		
		httpClientService.doPost(url, params, "utf-8");
	}
	
	//http://cart.jt.com/cart/update/num/{userId}/{itemId}/{num}
	public void updateNum(Long userId, Long itemId, Integer num) throws Exception{
		String url = CART_URL+"/cart/update/num/"+userId+"/"+itemId+"/"+num;
		httpClientService.doGet(url, "utf-8");
	}
	
	//http://cart.jt.com/cart/delete/{userId}/{itemId}
	public void delete(Long userId, Long itemId) throws Exception{
		String url = CART_URL+"/cart/delete/"+userId+"/"+itemId;
		httpClientService.doGet(url, "utf-8");
	}
}
