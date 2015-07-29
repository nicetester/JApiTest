package com.penngo.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.penngo.model.Project;
import com.penngo.model.UseCase;
import com.penngo.util.CaseHttpRun;
import com.penngo.util.DataConfig;


public class ApiController extends Controller {
	public void index() {
		render("index.html");
	}


	public void api() {
		String id = this.getPara("id");
		UseCase useCase = UseCase.model.findById(id);
		this.setAttr("id", id);
		String assertValue = useCase.get("assertValue") != null
				&& !useCase.get("assertValue").toString().equals("") ? useCase
				.getStr("assertValue") : "[]";
		useCase.set("assertValue", assertValue);
		this.setAttr("useCase", useCase);

		render("api.html");
	}

	public void all() {
		String id = this.getPara("id");
		Project project = Project.model.findById(id);
		this.setAttr("project", project);

		List<UseCase> list = UseCase.model.findAllByPid(id);
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		for (UseCase uc : list) {
			JSONObject json = new JSONObject();
			json.put("case_id", uc.get("id"));
			json.put("case_name", uc.get("name"));
			json.put("case_state", "未执行");
			String type = uc.get("type") != null ? uc.get("type").toString()
					: "";
			type = DataConfig.HTTP_CONTENT_TYPE.get(type);

			json.put("case_type", type);
			json.put("case_process", "0%");
			System.out.println("caseTime======" + uc.get("caseTime"));
			int case_time = uc.get("caseTime") != null ? uc
					.getInt("caseTime") + 1 : 0;
			json.put("case_time", case_time);
			resultList.add(json);
		}
		this.setAttr("caseList", JSONValue.toJSONString(resultList));
		this.setAttr("caseSize", resultList.size());
		render("all.html");
	}

	public void save() {
		UseCase useCase = this.saveUseCase();
		String resultData = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", "success");

		this.renderJson(JSONValue.toJSONString(map));
	}

	private UseCase saveUseCase() {
		String id = this.getPara("id");
		String url = this.getPara("url");
		String method = this.getPara("method");
		String type = this.getPara("type");
		String code = this.getPara("code");
		String request = this.getPara("request");
		String assertValue = this.getPara("assertValue");
		int timeOut = this.getParaToInt("timeOut");
		UseCase useCase = UseCase.model.findById(id);
		useCase.set("url", url);
		useCase.set("method", method);
		useCase.set("type", type);
		useCase.set("code", code);
		useCase.set("request", request);
		useCase.set("request", request);
		useCase.set("assertValue", assertValue);
		useCase.set("timeOut", timeOut);

		useCase.update();
		return useCase;
	}

	public void saveAssert() {
		String id = this.getPara("id");
		String assertValue = this.getPara("assert_value");
		UseCase useCase = UseCase.model.findById(id);

		Map<String, Object> map = new HashMap<String, Object>();

		this.renderJson(JSONValue.toJSONString(map));
		map.put("state", "error");
		if (useCase != null) {
			useCase.set("assertValue", assertValue);
			boolean b = useCase.update();
			if (b == true) {
				map.put("state", "success");
			}
		}
		this.renderJson(JSONValue.toJSONString(map));
	}

	public void execute() {
	
		UseCase useCase = this.saveUseCase();
		String url = useCase.getStr("url");
		String request = useCase.getStr("request");
		String method = useCase.getStr("method");
		String type = useCase.getStr("type");
		Map<String, Object> resultData = null;
		Map<String, Object> map = new HashMap<String, Object>();
		resultData = CaseHttpRun.httpRunJson(url, request, method, 30);
		map.put("time", resultData.get("time"));
		if (resultData.get("state").equals("success")) {
			JSONObject countMap;
			if(type.equals("json")){
				countMap = checkCase(useCase, (JSONObject)resultData.get("data"));
				map.put("passCount", countMap.get("passCount"));
				map.put("failCount", countMap.get("failCount"));
			}
			else{
				map.put("passCount", "0");
				map.put("failCount", "0");
			}
			
			map.put("state", "success");
			map.put("data", resultData.get("data"));
			
			
		} else {
			map.put("state", "error");
			map.put("msg", resultData.get("msg"));
		}
		this.renderJson(JSONValue.toJSONString(map));
	}

