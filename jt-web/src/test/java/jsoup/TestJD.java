package jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//抓取京东网站
public class TestJD {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	//抓取所有的三级分类，排除不规范分类
	public List getCatLevel3(String url) throws IOException{
		List<String> catLevel3s = new ArrayList<String>();
		
		Document doc = Jsoup.connect(url).get();
		Elements eles = doc.select(".items .clearfix dd a");
		for(Element ele : eles){
			String catLevel3Url = "http:" + ele.attr("href");
			
			//过滤掉，不规范分类
			if(catLevel3Url.startsWith("http://list.jd.com/list.html?cat=")){
				catLevel3s.add(catLevel3Url);
			}
			
			System.out.println(catLevel3Url);
		}
		System.out.println("抓取到商品三级分类数："+catLevel3s.size());
		return catLevel3s;
	}
	
	//获取某个分类下的总页数
	public Integer getPageNum(String catUrl) throws IOException{
		try{
			String page = Jsoup.connect(catUrl).get()
					.select("#J_topPage .fp-text i").get(0).text();
			return Integer.parseInt(page);
		}catch(Exception e){
			//todo:log，多次尝试
			return 0;
		}
	}
	
	//某个分类下的所有的分页链接
	public List<String> getCatPageUrl(String catUrl) throws IOException{
		List<String>  _list = new ArrayList<String>();
		
		Integer pageNum = this.getPageNum(catUrl);
		for(int i=1;i<=pageNum;i++){
			String url = catUrl + "&page="+i;
			_list.add(url);
		}
		System.out.println("分类："+catUrl + "--页数:"+pageNum);
		return _list;
	}
	
	//某个分类某个分页面下抓取商品的链接
	public List<String> getItemUrl(String catPageUrl) throws IOException{
		List<String> _list = new ArrayList<String>();
		
		//对于样式中有多个样式，写多个.select()
		Elements eles = Jsoup.connect(catPageUrl).get()
				.select(".gl-i-wrap").select(".j-sku-item .p-img a");
		for(Element ele : eles){
			String itemUrl = "http:" + ele.attr("href");
			_list.add(itemUrl);
			
			System.out.println(itemUrl);
		}
		return _list;
	}
	
	//抓取某个一个商品的信息
	public Item getItemInfo(String itemUrl) throws IOException{
		Item item = new Item();
		
		//http://item.jd.com/3038297.html
		String id = itemUrl.substring(itemUrl.lastIndexOf("/")+1,itemUrl.lastIndexOf("."));
		item.setId(Long.parseLong(id));
		
		Document doc = Jsoup.connect(itemUrl).get();
		
		//商品标题
		String title = doc.select(".product-intro")
			.select(".clearfix .itemInfo-wrap .sku-name").get(0).text();
		item.setTitle(title);
		
		//商品卖点 ajax
		String sellPointUrl = "http://ad.3.cn/ads/mgets?skuids=AD_"+id;
		String sellPointJsonData = Jsoup.connect(sellPointUrl).ignoreContentType(true).execute().body();
		JsonNode sellPointJsonNode = MAPPER.readTree(sellPointJsonData);
		String sellPoint = sellPointJsonNode.get(0).get("ad").asText();
		item.setSellPoint(sellPoint);
		
		//价格 ajax
		String priceUrl = "http://p.3.cn/prices/mgets?skuIds=J_"+id;
		String priceJsonData = Jsoup.connect(priceUrl).ignoreContentType(true).execute().body();
		JsonNode priceJsonNode = MAPPER.readTree(priceJsonData);
		Double price = priceJsonNode.get(0).get("p").asDouble();
		item.setPrice(price);
		
		//商品图片
		Elements elesImg = doc.select(".lh li img");
		StringBuilder sb = new StringBuilder();
		for(Element eleImg : elesImg){
			String imageUrl = "http:"+eleImg.attr("src");
			sb.append(imageUrl).append(",");
		}
		if(sb.length()>0){
			sb.delete(sb.length()-1, sb.length());	//删除最后逗号
		}
		item.setImage(sb.toString());
		
		//商品详情 jsonp
		String descUrl = "http://d.3.cn/desc/"+id;
		String descJsonp = Jsoup.connect(descUrl).ignoreContentType(true).execute().body();
		String descJson = descJsonp.substring(9, descJsonp.length()-1);
		JsonNode descJsonNode = MAPPER.readTree(descJson);
		String desc = descJsonNode.get("content").asText();
		item.setDesc(desc);
		
		System.out.println(item);
		return item;
	}
	
	@Test
	public void run() throws IOException{
		String url = "https://www.jd.com/allSort.aspx";
		//this.getCatLevel3(url);
		
		//Integer pageNum = getPageNum("https://list.jd.com/list.html?cat=9987,6880,6881");
		//System.out.println(pageNum);
		
		//url = "https://list.jd.com/list.html?cat=9987,6880,6881";
		
//		List<String> catUrlList = this.getCatLevel3(url);
//		for(String xurl : catUrlList){
//			getCatPageUrl(xurl);
//		}
		
		//url = "https://list.jd.com/list.html?cat=9987,6880,6881&page=1";
		//getItemUrl(url);
		
		url = "http://item.jd.com/3657077.html";
		
		this.getItemInfo(url);
	}
}
