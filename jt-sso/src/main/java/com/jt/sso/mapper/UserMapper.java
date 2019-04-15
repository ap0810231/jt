package com.jt.sso.mapper;

import java.util.Map;

import com.jt.common.mapper.SysMapper;
import com.jt.sso.pojo.User;

public interface UserMapper extends SysMapper<User>{
	public Integer check(Map<String,Object> map);
}
