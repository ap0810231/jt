package com.jt.cart.mapper;

import java.util.Map;

import com.jt.cart.pojo.Cart;
import com.jt.common.mapper.SysMapper;

public interface CartMapper extends SysMapper<Cart>{
	public void updateNum(Map<String,Object> map);
}
