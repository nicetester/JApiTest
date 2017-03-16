package com.penngo.common;

import java.io.File;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.penngo.api.ApiController;
import com.penngo.index.IndexController;
import com.penngo.model.Project;
import com.penngo.model.UseCase;

/**
 * API引导式配置
 */
public class ApiConfig extends JFinalConfig {
	//public final static String dataPath = "/data/"; 
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
	
		PropKit.use("config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class, "/index");	// 第三个参数为该Controller的视图存放路径
		me.add("/api", ApiController.class);	
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
		c3p0Plugin.setDriverClass("org.sqlite.JDBC");
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		arp.setDialect(new Sqlite3Dialect());
		arp.addMapping("project", Project.class);
		arp.addMapping("useCase", UseCase.class);
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		
	}
	

	public static void main(String[] args) {
		//System.out.println("args=====" + args.length);
		int port = 8000; // 默认端口
		if(args.length == 1){
			port = Integer.valueOf(args[0]);
		}
		String root = new File("").getAbsolutePath();
		PathKit pathKit = new PathKit();
		pathKit.setWebRootPath(root + "/src/main/webapp");
//		pathKit.setRootClassPath(root + "/src/main/webapp/WEB-INF/classes");

		//System.out.println("PathKit.getWebRootPath()=====" + PathKit.getWebRootPath());

		JFinal.start("src/main/webapp", port, "/", 5);
//		JFinal.s.start("src/main/webapp", 80, "/");
	}
}
