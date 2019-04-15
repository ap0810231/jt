package com.jt.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	//通用转向jsp页面的方法
	@RequestMapping("/page/{pageName}") //利用RESTFul形式传参
	public String goHome(@PathVariable String pageName){
		return pageName;
	}
}
