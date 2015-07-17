package com.penngo.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;


/**
 * 
CREATE TABLE [useCase] (
[id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,
[pid] INTEGER  NOT NULL,
[name] VARCHAR(50)  NULL,
[url] VARCHAR(200)  NULL,
[method] VARCHAR(10)  NULL,
[type] VARCHAR(50)  NULL,
[code] vaRCHAR(20)  NULL,
[request] TEXT  NULL,
[response] TEXT  NULL,
[createTime] VARCHAR(20)  NULL,
[dataSort] INTEGER DEFAULT '0'' NULL,
[caseTime] INTEGER DEFAULT '''0''' NULL,
[assertValue] TEXT  NULL
)
 *
 */
public class UseCase  extends Model<UseCase> {
	public final static String TYPE = "useCase";
	public final static UseCase model = new UseCase();
//	private String id;
//	private String pid;
//	private String name;
//	String id = this.getPara("id");
//	String url = this.getPara("url");
//	String method = this.getPara("method");
//	String type = this.getPara("type");
//	String code = this.getPara("code");
//	String request = this.getPara("request");
//	private String url;
//	private String method;
//	private String type;
//	private String code;
//	private String request;
	
	
	//	public UseCase(){
	//		this.table = "useCase";
	//	}
	//
	//	public String getId() {
	//		return id;
	//	}
	//
	//	public void setId(String id) {
	//		this.id = id;
	//	}
	//
	//	public String getPid() {
	//		return pid;
	//	}
	//
	//	public void setPid(String pid) {
	//		this.pid = pid;
	//	}
	//
	//	public String getName() {
	//		return name;
	//	}
	//
	//	public void setName(String name) {
	//		this.name = name;
	//	}
	//	
	//	public String getUrl() {
	//		return url;
	//	}
	//
	//	public void setUrl(String url) {
	//		this.url = url;
	//	}
	//
	//	public String getMethod() {
	//		return method;
	//	}
	//
	//	public void setMethod(String method) {
	//		this.method = method;
	//	}
	//
	//	public String getType() {
	//		return type;
	//	}
	//
	//	public void setType(String type) {
	//		this.type = type;
	//	}
	//
	//	public String getCode() {
	//		return code;
	//	}
	//
	//	public void setCode(String code) {
	//		this.code = code;
	//	}
	//
	//	public String getRequest() {
	//		return request;
	//	}
	//
	//	public void setRequest(String request) {
	//		this.request = request;
	//	}
	
	
	
	public List<UseCase> findAllByPid(String pid){
		List<UseCase> list = UseCase.model.find(
				"select * from useCase where pid = ? ", pid);
		return list;
	}
	public static void main(String[] args){
		String table = "sss_aa";
		System.out.println(table.substring(0, table.indexOf("_")));
	}
}
