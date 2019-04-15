package com.jt.web.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jt.common.vo.SysResult;
import com.jt.web.service.SearchService;


@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping("search")
    public ModelAndView search(@RequestParam("q") String keyWords,
            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        ModelAndView mv = new ModelAndView("search");
        
        SysResult taotaoResult = this.searchService.search(keyWords, page);
        mv.addObject("itemList", taotaoResult.getData());
        
        Integer total = Integer.valueOf(taotaoResult.getMsg());
        Integer SEARCH_COUNT = 20;
        Integer pages = (total - 1 + SEARCH_COUNT) / SEARCH_COUNT;
        mv.addObject("pages", pages);
        mv.addObject("page", page);
        
        try {
            keyWords = new String(keyWords.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mv.addObject("query", keyWords);
        return mv;
    }

}
