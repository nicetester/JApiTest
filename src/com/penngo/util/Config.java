package com.penngo.util;

import java.util.HashMap;
import java.util.Map;

public class Config {
	public static Map<String, String> HTTP_CONTENT_TYPE;
	static{
		HTTP_CONTENT_TYPE = new HashMap<String, String>();
		HTTP_CONTENT_TYPE.put("json", "application/json");//
	}

}
