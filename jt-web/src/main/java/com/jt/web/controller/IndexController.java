package com.jt.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	/*
	 * 访问前台首页
	 * 用户访问地址：http://www.jt.com/index.html
	 * 访问下面的方法：
	 */
	@RequestMapping("/index")
	public String index(){
		return "index";
	}
}
