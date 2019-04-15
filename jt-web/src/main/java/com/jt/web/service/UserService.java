package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.web.pojo.User;

@Service
public class UserService {
	@Autowired
	private HttpClientService httpClientService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public String doRegister(User user) throws Exception{
		//发起httpClient
		String url = "http://sso.jt.com:8083/user/register";
		Map<String,String> params = new HashMap<String,String>();
		params.put("username", user.getUsername());
		params.put("password", user.getPassword());
		params.put("phone", user.getPhone());
		params.put("email", user.getEmail());
		
		//只获取json串对象中的某一个值，为什么不直接转换成SysResult，SysResult写不好，不能强转
		String jsonData = httpClientService.doPost(url, params, "utf-8");
		JsonNode jsonNode = MAPPER.readTree(jsonData);
		String username = jsonNode.get("data").asText();
		return username;
	}
	
	public String doLogin(String username, String password) throws Exception{
		String url = "http://sso.jt.com:8083/user/login";
		Map<String,String> params = new HashMap<String,String>();
		params.put("u", username);
		params.put("p", password);
		
		String jsonData = httpClientService.doPost(url, params, "utf-8");
		JsonNode jsonNode = MAPPER.readTree(jsonData);
		String ticket = jsonNode.get("data").asText();
		return ticket;
	}
}
