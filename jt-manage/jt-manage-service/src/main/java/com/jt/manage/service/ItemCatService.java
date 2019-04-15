package com.jt.manage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.RedisSentinelService;
import com.jt.common.vo.ItemCatData;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.ItemCat;

@Service
public class ItemCatService extends BaseService<ItemCat>{
	@Autowired
	private ItemCatMapper itemCatMapper;
	@Autowired
	private RedisSentinelService redisService;
	
	//引入java对象和json串转换对象ObjectMapper；全局唯一
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public List<ItemCat> queryByPid(Integer id) throws IOException{
		/*
		 * 商品分类要使用缓存步骤：
		 * 1）先判断缓存中是否有数据，如果有数据就读取，直接返回
		 * 2）如果缓存中没有数据，要继续执行业务，不能抛出异常
		 * 3）执行完业务，要多一步动作，要把结果放入缓存中string，先把java对象转换成json串，kv写入缓存中。
		 */
		
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String ITEM_CAT_KEY = "ITEM_CAT_" + id;	//唯一性
		
		//从redis中获取数据
		String jsonItemCat = redisService.get(ITEM_CAT_KEY);
		if(StringUtils.isNotEmpty(jsonItemCat)){
			JsonNode jsonNode = MAPPER.readTree(jsonItemCat);	//把json串转换JsonNode
			//利用jackson提供方法，将json串转成java对象，List<Object>
			List<ItemCat> itemCatList = MAPPER.readValue(jsonNode.traverse(),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, ItemCat.class));
			return itemCatList;
		}else{
			//继续执行业务，一般都是去数据库访问
			List<ItemCat> itemCatList = itemCatMapper.queryByPid(id);
			
			//写redis，kv(string,string)
			jsonItemCat = MAPPER.writeValueAsString(itemCatList);	//将java对象转成json串
			redisService.set(ITEM_CAT_KEY, jsonItemCat);
			return itemCatList;
		}
	}

	//为前台获取商品分类集合，要按json格式来构建
	public ItemCatResult getItemCatList(){
		ItemCatResult result = new ItemCatResult();
		List<ItemCat> cats = itemCatMapper.select(null);	//获取所有的数据
		
		//获取当前的节点的所有的子节点的集合（巧妙）
		Map<Long,List<ItemCat>> map = new HashMap<Long,List<ItemCat>>();
		for(ItemCat itemCat : cats){
			if(!map.containsKey(itemCat.getParentId())){	//当前的id不存在
				map.put(itemCat.getParentId(), new ArrayList<ItemCat>());
			}
			//如果存在key
			map.get(itemCat.getParentId()).add(itemCat);
		}
		
		//构建3级菜单（缺点：读程差，3层循环效率低，看不懂，拷贝）
		List<ItemCat> itemCatList1 = map.get(0L);	//利用map获取所有是第一目录
		List<ItemCatData> item1 = new ArrayList<ItemCatData>();
		for(ItemCat itemCat : itemCatList1){
			//构建一级菜单
			ItemCatData cat1 = new ItemCatData();
			String url = "/products/" + itemCat.getId() + ".html";
			cat1.setUrl(url);
			cat1.setName("<a href='"+url+"'>"+itemCat.getName()+"</a>");
			
			//构建二级菜单
			List<ItemCat> itemCatList2 = map.get(itemCat.getId());
			List<ItemCatData> item2 = new ArrayList<ItemCatData>();
			for(ItemCat itemCat2 : itemCatList2){
				ItemCatData cat2 = new ItemCatData();
				url = "/products/" + itemCat2.getId() + ".html";
				cat2.setUrl(url);
				cat2.setName(itemCat2.getName());
				
				//构建三级菜单
				List<ItemCat> itemCatList3 = map.get(itemCat2.getId());
				List<String> item3 = new ArrayList<String>();
				for(ItemCat itemCat3 : itemCatList3){
					url = "/products/" + itemCat3.getId() + ".html";
					item3.add(url+"|"+itemCat3.getName());	//第三层菜单集合
				}
				cat2.setItems(item3);
				item2.add(cat2);
			}
			cat1.setItems(item2);
			
			//主菜单最多14个
			if(item1.size()>14){
				break;
			}

			item1.add(cat1);
		}
		result.setItemCats(item1);
		return result;
	}
}
