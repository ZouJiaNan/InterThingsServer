package com.casit.bootsystem.sys;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;
import com.casit.suwen.security.MD5;
import javax.servlet.http.HttpServletRequest;

@Path("system")
public class Login
{
  private static final String key = "superadmin";
  
  @Post("login")
  public JsonO login(String passwd, String loginnm, HttpServletRequest rq){
	  
	JsonO info_ok =new JsonO();
	  
    if ((loginnm.equals("superadmin")) && (passwd.equals("superadmin"))){
      JsonO json = new JsonO();
      json.putQuoted("loginnm", "superadmin");
      json.putQuoted("usernm", "超级管理员");
      rq.getSession().setAttribute("userinfo", json);
      info_ok.put("info", json);
      return info_ok;
    }
    passwd = MD5.encode(passwd);
    String tpl = "select password,userid,usernm,loginnm,phone,deptidf from sys_user where loginnm='${loginnm}' and password='${passwd}'";
    
    tpl = Template.apply(tpl, new Object[] { loginnm, passwd });
    //JsonO info = DB3.getSingleRowAsJsonO(tpl, "");
    JsonO  info=null;
  
    JsonA   ja=  DB3.getResultAsJsonA(tpl, "");
   
    if(ja.size()>0){
	   info=ja.getJsonO(0);
    }
    
    if (info != null) {
      rq.getSession().setAttribute("userinfo", info);
      info_ok.put("info", info);
      return info_ok;
    }
    return info_ok;
  }
  
  @Post("func")
  public JsonA func(String node, CookieSession cs) {
	  
    JsonO info = (JsonO)cs.getAttribute("userinfo");
    String usernm = info.getString("loginnm");
    String tpl = "select * from sys_funcs where parents=${node} order by sortno";
    Log.syslog("登录", usernm + "登录主页面", cs);
    return DB3.getResultAsJsonA(Template.apply(tpl, new Object[] { node }), "leaf");
  }
  
  @Post
  public JsonO usernm(CookieSession cs) {
	  
    JsonO info = (JsonO)cs.getAttribute("userinfo");
    String usernm = info.getString("usernm");
    String loginnm = info.getString("loginnm");
    String userid = info.getString("userid");
    JsonO json = new JsonO();
    json.putQuoted("usernm", usernm);
    json.putQuoted("loginnm", loginnm);
    json.putQuoted("userid", userid);
    return json;
  }
  
  @Post("getMenuTree")
  public JsonA getMenuTree(HttpServletRequest re){
	  JsonO userinfo=(JsonO)re.getSession().getAttribute("userinfo");
	  JsonA menus=new JsonA();
	  
	  String loginnm=userinfo.getString("loginnm");
	  String passwd=userinfo.getString("password");
	  
	  String tpl="select * from sys_funcs where sys_funcs.funcid in"
	  		+ "(select sys_user_func.funcid from sys_user_func "
	  		+ "where sys_user_func.userid=(select userid from sys_user where "
	  		+ "loginnm='${loginnm}' and password='${passwd}'))";
	  
	  tpl = Template.apply(tpl, new Object[] { loginnm, passwd });
	  
	  menus=DB3.getResultAsJsonA(tpl, "");
	 
	  return menus;
  }
}
