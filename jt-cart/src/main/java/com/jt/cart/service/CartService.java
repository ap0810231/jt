package com.jt.cart.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.cart.mapper.CartMapper;
import com.jt.cart.pojo.Cart;
import com.jt.cart.pojo.Item;
import com.jt.common.service.HttpClientService;
import com.jt.common.spring.exetend.PropertyConfig;

@Service
public class CartService extends BaseService<Cart>{
	@Autowired
	private HttpClientService httpClientService;
	@Autowired
	private CartMapper cartMapper;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	@PropertyConfig
	private String MANAGE_URL;
	
	//我的购物车
	public List<Cart> queryCartList(Long userId){
		Cart param = new Cart();
		param.setUserId(userId);
		
		return cartMapper.select(param);
	}
	
	//保存商品到购物车，num页面提交商品数量
	public void saveCart(Long userId, Long itemId, Integer num) throws Exception{
		/*
		 * 步骤：
		 * 1、判断此用户的此商品是否购物车中已经存在
		 * 2、如果存在，进行修改，数量+1
		 * 3、如果不存在，先获取三个冗余字段，通过httpClient去后台系统中访问
		 * 4、把获取信息和已知信息合并Cart，然后保存
		 */
		Cart param = new Cart();
		param.setUserId(userId);
		param.setItemId(itemId);
		
		Cart cart = super.queryByWhere(param);
		if(null==cart){	//不存在
			//到后台系统中获取3个冗余字段
			String url = MANAGE_URL+"/web/item/"+itemId;
			String jsonData = httpClientService.doGet(url, "utf-8");
			Item item = MAPPER.readValue(jsonData, Item.class);
			
			//新增
			Cart curCart = new Cart();
			curCart.setUserId(userId);
			curCart.setItemId(itemId);
			curCart.setItemTitle(item.getTitle());
			try{
				curCart.setItemImage(item.getImage().split(",")[0]);
			}catch(Exception e){
				//todo: log
			}
			curCart.setItemPrice(item.getPrice());
			curCart.setCreated(new Date());
			curCart.setUpdated(curCart.getCreated());
			curCart.setNum(num);
			
			cartMapper.insertSelective(curCart);
		}else{
			//存在，
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("num", cart.getNum() + num);	//数量=旧的数量+页面提交新的数量
			map.put("userId", userId);
			map.put("itemId", itemId);
			
			cartMapper.updateNum(map);
		}
	}
	
	//修改某个用户的某个商品的数量，商品数量就是页面提交数量
	public void updateNum(Cart cart){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("num", cart.getNum());	//数量=旧的数量+页面提交新的数量
		map.put("userId", cart.getUserId());
		map.put("itemId", cart.getItemId());

		cartMapper.updateNum(map);
	}
	
	//删除某个用户的商品
	public void deleteCart(Long userId, Long itemId){
		Cart param = new Cart();
		param.setItemId(itemId);
		param.setUserId(userId);
		
		cartMapper.delete(param);
	}
}
