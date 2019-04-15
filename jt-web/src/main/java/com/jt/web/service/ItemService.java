package com.jt.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.service.RedisService;
import com.jt.web.pojo.Item;

@Service
public class ItemService {
	public String ITEM_KEY = "ITEM_";
	@Autowired
	private RedisService redisService;
	@Autowired
	private HttpClientService httpClientService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public Item getItemById(Long itemId) throws Exception{
		//增加缓存
		try{
			String jsonData = redisService.get(ITEM_KEY+itemId);
			if(StringUtils.isNotEmpty(jsonData)){
				Item item = MAPPER.readValue(jsonData, Item.class);
				return item;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//通过httpClient发起http请求，请求后台
		String url = "http://manage.jt.com/web/item/"+itemId;
		//注意有超时的问题，
		String jsonData = httpClientService.doGet(url, "utf-8");
		//把json串转成单个pojo对象
		Item item = MAPPER.readValue(jsonData, Item.class);
		
		//写缓存
		try{
			redisService.set(ITEM_KEY+itemId, jsonData);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return item;
	}
}
