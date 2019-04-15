package com.jt.manage.controller.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.RedisService;
import com.jt.manage.pojo.Item;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("/web/item")
public class WebItemController {
	public String ITEM_KEY = "ITEM_";
	@Autowired
	private ItemService itemService;
	@Autowired
	private RedisService redisService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	//根据id获取商品详细信息
	@RequestMapping("/{itemId}")
	@ResponseBody	//httpclient返回值都是json串
	public Item getItemById(@PathVariable Long itemId) {
		//增加商品缓存
		String jsonData = redisService.get(ITEM_KEY+itemId);
		//底下代码多余转换2次，但为了架构统一性，实际中就是这样做
		if(StringUtils.isNotEmpty(jsonData)){
			Item item;
			try {
				//缓存必须try/catch，因为它不能暂停我们的工作
				item = MAPPER.readValue(jsonData, Item.class);
				return item;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		Item item = itemService.queryById(itemId);
		//写入缓存
		try {
			redisService.set(ITEM_KEY+itemId, MAPPER.writeValueAsString(item));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return item;
	}
}
