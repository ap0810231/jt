package com.jt.manage.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.spring.exetend.PropertyConfig;
import com.jt.common.vo.EasyUIResult;
import com.jt.common.vo.SysResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("item")
public class ItemController {
	//引入Log4j
	private static final Logger log = Logger.getLogger(ItemController.class);
	
	@Autowired
	private ItemService itemService;
	
	@PropertyConfig
	public String MANAGE_URL;
	
	@RequestMapping("query")
	@ResponseBody		//商品列表，分页
	public EasyUIResult queryItemList(Integer page, Integer rows){
		return itemService.queryItemList(page, rows);
	}
	
	//商品新增	/item/save
	@RequestMapping("save")
	@ResponseBody
	public SysResult save(Item item, String desc){
		try{
			itemService.saveItem(item, desc);
			log.info("新增商品成功!");
			return SysResult.ok();
		}catch(Exception e){
			//写日志 log4j
			log.error(e.getMessage());
			return SysResult.build(201, "新增保存出错，原因："+e.getMessage());
		}
	}
	
	//商品修改	/item/update
	@RequestMapping("update")
	@ResponseBody
	public SysResult update(Item item, String desc){
		try{
			itemService.updateItem(item, desc);
			return SysResult.ok();
		}catch(Exception e){
			log.error(e.getMessage());
			return SysResult.build(201, "修改出现错误!");
		}
	}
	
	//商品删除，批量删除	/item/delete
	@RequestMapping("delete")
	@ResponseBody
	public SysResult deleteItem(String[] ids){
		try{
			itemService.deleteItem(ids);
			return SysResult.ok();
		}catch(Exception e){
			log.error(e.getMessage());
			return SysResult.build(201, "删除出现错误!");
		}
	}
	
	//获取某个商品的详情 /item/query/item/desc/'+data.id
	@RequestMapping("/query/item/desc/{itemId}")
	@ResponseBody
	public SysResult getItemDesc(@PathVariable Long itemId){
		ItemDesc itemDesc = itemService.getItemDesc(itemId);
		try{
			return SysResult.ok(itemDesc);
		}catch(Exception e){
			log.error(e.getMessage());
			return SysResult.build(201, e.getMessage());
		}
	}
}
