package com.jt.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.util.CookieUtils;
import com.jt.common.vo.SysResult;
import com.jt.web.pojo.User;
import com.jt.web.service.UserService;
import com.jt.web.threadlocal.UserThreadLocal;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	public static final String JT_TICKET = "JT_TICKET";
	
	//转向注册页面 http://www.jt.com/user/register.html
	@RequestMapping("/register")
	public String register(){
		return "register";
	}
	
	//转向登录页面 http://www.jt.com/user/login.html
	@RequestMapping("/login")
	public String login(){
		return "login";
	}
	
	//注册 /user/doRegister
	@RequestMapping("/doRegister")
	@ResponseBody
	public SysResult doRegister(User user) throws Exception{
		String username = userService.doRegister(user);
		return SysResult.ok(username);
	}
	
	//登录，/service/user/doLogin
	@RequestMapping("/doLogin")
	@ResponseBody
	public SysResult doLogin(String username, String password, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String ticket = userService.doLogin(username, password);
		//写入到cookie中，前台首页jt.js就可以访问
		CookieUtils.setCookie(request, response, JT_TICKET, ticket);
		
		return SysResult.ok(ticket);
	}
	
	//登出，http://www.jt.com/user/logout.html
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response){
		CookieUtils.deleteCookie(request, response, JT_TICKET);
		UserThreadLocal.set(null);
		
		return "index";
	}
}
