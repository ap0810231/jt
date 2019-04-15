package com.jt.manage.controller;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.pojo.ItemCat;
import com.jt.manage.service.ItemCatService;

@Controller
@RequestMapping("/item/cat")
public class ItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	//查询分类表的所有数据，返回json（废除，检验架构）
	@RequestMapping("/all")
	@ResponseBody	//会自动转换当前的java对象为json字符串，背后ObjectMapper，fastxml，jackjson
	public List<ItemCat> queryAll(){
		List<ItemCat> itemCatList = itemCatService.queryAll();
		return itemCatList;
	}
	
	//获取商品分类树json串
	@RequestMapping("/list")	//EasyUI异步加载树，点击当前节点时，会自动把当前节点的id值，作为一个id参数传递过来
	@ResponseBody	//设置默认值，第一次访问时这时为0，约定	
	public List<ItemCat> queryByPid(@RequestParam(defaultValue="0") Integer id) throws IOException{
		return itemCatService.queryByPid(id);
	}
}
