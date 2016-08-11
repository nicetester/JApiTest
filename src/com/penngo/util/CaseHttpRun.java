package com.penngo.util;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

public class CaseHttpRun {
	public static String HTTP_GET = "get";
	public static String HTTP_POST = "post";
	
	/**
	 * @param url
	 * @param parame
	 * @param method
	 * @param timeout 秒
	 * @return
	 */
	public static Map<String, Object> httpRunJson(String url, String parame, String method,  Integer timeout){
		if(timeout == null){
			timeout = 30000;
		}
		else{
			timeout = timeout * 1000;
		}
		Object resultData = "";
		Map<String, Object> map = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			Connection conn = Jsoup
					.connect(url)
					.timeout(timeout)
					.userAgent(
							"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
			if (parame != null && !parame.trim().equals("")) {
				Object jsonObject = JSONValue.parse(parame);
//				System.out.println("httpRunJson========parame:" + jsonObject);
				if (jsonObject != null && jsonObject instanceof JSONObject) {
					JSONObject data = (JSONObject) jsonObject;
					conn.data(data);
				}
			}
			conn.ignoreContentType(true);
			if (method.equals(HTTP_POST)) {
				conn.method(Method.POST);
			} else {
				conn.method(Method.GET);
			}
			Response res = conn.execute();
			String data = res.body();
//			System.out.println("httpRunJson========" + data);
			if (data != null && !data.equals("")) {
				resultData = JSONValue.parse(data);
				if(resultData != null){
					map.put("data", resultData);
				}
				else{
					map.put("data", data);
				}
				map.put("state", "success");
				
			}
			else{
				resultData = data;
				map.put("state", "error");
				map.put("msg", data);
			}
			
		} catch (Exception e) {
			map.put("state", "exception");
			map.put("msg", e.getMessage());
//			e.printStackTrace();
			System.out.println("url error==========" + url + ", error info:" + e.getMessage());
		}
		long end = System.currentTimeMillis();
		map.put("time", end - start);
		return map;
	}
	
	public static void main(String[] args) {
		try {
//			String method = "get";
//			Connection conn = Jsoup
//					.connect("http://www.oschina.net/")
//					.userAgent(
//							"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
//
//			conn.ignoreContentType(true);
//			if (method.equals("post")) {
//				conn.method(Method.POST);
//			} else {
//				conn.method(Method.GET);
//			}
//			Response res = conn.execute();
			// res.body();
//			System.out.println("========" + res.body());
			Map<String, Object> body = CaseHttpRun.httpRunJson("http://www.oschina.net/", null, "", 30);
			System.out.println("========" + JSONValue.toJSONString(body));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
