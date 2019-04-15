package com.jt.manage.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;

@Controller	//完成图片上传
public class PicUploadController {
	//	pic/upload?dir=image
	@RequestMapping("pic/upload")
	@ResponseBody
	public PicUploadResult upload(MultipartFile uploadFile) throws IllegalStateException, IOException{
		//包装一个对象，error/height/width/url属性，返回json串，KindEditor回显
		PicUploadResult result = new PicUploadResult();
		
		//1.校验图片文件后缀
		String oldFileName = uploadFile.getOriginalFilename();	//原始文件名
		String extFileName = oldFileName.substring(oldFileName.lastIndexOf("."));
		
		if(!oldFileName.matches("^.*(jpg|png|gif)$")){	//是jpg或者png；abc.jpg，正则表达式
			result.setError(1);		//0正常，1代表出错
			return result;
		}
		
		//2. 检查是否是木马，springmvc直接可以获取输入流，而不用先保存到本地，在读取
		BufferedImage buf = ImageIO.read(uploadFile.getInputStream());
		try{
			result.setHeight(buf.getHeight()+"");
			result.setWidth(buf.getWidth()+"");
		}catch(Exception e){
			//todo 写日志，方便观察是否有人做恶意破坏，如果有，把它的IP地址加入到路由器黑名单
			result.setError(1);		//0正常，1代表出错
			return result;
		}

		//3. 生成图片存放路径，绝对路径和相对路径
		String dir = "c:/jt-upload/";
		String path = "images/" +new SimpleDateFormat("yyyy/MM/dd/").format(new Date());	//C:\jt-upload\images\2016\12\06
		String newFileName = System.currentTimeMillis() +""+ RandomUtils.nextInt(100, 999) + extFileName;
		
		result.setUrl("http://image.jt.com/" + path + newFileName);
		File _dir = new File(dir+path);
		if(!_dir.exists()){
			_dir.mkdirs();	//创建多级
		}
		uploadFile.transferTo(new File(dir + path + newFileName));	//保存文件
		
		return result;
	}
}
