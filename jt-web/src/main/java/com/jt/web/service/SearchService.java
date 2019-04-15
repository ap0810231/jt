package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.common.service.HttpClientService;
import com.jt.common.spring.exetend.PropertyConfig;
import com.jt.common.vo.SysResult;
import com.jt.web.pojo.Item;

@Service
public class SearchService {

    @Autowired
    private HttpClientService httpClientService;

    @PropertyConfig
    private String SEARCH_URL;

    @PropertyConfig
    public Integer SEARCH_COUNT;

    /**
     * 搜索
     * 
     * @param keyWords
     * @param page
     * @return
     */
    public SysResult search(String keyWords, Integer page) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("keyWords", keyWords);
        params.put("page", String.valueOf(page));
        params.put("rows", String.valueOf(SEARCH_COUNT));
        try {
            return SysResult.formatToList(this.httpClientService.doPost(SEARCH_URL, params), Item.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
