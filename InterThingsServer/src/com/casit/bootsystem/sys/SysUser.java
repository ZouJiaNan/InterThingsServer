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
public class SysUser {  
	/**
	 * *****************************************************************
	 * 功能描述：数据查询
	 * 参数 ：  keys   查询关键字
	 * *****************************************************************
	 */
	@Post
	public JsonO info(String keys, String limit, String offset){
		JsonO json = new JsonO();
		String tpl = "";
		if(keys != "" && keys != null){
			tpl = "select r.rolename,d.deptnm,u.userid,u.loginnm,u.usernm,u.deptidf,u.roleidf,u.isvalid,"+
				  "u.sortnouser,u.memo from sys_user u left join sys_role r on u.roleidf = r.rolecode left "+
				  "join sys_dept d on u.deptidf = d.deptid where u.usernm like '%${keys}%' or u.loginnm "+
				  "like '%${keys}%' order by u.userid";
			tpl = Template.apply(tpl,keys,keys);
		}else{
			tpl = "select r.rolename,d.deptnm,u.userid,u.loginnm,u.usernm,u.deptidf,u.roleidf,u.isvalid,"+
				  "u.sortnouser,u.memo from sys_user u left join sys_role r on u.roleidf = r.rolecode left "+
				  "join sys_dept d on u.deptidf = d.deptid order by u.userid";
			tpl = Template.apply(tpl);
		}
		Map<String, String> hm=new HashMap<String, String>();
		hm.put("start", offset);
		hm.put("limit", limit);
		json = DB3.getAutoStore(tpl, "", "", hm, null);
		JsonA ja = json.getJsonA("root");
		json.remove("root");
		json.put("rows", ja);
		return json;
		
	} 
	/**
	 * *****************************************************************
	 * 功能描述：删除系统用户
	 * 参数 ：  node   userid
	 * *****************************************************************
	 */
	@Post
	public String delsysuser(String node,CookieSession cs){
		try{
			Log.syslog("删除数据","删除系统用户", cs);
			node = node.replace("insert_", "");
			DB3.update(Template.apply("delete from sys_user where userid=${node}",node));
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
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
	 * 功能描述：系统用户保存
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String save(String jstr, CookieSession cs){
		JsonO json = new JsonO(jstr);
		try{
			Log.syslog("保存数据","保存系统用户信息", cs);
			
			String newpassword=MD5.encode("123456");
			json.putQuoted("password", newpassword);			
			DB3.saveJsonOToDB(json, "sys_user", "userid", "sortnouser");
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	/**
	 * *****************************************************************
	 * 功能描述：getNewID
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String sysuserNID(){
		return DB3.getNewID("sys_user", 1);
	}
	
}
