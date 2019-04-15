package com.jt.manage.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.ItemCatResult;
import com.jt.manage.service.ItemCatService;

@Controller
@RequestMapping("/web/itemcat")
public class WebItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	//获取前台首页菜单数据
	@RequestMapping("/all")
	@ResponseBody	//配置自定义转换器，把callback进行解析，获取到名称，Object，ObjectMapper转json，把方法名拼接上去
	public ItemCatResult getItemCatAll(){
		return itemCatService.getItemCatList();
	}
}
