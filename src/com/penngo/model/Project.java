package com.penngo.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * 
 * 
 * 
 * 
CREATE TABLE [project] (
[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,
[name] VARCHAR(50)  NOT NULL
)
 * @author pe
 *
 */
@SuppressWarnings("serial")
public class Project extends Model<Project> {
	public static final Project model = new Project();
	public final static String TYPE = "project";
	public List<Project> findAll(){
		List<Project> list = this.find("select id, name from project");
		return list;
	}
	
	
}
