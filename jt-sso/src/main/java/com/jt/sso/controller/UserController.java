package com.jt.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.service.RedisService;
import com.jt.common.vo.SysResult;
import com.jt.sso.pojo.User;
import com.jt.sso.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private RedisService redisService;
	@Autowired
	private UserService userService;
	
	//http://sso.jt.com/user/check/{param}/{type}
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public SysResult check(@PathVariable String param,@PathVariable Integer type){
		Boolean b = userService.check(param, type);
		return SysResult.ok(b);
	}
	
	//http://sso.jt.com/user/register
	@RequestMapping("/register")
	@ResponseBody
	public SysResult register(User user){
		String username = userService.register(user);
		return SysResult.ok(username);
	}
	
	//http://sso.jt.com/user/login
	@RequestMapping("/login")
	@ResponseBody
	public SysResult login(String u, String p) throws Exception{
		User _user = new User();
		_user.setUsername(u);
		_user.setPassword(p);
		
		String ticket = userService.login(_user);
		return SysResult.ok(ticket);
	}
	
	//http://sso.jt.com/user/query/{ticket}
	//直接到redis中按ticket查询
	@RequestMapping("query/{ticket}")
	@ResponseBody
	public SysResult queryByTicket(@PathVariable String ticket){
		String userJson = redisService.get(ticket);
		
		return SysResult.ok(userJson);
	}
}