	private JSONObject checkCase(UseCase useCase, Object data) {
		int passCount = 0;
		int failCount = 0;
		//int exceCount = 0;
		int totalCount = 0;
		if (useCase != null && data!= null) {
			JSONObject resultData = (JSONObject)data;
			String assertValue = useCase.getStr("assertValue");
			Object jsonObj = JSONValue.parse(assertValue);
			if (jsonObj != null && jsonObj instanceof JSONArray) {
				JSONArray array = (JSONArray) jsonObj;
				totalCount = array.size();
				if (totalCount > 0) {
					JSONObject caseCondition = new JSONObject();
					for (int i = 0; i < array.size(); i++) {
						JSONObject check = (JSONObject) array.get(i);
						caseCondition.put(check.get("en_name"), check);
					}
					Iterator it = resultData.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry e = (Map.Entry) it.next();
						String key = e.getKey().toString();
						String value = String.valueOf(e.getValue());
						
						if (caseCondition.get(key) != null) {
							JSONObject condition = (JSONObject) caseCondition
									.get(key);
							String type = condition.get("type").toString();
							String default_value = condition.get(
									"default_value").toString();
							if (default_value.equals(value)) {
								passCount = passCount + 1;
							} else {
								failCount = failCount + 1;
							}
						}
					}
				}

			}
		}
		JSONObject result = new JSONObject();
		result.put("passCount", passCount);
		result.put("failCount", failCount);
		return result;
	}

	public void executeById() {
		String id = this.getPara("id");
		System.out.println("executeById=" + id);
		UseCase useCase = UseCase.model.findById(id);
		String url = useCase.getStr("url");
		String request = useCase.getStr("request");
		String method = useCase.getStr("method");
		String type = useCase.getStr("type");
		int timeOut = useCase.getInt("timeOut") != null ? useCase.getInt("timeOut") : 30;
		Map<String, Object> resultData = CaseHttpRun.httpRunJson(
				useCase.getStr("url"), request, method, timeOut);
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("time", resultData.get("time"));
		if (resultData.get("state").equals("success")) {
			JSONObject countMap;
			if(type.equals("json")){
				countMap = checkCase(useCase, (JSONObject)resultData.get("data"));
				map.put("passCount", countMap.get("passCount"));
				map.put("failCount", countMap.get("failCount"));
			}
			else{
				map.put("passCount", "0");
				map.put("failCount", "0");
			}
			map.put("state", "success");
			map.put("data", resultData.get("data"));
		} else {
			map.put("state", "error");
			map.put("msg", resultData.get("msg"));
		}
		int case_time = useCase.get("caseTime") != null ? useCase
				.getInt("caseTime") + 1 : 0;
		useCase.set("caseTime", case_time);
		useCase.update();
		map.put("data", resultData);
		map.put("case_time", case_time);
		this.renderJson(JSONValue.toJSONString(map));
	}

	public void addProject() {
		String id = this.getPara("id");
		String directory_name = this.getPara("directory_name");
		Project project;
		if(!id.equals("")){
			project = Project.model.findById(id);
		}
		else{
			project = new Project();
		}
		project.set("name", directory_name);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(!id.equals("")){
				project.update();
			}
			else{
				project.save();
			}
			result.put("state", "success");
			JSONObject json = new JSONObject();
			json.put("isexpand", "false");
			json.put("slide", false);
			json.put("data_id", project.getInt("id"));
			json.put("text", project.getStr("name"));
			json.put("node_type", project.TYPE);
			json.put("url", "");
			json.put("children", new ArrayList());
//			JSONArray array = new JSONArray();
//			array.add(json);
//			result.put("data", array);
			
			if(!id.equals("")){
				result.put("data", json);
			}
			else{
				JSONArray array = new JSONArray();
				array.add(json);
				result.put("data", array);
			}
			
		} catch (Exception e) {
			result.put("state", "error");
			e.printStackTrace();
		}
		this.renderJson(JSONValue.toJSONString(result));
	}

	public void addCase() {
		String pid = this.getPara("pid");
		int id = this.getParaToInt("id");
		String name = this.getPara("name");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			UseCase useCase = null;
			if(id > 0){
				useCase = UseCase.model.findById(id);
			}
			else{
				useCase = new UseCase();
			}
			useCase.set("pid", Integer.valueOf(pid));
			useCase.set("name", name);
			useCase.set("caseTime", 0);
			useCase.set("dataSort", 0);
			useCase.set("timeOut", 30);
			if(id > 0 ){
				useCase.update();
			}
			else{
				useCase.save();
			}
			JSONObject json = new JSONObject();
			json.put("isexpand", "false");
			json.put("slide", false);
			json.put("data_id", useCase.getInt("id"));
			json.put("pid", useCase.getInt("pid"));
			json.put("text", useCase.getStr("name"));
			json.put("node_type", useCase.TYPE);
			json.put("url", "/api/api?id=" + useCase.getInt("id"));
			if(id > 0){
				result.put("data", json);
			}
			else{
				JSONArray array = new JSONArray();
				array.add(json);
				result.put("data", array);
			}
			result.put("state", "success");
		} catch (Exception e) {
			result.put("state", "error");
			e.printStackTrace();
		}
		this.renderJson(JSONValue.toJSONString(result));
	}
	public void copyCase() {
		String pid = this.getPara("pid");
		int id = this.getParaToInt("id");
		String name = this.getPara("name");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			UseCase copyCase = UseCase.model.findById(id);
			UseCase useCase = new UseCase();
			
			useCase.set("pid", Integer.valueOf(pid));
			useCase.set("name", name);
			
			useCase.set("url", copyCase.get("url"));
			useCase.set("method", copyCase.get("method"));
			useCase.set("type", copyCase.get("type"));
			useCase.set("code", copyCase.get("code"));
			useCase.set("request", copyCase.get("request"));
			useCase.set("assertValue", copyCase.get("request"));
			useCase.set("dataSort", 0);
			useCase.set("timeOut", copyCase.get("timeOut"));
			useCase.set("caseTime", 0);
		
			useCase.save();
			JSONObject json = new JSONObject();
			json.put("isexpand", "false");
			json.put("slide", false);
			json.put("data_id", useCase.getInt("id"));
			json.put("pid", useCase.getInt("pid"));
			json.put("text", useCase.getStr("name"));
			json.put("node_type", useCase.TYPE);
			json.put("url", "/api/api?id=" + useCase.getInt("id"));

			JSONArray array = new JSONArray();
			array.add(json);
			result.put("data", array);

			result.put("state", "success");
		} catch (Exception e) {
			result.put("state", "error");
			e.printStackTrace();
		}
		this.renderJson(JSONValue.toJSONString(result));
	}
	public void deleteNode() {
		String id = this.getPara("id");
		String type = this.getPara("type");
		boolean d = false;
		JSONObject result = new JSONObject();
		result.put("state", "error");
		if (type.equals(UseCase.TYPE)) {
			d = UseCase.model.deleteById(id);
			if (d == true) {
				result.put("state", "success");
			}
		} else if (type.equals(Project.TYPE)) {
			d = Project.model.deleteById(id);
			if (d == true) {
				result.put("state", "success");
			}
			Db.update("delete from useCase where pid = ? ", id);
		}
		this.renderJson(JSONValue.toJSONString(result));
	}

	public void testLogin(){
		String name = this.getPara("name");
		String pws = this.getPara("psw");
		JSONObject result = new JSONObject();
		result.put("state", "error");
		if(name.equals("penngo") && pws.equals("test123456")){
			result.put("state", "success");
			JSONObject data = new JSONObject();
			data.put("nickname", "penngo");
			data.put("pic", "default.png");
			result.put("data", data);
		}
		this.renderJson(JSONValue.toJSONString(result));
	}
	public void getChildNode() {
		String node_type = this.getPara("node_type");
		String pid = this.getPara("pid") != null ? this.getPara("pid") : this
				.getPara("pid");
		List<JSONObject> data = null;
		if (node_type.equals("projects")) {
			data = this.getAllProjects();
		} else if (node_type.equals(Project.TYPE)) {
			data = this.getAllUseCase(pid);
		}
		JSONObject result = new JSONObject();
		result.put("state", "success");
		result.put("data", data);
		this.renderJson(JSONValue.toJSONString(result));
	}

	public List<JSONObject> getAllProjects() {
		Project model = new Project();
		List<Project> list = model.findAll();
		List<JSONObject> data = new ArrayList<JSONObject>();
		for (int i = 0; i < list.size(); i++) {
			Project project = list.get(i);
			JSONObject json = new JSONObject();
			json.put("isexpand", "false");
			json.put("slide", false);
			json.put("data_id", project.getInt("id"));
			json.put("text", project.getStr("name"));
			json.put("node_type", project.TYPE);
			// json.put("url", "");
			json.put("url", "/api/all?id=" + project.getInt("id"));
			json.put("children", new ArrayList());
			data.add(json);
		}
		return data;
		// return null;
	}

	public List<JSONObject> getAllUseCase(String pid) {
		List<UseCase> list = UseCase.model.findAllByPid(pid);
		List<JSONObject> data = new ArrayList<JSONObject>();
		for (int i = 0; i < list.size(); i++) {
			UseCase uc = list.get(i);
			JSONObject json = new JSONObject();
			json.put("isexpand", "false");
			json.put("slide", false);
			json.put("data_id", uc.getInt("id"));
			json.put("pid", uc.getInt("pid"));
			json.put("text", uc.getStr("name"));
			json.put("node_type", uc.TYPE);
			json.put("url", "/api/api?id=" + uc.getInt("id"));
			data.add(json);
		}
		return data;
	}

	
}
