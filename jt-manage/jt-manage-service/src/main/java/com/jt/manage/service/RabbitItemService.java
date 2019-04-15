package com.jt.manage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.common.service.RedisService;

@Service	//rabbitmq侦听自动触发
public class RabbitItemService {
	@Autowired
	private RedisService redisService;
	
	public void updateItem(Long itemId){
		//更新缓存
		redisService.del("ITEM_"+itemId);
	}
}
