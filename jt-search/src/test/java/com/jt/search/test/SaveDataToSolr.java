package com.jt.search.test;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jt.common.service.HttpClientService;
import com.jt.common.vo.EasyUIResult;
import com.jt.search.pojo.Item;

public class SaveDataToSolr {

    private HttpSolrServer httpSolrServer;
    private HttpClientService httpClientService;

    @Before
    public void before() {
        // 创建solrServer对象
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "spring/applicationContext*.xml");
        this.httpSolrServer = applicationContext.getBean(HttpSolrServer.class);
        
        this.httpClientService = applicationContext.getBean(HttpClientService.class);
	}
	
	@Test
	public void testData() throws Exception {
	    // 查询商品数据
	    String url = "http://manage.jt.com/item/query?page={page}&rows=100";
	    int page = 1;
	    do {
	        String jsonData = this.httpClientService.doGet(StringUtils.replace(url, "{page}", page + ""));
	        EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData, Item.class);
	        List<Item> items = (List<Item>) easyUIResult.getRows();
	        if (items == null || items.isEmpty()) {
	            break;
	        }
	
	        // 写入数据
	        this.httpSolrServer.addBeans(items);
	        page++;
	    } while (true);
	
	    this.httpSolrServer.commit();
	}

}
