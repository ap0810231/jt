package jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsoup {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Test	//抓取整个网站
	public void siteall() throws IOException{
		String url = "http://news.sina.com.cn/china/xlxw/2017-02-23/doc-ifyavwcv8642796.shtml";
		Document doc = Jsoup.connect(url).get();
		String context = doc.html(); 	//获取整个页面
		System.out.println(context);
	}
	
	@Test	//抓取商品标题
	public void getTitle() throws IOException{
		String url = "http://item.jd.com/3133847.html?jd_pop=94cabd25-6e9e-42cd-bc4d-dd4582090db8&abt=3";
		Document doc = Jsoup.connect(url).get();
		Elements eles = doc.select(".sku-name");	//通过jsoup选择器，选择css class属性，找到元素
		for(Element ele : eles){
			String title = ele.text();
			System.out.println(title);
		}
	}
	
	@Test	//抓取商品价格，价格是直接获取不到，ajax提交
	public void getPrice() throws IOException{
		//ajxa请求链接
		String url = "http://p.3.cn/prices/mgets?skuIds=J_3133847";
		//抓取json格式，设置忽略ContentType
		String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
		JsonNode jsonNode = MAPPER.readTree(json);
		Double price = jsonNode.get(0).get("p").asDouble();	//获取json中的属性
		System.out.println(price);
	}
	
	@Test	//抓取商品详情
	public void getDesc() throws IOException{
		String url = "http://d.3.cn/desc/2967927";
		String jsonp = Jsoup.connect(url).ignoreContentType(true).execute().body();
		String json = jsonp.substring(9, jsonp.length()-1);
		JsonNode jsonNode = MAPPER.readTree(json);
		String desc = jsonNode.get("content").asText();
		System.out.println(desc);
	}
}
