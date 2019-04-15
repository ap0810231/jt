package com.jt.web.threadlocal;

import com.jt.web.pojo.User;

//线程安全，共享USER变量
public class UserThreadLocal {
	private static final ThreadLocal<User> USER = new ThreadLocal<User>();
	public static User get(){
		return USER.get();
	}
	public static void set(User user){
		USER.set(user);
	}
	
	//为了方便获取userId
	public static Long getUserId(){
		if(null==USER){
			return null;
		}else{
			if(null == USER.get()){
				return null;
			}else{
				return USER.get().getId();
			}
		}
	}
}
