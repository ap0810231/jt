package com.jt.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.search.pojo.Item;
import com.jt.search.service.SearchService;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 通过关键字搜索
     * 
     * @param keyWords
     * @return
     */
    @RequestMapping(value = "search")
    @ResponseBody
    public SysResult search(@RequestParam("keyWords") String keyWords, @RequestParam("page") Integer page,
            @RequestParam("rows") Integer rows) {
        return this.searchService.search(keyWords, page, rows);
    }
    
    /**
     * 更新solr中的数据
     * @param item
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public SysResult update(@RequestBody Item item){
        return this.searchService.update(item);
    }

}
