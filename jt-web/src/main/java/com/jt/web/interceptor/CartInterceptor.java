package com.jt.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.util.CookieUtils;
import com.jt.web.controller.UserController;
import com.jt.web.pojo.User;
import com.jt.web.threadlocal.UserThreadLocal;

//购物车拦截器
public class CartInterceptor implements HandlerInterceptor{
	@Autowired
	private HttpClientService httpClientService;
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override	//执行controller方法前执行
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		/*
		 * 步骤：
		 * 1、从cookie中获取ticket
		 * 2、从sso业务接口去访问redis获取当前用户
		 * 3、将用户转成user对象，保存UserThreadLocal，线程保护，防止并发问题
		 */
		String ticket = CookieUtils.getCookieValue(request, UserController.JT_TICKET);
		if(StringUtils.isNotEmpty(ticket)){
			String url = "http://sso.jt.com/user/query/"+ticket;
			String jsonData = httpClientService.doGet(url, "utf-8");
			if(StringUtils.isNotEmpty(jsonData)){
				JsonNode jsonNode = MAPPER.readTree(jsonData);
				JsonNode userNode = jsonNode.get("data");
				User curUser = MAPPER.readValue(userNode.asText(), User.class);
				UserThreadLocal.set(curUser);
				return true;	//false不放行，true放行
			}else{
				UserThreadLocal.set(null);
			}
		}else{
			UserThreadLocal.set(null);
		}
		
		//如果没有登录，跳转到登录页面
		response.sendRedirect("/user/login.html");
		return false;
	}

	@Override	//执行controller方法后执行
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override	//执行controller方法后执行，返回页面前，render渲染（model）
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
