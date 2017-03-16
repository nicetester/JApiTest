package com.penngo.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jfinal.core.Controller;
import com.penngo.model.User;

public class UserController extends Controller{
	/**
	 * 登录功能，访问地址http://localhost:8000/user/login
	 */
	public void login() {
		Map map = new HashMap();
		String name = this.getPara("name");// 用户名
		String password = this.getPara("password");//密码
		String code = this.getPara("code");  // 验证码
		if(this.getSessionAttr("code").equals(code)){
			if(User.login(name, password) == true){
				map.put("msg", "登录成功");
			}
			else{
				map.put("msg", "用户名或密码错误");
			}
		}
		else{
			map.put("msg", "验证码输入错误");
		}
		this.renderJson(JSONValue.toJSONString(map));
	}
	
	/**
	 * 压测方法一：直接把参数写在服务器端，
	 * 使用jmeter压测链接http://localhost:8000/user/testLogin1
	 */
	public void testLogin1(){
		Random rand = new Random();
		String[][] user = {{"user1", "psw1"}, {"user2", "psw2"}};
		String[] u = user[rand.nextInt(10) / user.length];
		
		Map map = new HashMap();
		String name = u[0];// 用户名
		String password = u[1];//密码
		String code = this.getSessionAttr("code");  // 验证码
		if(this.getSessionAttr("code").equals(code)){
			if(User.login(name, password) == true){
				map.put("msg", "登录成功");
			}
			else{
				map.put("msg", "用户名或密码错误");
			}
		}
		else{
			map.put("msg", "验证码输入错误");
		}
		this.renderJson(JSONValue.toJSONString(map));
	}
	/**
	 * 压测方法二：通过代码直接在服务器端模拟HTTP请求
	 * 使用jmeter压测链接http://localhost:8000/user/testLogin2
	 */
	public void testLogin2(){
		Random rand = new Random();
		String[][] user = {{"user1", "psw1"}, {"user2", "psw2"}};
		String[] u = user[rand.nextInt(10) / user.length];
		String code = "test";
		String body = "";
		try{
			Document doc = Jsoup.connect("http://localhost:8000/user/login")
			.data("name", u[0]).data("password", u[1]).data("code", code)
			.ignoreContentType(true).post();
			body = doc.body().text();
			System.out.println(body);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		this.renderText(body);
	}
}
