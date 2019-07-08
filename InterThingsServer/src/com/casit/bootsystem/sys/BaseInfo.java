package com.casit.bootsystem.sys;

import java.util.HashMap;
import java.util.Map;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;
import com.casit.suwen.security.MD5;
@Path
public class BaseInfo {  
	
	/**
	 * *****************************************************************
	 * 功能描述：角色&组织机构查询
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public JsonO roledeptinfo(){
		JsonO json = new JsonO();
		JsonA role = DB3.getResultAsJsonA("select * from sys_role order by roleid", "");
		JsonA dept = DB3.getResultAsJsonA("select deptid,deptnm as text,parents from sys_dept order by deptid", "");
		json.put("role", role);
		json.put("dept", dept);
		return json;
	} 
	
	/**
	 * *****************************************************************
	 * 功能描述：区域结构查询
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public JsonO regioninfo(){
		JsonO json = new JsonO();	
		JsonA base_region = DB3.getResultAsJsonA("select id,name as text,parents from base_region order by id", "");
		json.put("base_region", base_region);
		return json;
	} 	
	@Post
	public JsonO region_layerid(String id){
		JsonO json = new JsonO();	
		JsonA base_region = DB3.getResultAsJsonA("select * from base_region where id="+id+" order by id", "");
		json.put("base_region", base_region);
		return json;
	}
	/**
	 * *****************************************************************
	 * 功能描述：设备类型查询
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public JsonO equip_type_info(){
		JsonO json = new JsonO();	
		JsonA equip_type = DB3.getResultAsJsonA("select equip_type_id,equip_type_name as text,parents from base_equip_type order by equip_type_id", "");
		json.put("equip_type", equip_type);
		return json;
	} 	
}
