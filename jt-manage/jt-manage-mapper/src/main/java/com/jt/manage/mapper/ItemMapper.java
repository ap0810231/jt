package com.jt.manage.mapper;

import java.util.List;

import com.jt.common.mapper.SysMapper;
import com.jt.manage.pojo.Item;

public interface ItemMapper extends SysMapper<Item>{
	public List<Item> queryItemList();
}
