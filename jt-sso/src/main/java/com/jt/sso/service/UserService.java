package com.jt.sso.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.RedisService;
import com.jt.sso.mapper.UserMapper;
import com.jt.sso.pojo.User;

@Service
public class UserService extends BaseService<User>{
	@Autowired
	private RedisService redisService;
	@Autowired
	private UserMapper userMapper;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	//用户、手机、邮箱检查
	public Boolean check(String param,Integer type){
		Map<String,Object> map = new HashMap<String,Object>();
		if(type==1){
			map.put("typename", "username");
		}else if(type==2){
			map.put("typename", "phone");
		}else{
			map.put("typename", "email");
		}
		map.put("param", param);
		
		Integer num = userMapper.check(map);
		if(num>0){
			return true;
		}else{
			return false;
		}
	}
	
	//用户注册，新增
	public String register(User user){
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		user.setEmail(user.getPhone());	//故意设置，防止唯一性提升出错
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		
		userMapper.insertSelective(user);
		return user.getUsername();
	}
	
	//用户登录
	public String login(User user) throws Exception{
		/*
		 * 步骤：
		 * 1、先按username查询，获取一个用户数据（username强制设置唯一性校验）
		 * 2、获取的user对象拿起密码和password进行md5加密后比较
		 * 3、如果密码相等，系统中的用户，继续执行业务；如果不相等直接返回null（不能去抛出）返回给调用者，调用者根据null来转向登录页面
		 * 4、生成ticket（算法）
		 * 5、写入redis中
		 * 6、返回
		 */
		//user通用Mapper来生成where自动，规则：当这个字段不为null时就写入到where中，如果为null就不写
		User param = new User();
		param.setUsername(user.getUsername());
		
		User curUser = super.queryByWhere(param);	//获取到当前用户
		String newPassword = DigestUtils.md5Hex(user.getPassword());	//将页面明码加密
		if(curUser.getPassword().equals(newPassword)){
			String ticket = DigestUtils.md5Hex("TICKET_"+user.getUsername()+user.getId()+System.currentTimeMillis());	//唯一性，动态性，不可逆md5hash,其它不常用加密算法
			redisService.set(ticket, MAPPER.writeValueAsString(curUser), 60*60*24*10);	//电商10天，java编译，直接结果
			
			return ticket;
		}else{
			return null;
		}
	}
}
