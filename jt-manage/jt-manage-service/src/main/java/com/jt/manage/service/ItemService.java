package com.jt.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.common.service.RedisService;
import com.jt.common.spring.exetend.PropertyConfig;
import com.jt.common.vo.EasyUIResult;
import com.jt.manage.mapper.ItemDescMapper;
import com.jt.manage.mapper.ItemMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;

@Service
public class ItemService extends BaseService<Item>{
	@Autowired
	private RedisService redisService;
	@Autowired
	private ItemMapper itemMapper; 
	@Autowired
	private ItemDescMapper itemDescMapper;
	@Autowired
	private RabbitTemplate rabbitTemplate;	//整合spring框架后，spring会自动创建
	
	@PropertyConfig	//自定义注解，读取配置文件中的变量，把值赋值给下面的成员变量
	public String MANAGE_URL;
	@PropertyConfig	
	public String IMAGE_BASE_URL;
	
	public EasyUIResult queryItemList(Integer page, Integer rows){
		//分页
		PageHelper.startPage(page, rows, true);	//代表分页开启
		List<Item> itemList = itemMapper.queryItemList();	//业务调用，它被拦截，结果是分页的结果
		//List<Item> itemList2 = itemMapper.queryItemList();	//业务调用，它被拦截，结果是分页的结果
		PageInfo<Item> pageInfo = new PageInfo<Item>(itemList);
		
		return new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
	}
	
	//商品新增
	public void saveItem(Item item, String desc){
		item.setStatus(1);		//1正常2下架3删除
		item.setCreated(new Date());
		item.setUpdated(item.getCreated());
		
		//数据库新增完成后，mybatis+mysql框架把新增的值回显（设置到item对象）
		itemMapper.insertSelective(item);
		
		//保存商品详情
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(item.getCreated());
		itemDesc.setUpdated(item.getCreated());
		
		itemDescMapper.insert(itemDesc);
	}
	
	//商品修改
	public void updateItem(Item item, String desc){
		item.setUpdated(new Date());
		itemMapper.updateByPrimaryKeySelective(item);
		
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(item.getUpdated());
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
		
		//更新缓存
		//redisService.del("ITEM_"+item.getId());
		
		//发送消息
		String routingKey = "item.update";
		rabbitTemplate.convertAndSend(routingKey, item.getId());
	}
	
	//获取商品详情
	public ItemDesc getItemDesc(Long itemId){
		return itemDescMapper.selectByPrimaryKey(itemId);	//一对一，设置成主外键一致
	}
	
	//级联删除，先删除子对象，然后删除主对象
	public void deleteItem(String[] ids){
		itemDescMapper.deleteByIDS(ids);
		itemMapper.deleteByIDS(ids);
		
		//删除缓存
		for(String id : ids){
			redisService.del("ITEM_"+id);
		}
	}
}
