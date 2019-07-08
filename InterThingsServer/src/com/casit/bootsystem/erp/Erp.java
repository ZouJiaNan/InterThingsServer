package com.casit.bootsystem.erp;

import com.casit.bootsystem.config.SysConfig;
import com.casit.bootsystem.util.DateTools;
import com.casit.bootsystem.util.HttpRequest;
import com.casit.bootsystem.util.SysSetting;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

@Path
public class Erp{
	
	public static int shijain=SysConfig.REAL_SPACE_TIME;
	public static long shijain2=SysConfig.REAL_SPACE_MIL_TIME;
	
	
  @Post
  public JsonO info(String keys, String limit, String offset)  {
    JsonO json = new JsonO();
    String tpl = "";
    if ((keys != "") && (keys != null) && (keys != "null"))    {
    	 keys=keys.trim();
      tpl = "select u.* , r.is_setup, r.id as base_erp_region_id, br2.name as build_name  , br.name as floor_name  , br2.id as build_id  , br.id as floor_id  from base_erp u  left join base_erp_region r on u.equiNo = r.equi_no    left join base_region br on br.id = r.region_id     left join base_region br2 on br.parents = br2.id     where u.equiNo like '%${keys}%' or u.equiSpec like '%${keys}%' or u.alLocal like '%${keys}%' or br.name like '%${keys}%'  order by r.is_setup desc , u.equiNo desc";   
      tpl = Template.apply(tpl, new Object[] { keys, keys, keys, keys });
    }
    else    {
      tpl = "select u.* , r.is_setup , r.id as base_erp_region_id, br2.name as build_name  , br.name as floor_name  , br2.id as build_id  , br.id as floor_id  from base_erp u  left join base_erp_region r on u.equiNo = r.equi_no    left join base_region br on br.id = r.region_id     left join base_region br2 on br.parents = br2.id   order by r.is_setup desc , u.equiNo desc";
      tpl = Template.apply(tpl);
    }
    Map<String, String> hm = new HashMap();
    int cc = Integer.valueOf(offset).intValue();
    if (cc == 0) {
      cc = 1;
    }
    hm.put("start", String.valueOf(cc - 1));
    hm.put("limit", limit);
    json = DB3.getAutoStore(tpl, "", "", hm, null);    
    JsonA ja = json.getJsonA("root");
    for (int i = 0; i < ja.size(); i++)    {
      JsonO re_jo = ja.getJsonO(i);      
      String tp2 = "SELECT r.DEVICE_ID ,r.DEVICE_TYPE ,r.MAC  FROM real_data r  WHERE INSERT_DATE > (select SUBDATE(now(),interval "+shijain+" second))   and DEVICE_ID='" + re_jo.getString("equino").replace("ZCKP", "").trim() + "'    Group By DEVICE_ID;";
      String tp3 = Template.apply(tp2);
      JsonA jsoneee = DB3.getResultAsJsonA(tp3, "");
      if (jsoneee.size() > 0) {
        re_jo.putQuoted("run_state", "运行");
      } else {
        re_jo.putQuoted("run_state", "停止");
      }
    }
    json.remove("root");
    json.put("rows", ja);
    return json;
  }
  
  
  //从erp的视图中去取数据
  @Post
  public JsonO getErpInfoFromView(String keys, String limit, String offset)  {
    String ddd = "";
    JsonO json = new JsonO();
    if ((limit != "") && (!"".equals(limit)) && (limit != null) && (offset != "") && (!"".equals(offset)) && (offset != null))    {
      if ((keys == "") || ("".equals(keys)) || (keys == null)) {
         keys = "";
      }
      keys=keys.trim();
      try {
		ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/info?keys=" + URLEncoder.encode(keys, "UTF-8")  + "&limit=" + limit + "&offset=" + offset, "");
	  } 
        catch (UnsupportedEncodingException e) {
		ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/info?keys=" +keys + "&limit=" + limit + "&offset=" + offset, "");
	}
        json = new JsonO(ddd);
    }
    else    {
      try {
		ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/getInfoAllByNums?equi_arch_nos=" + URLEncoder.encode(keys, "UTF-8"), "");
	} catch (UnsupportedEncodingException e) {
		ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/getInfoAllByNums?equi_arch_nos=" +keys, "");;
	}      
       JSONArray joo = new JSONArray(ddd);
       JsonA ja = new JsonA(ddd.toString());
       json.put("result", ja);
    }
      return json;
  }
  
  
  
  
  
  //从本地数据库中去取数据
  @Post
  public JsonO getErpInfo(String keys, String limit, String offset)  {
	  JsonO json = new JsonO();
		String tpl = "";
		if(keys != "" && keys != null){
			 keys=keys.trim();
			keys=keys.replace("ZCKP", "");
			if("null".equals(limit)||"null".equals(offset)||"".equals(offset)||"".equals(limit)||limit==null||offset==null){
				limit="10";
				offset="0";
			}
			tpl = "select u.* from other_erp u  where u.equi_arch_no like '%${keys}%' or u.equi_name "+
				  "like '%${keys}%' or u.dept_name like '%${keys}%'  or u.gb_name like '%${keys}%'  order by u.equi_arch_no";
			tpl = Template.apply(tpl,keys,keys,keys,keys);
		}else{
			tpl = "select u.* from other_erp u    order by u.equi_arch_no";
			tpl = Template.apply(tpl);
		}
		Map<String, String> hm=new HashMap<String, String>();
		//hm.put("start", String.valueOf(Integer.valueOf(offset).intValue() - 1));
		hm.put("start", offset);
		hm.put("limit", limit);
		json = DB3.getAutoStore(tpl, "", "", hm, null);
		JsonA ja = json.getJsonA("root");
		json.remove("root");
		json.put("rows", ja);
		return json;
  }
  
  //根据编号查询设备信息
  @Post
	public JsonA getInfoAllByNums(String equi_arch_nos){
		JsonA  ja=new JsonA();
		if(equi_arch_nos!=null&&!"".equals(equi_arch_nos)){
			
			equi_arch_nos=equi_arch_nos.trim();
			
			String arr[]=equi_arch_nos.split(",");		
			
			String pingjie="";
			for(int i=0;i<arr.length;i++){
				
				String dddd="'"+arr[i]+"'";
				
				
				if(i==arr.length-1){
					pingjie=pingjie+" u.equi_arch_no="+dddd+" ";
				}
				else{
					
					pingjie=pingjie+" u.equi_arch_no="+dddd +" or ";
				}
			}
			
			
			System.out.println(pingjie);
			
			
			String tpl = "select  u.* from other_erp u where "+pingjie+"   order by u.equi_arch_no";
			ja=DB3.getResultAsJsonA(Template.apply(tpl), "");			
		}			
		
		return ja;
		
	} 
  
  
  
  
  
  //呼吸机页面的列表查询，模糊查询加条件查询（楼房region_id_build   楼层region_id_floor     科室  ）
  
  @Post
  public JsonO real_info(String keys, String limit, String offset,String region_id_build ,String region_id_floor,String deptid,String base_state_id)  {
    JsonO json = new JsonO();
    String tpl = "";
    boolean is_add_where=false;
   
    
    String keys_sql="";
    if (keys != ""&& (keys != null) && (keys != "null"))    {
      keys=keys.trim();
      keys_sql=" region.name like '%${keys}%' or ap.mac like '%${keys}%' or r.DEVICE_ID like '%${keys}%' or r.DEVICE_TYPE like '%${keys}%' " ;
      is_add_where=true;
    }    
    
    String region_id_build_sql="";
    if (region_id_build != ""&& (region_id_build != null) && (region_id_build != "null"))    {
    	region_id_build_sql="  region2.id=" +region_id_build;
    	if(is_add_where){
    		region_id_build_sql=" and"+region_id_build_sql;
    	}
    	is_add_where=true;
    	
    }
    
    String region_id_floor_sql="";
    if (region_id_floor != ""&& (region_id_floor != null) && (region_id_floor != "null"))    {
    	region_id_floor_sql="  region_id=" +region_id_floor ;
    	if(is_add_where){
    		region_id_floor_sql=" and"+region_id_floor_sql;
    	}
    	is_add_where=true;
    	
    }
    
    String deptid_sql="";
    if (deptid != ""&& (deptid != null) && (deptid != "null"))    {
    	deptid_sql="  deptid=" +deptid;
    	if(is_add_where){
    		deptid_sql=" and"+deptid_sql;
    	}
    	is_add_where=true;
    
    }
    
    
    
    //再加一个条件是时间是否大于50秒内的数据，表示是备用
    
    String base_state_sql="";
    if (base_state_id != ""&& (base_state_id != null) && (base_state_id != "null")&&!"4".equals(base_state_id))    {
    	base_state_sql="  bs.id=" +base_state_id +" and INSERT_DATE > (select SUBDATE(now(),interval 50 second))";
    	if(is_add_where){
    		base_state_sql=" and"+base_state_sql;
    	}
    	is_add_where=true;
    
    }   
    
    if("4".equals(base_state_id)){
    	base_state_sql="  INSERT_DATE < (select SUBDATE(now(),interval 50 second)) " ;
    	if(is_add_where){
    		base_state_sql=" and"+base_state_sql;
    	}
    	is_add_where=true;
    }
    
    
    
    tpl = "select region.mapserver_name , dept.deptid , dept.deptnm ,   dept.parents , r.* ,region.name ,region.id as  region_id , region2.name as region_father_name , region2.id as region_father_id ,bs.name as run_state ,bs.id as run_state_id from real_data r    left join base_state bs on bs.value = r.USEING_STATU   left join base_ap ap on ap.mac = r.MAC      left join base_region region on region.id = ap.region_id      left join base_region region2 on region2.id = region.parents    left join sys_dept dept on dept.deptid = ap.dept_id   " ;
    
    String is_where="";
    if(is_add_where){
    	is_where=" where ";
    }
    
    tpl=tpl+is_where + keys_sql +region_id_build_sql +region_id_floor_sql +  deptid_sql + base_state_sql +"   order by r.INSERT_DATE desc ";
    
    
    
    System.out.println(tpl);
    
    
      //tpl = "select r.* ,region.name ,region.id as  region_id , region2.name as region_father_name,oe.equi_model ,oe.in_date  ,oe.at_loca ,oe.dept_name , region2.id as region_father_id ,bs.name as run_state from real_data r    left join base_state bs on bs.value = r.USEING_STATU   left join base_ap ap on ap.mac = r.MAC      left join base_region region on region.id = ap.region_id      left join base_region region2 on region2.id = region.parents    left join other_erp oe on oe.equi_arch_no = r.DEVICE_ID    where region.name like '%${keys}%' or ap.mac like '%${keys}%' or r.DEVICE_ID like '%${keys}%' or r.DEVICE_TYPE like '%${keys}%'    order by r.DEVICE_ID desc ";
//    tpl = "select dept.deptid , dept.deptnm , dept.deptnm ,  dept.parents , r.* ,region.name ,region.id as  region_id , region2.name as region_father_name , region2.id as region_father_id ,bs.name as run_state from real_data r    left join base_state bs on bs.value = r.USEING_STATU   left join base_ap ap on ap.mac = r.MAC      left join base_region region on region.id = ap.region_id      left join base_region region2 on region2.id = region.parents    left join sys_dept dept on dept.deptid = ap.dept_id   " +
//      		
//    		" where " +
//    		
//    		"region.name like '%${keys}%' or ap.mac like '%${keys}%' or r.DEVICE_ID like '%${keys}%' or r.DEVICE_TYPE like '%${keys}%' " +
//      		" and region_id=" +region_id_floor +
//      		" and region_father_id=" +region_id_build+
//      		" and deptid=" +deptid+
//      		"   order by r.DEVICE_ID desc ";
      
      
            tpl = Template.apply(tpl, new Object[] { keys, keys, keys, keys });
   
    
    
//    else    {
//      //tpl = "select r.* ,region.name ,region.id as  region_id , region2.name as region_father_name ,oe.equi_model ,oe.in_date  ,oe.at_loca ,oe.dept_name , region2.id as region_father_id  ,bs.name as run_state from real_data r     left join base_state bs on bs.value = r.USEING_STATU     left join base_ap ap on ap.mac = r.MAC      left join base_region region on region.id = ap.region_id   left join other_erp oe on oe.equi_arch_no = r.DEVICE_ID   left join base_region region2 on region2.id = region.parents  order by r.DEVICE_ID desc  ";
//      tpl = "select dept.deptid , dept.deptnm ,  dept.parents , r.* ,region.name ,region.id as  region_id , region2.name as region_father_name  , region2.id as region_father_id  ,bs.name as run_state from real_data r     left join base_state bs on bs.value = r.USEING_STATU     left join base_ap ap on ap.mac = r.MAC      left join base_region region on region.id = ap.region_id    left join base_region region2 on region2.id = region.parents    left join sys_dept dept on dept.deptid = ap.dept_id   order by r.DEVICE_ID desc  ";
//      
//      tpl = Template.apply(tpl);
//    }
    Map<String, String> hm = new HashMap();
    int cc = Integer.valueOf(offset).intValue();
    if (cc == 0) {
        cc = 1;
    }
    hm.put("start", String.valueOf(cc - 1));
    hm.put("limit", limit);
    json = DB3.getAutoStore(tpl, "", "", hm, null);
    JsonA ja = json.getJsonA("root");
    
    
    if("4".equals(base_state_id)){
    	 for (int i = 0; i < ja.size(); i++)    {
    	      JsonO re_jo = ja.getJsonO(i);
    	   
    	    	re_jo.remove("run_state");
    	        re_jo.putQuoted("run_state", "备用");
    	     
    	    }
    }
   
    //如果时间差小于50秒表示备用
    
    for (int x = 0; x < ja.size(); x++){
    	
        //得到一个设备盒子
        JsonO yigeshebei = ja.getJsonO(x);
        //////////////////////////
        String insert_date = yigeshebei.getString("insert_date");
        Date insert_date2 = DateTools.StrToDate(insert_date);
        Date now_date = new Date();
        long shijiancha = now_date.getTime() - insert_date2.getTime();
        if (shijiancha < shijain2) {
         
        } else {
        	yigeshebei.remove("run_state");
        	yigeshebei.putQuoted("run_state", "备用");
        }
        
      }
    
    
    
    
    json.remove("root");
    json.put("rows", ja);
    return json;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  ////////////不分页
  
//呼吸机页面的列表查询，模糊查询加条件查询（楼房region_id_build   楼层region_id_floor     科室  ）
  
  @Post
  public JsonO real_info_nopage(String keys,String region_id_build ,String region_id_floor,String deptid,String base_state_id)  {
    JsonO json = new JsonO();
    String tpl = "";
    boolean is_add_where=false;   
    
    String keys_sql="";
    if (keys != ""&& (keys != null) && (keys != "null"))    {
      keys=keys.trim();
      keys_sql=" region.name like '%${keys}%' or ap.mac like '%${keys}%' or r.DEVICE_ID like '%${keys}%' or r.DEVICE_TYPE like '%${keys}%' " ;
      is_add_where=true;
    }    
    
    String region_id_build_sql="";
    if (region_id_build != ""&& (region_id_build != null) && (region_id_build != "null"))    {
    	region_id_build_sql="  region2.id=" +region_id_build;
    	if(is_add_where){
    		region_id_build_sql=" and"+region_id_build_sql;
    	}
    	is_add_where=true;    	
    }
    
    String region_id_floor_sql="";
    if (region_id_floor != ""&& (region_id_floor != null) && (region_id_floor != "null"))    {
    	region_id_floor_sql="  region_id=" +region_id_floor ;
    	if(is_add_where){
    		region_id_floor_sql=" and"+region_id_floor_sql;
    	}
    	is_add_where=true;    	
    }
    
    String deptid_sql="";
    if (deptid != ""&& (deptid != null) && (deptid != "null"))    {
    	deptid_sql="  deptid=" +deptid;
    	if(is_add_where){
    		deptid_sql=" and"+deptid_sql;
    	}
    	is_add_where=true;
    
    }  
    
    //再加一个条件是时间是否大于50秒内的数据，表示是备用
    
    String base_state_sql="";
    if (base_state_id != ""&& (base_state_id != null) && (base_state_id != "null")&&!"4".equals(base_state_id))    {
    	base_state_sql="  bs.id=" +base_state_id +" and INSERT_DATE > (select SUBDATE(now(),interval 50 second))";
    	if(is_add_where){
    		base_state_sql=" and"+base_state_sql;
    	}
    	is_add_where=true;
    
    }   
    
    if("4".equals(base_state_id)){
    	base_state_sql="  INSERT_DATE < (select SUBDATE(now(),interval 50 second)) " ;
    	if(is_add_where){
    		base_state_sql=" and"+base_state_sql;
    	}
    	is_add_where=true;
    }
    
    
    
    tpl = "select region.mapserver_name , dept.deptid , dept.deptnm ,   dept.parents , r.* ,region.name ,region.id as  region_id , region2.name as region_father_name , region2.id as region_father_id ,bs.name as run_state ,bs.id as run_state_id from real_data r    left join base_state bs on bs.value = r.USEING_STATU   left join base_ap ap on ap.mac = r.MAC      left join base_region region on region.id = ap.region_id      left join base_region region2 on region2.id = region.parents    left join sys_dept dept on dept.deptid = ap.dept_id   " ;
    
    String is_where="";
    if(is_add_where){
    	is_where=" where ";
    }
    
    tpl=tpl+is_where + keys_sql +region_id_build_sql +region_id_floor_sql +  deptid_sql + base_state_sql +"   order by r.DEVICE_ID desc ";
  
 
            tpl = Template.apply(tpl, new Object[] { keys, keys, keys, keys });
   
 
//    }


  //  json = DB3.getAutoStore(tpl, "", "", hm, null);
            JsonA  ja =  DB3.getResultAsJsonA(tpl, "");
    //JsonA ja = json.getJsonA("root");
    
    
    if("4".equals(base_state_id)){
    	 for (int i = 0; i < ja.size(); i++)    {
    	      JsonO re_jo = ja.getJsonO(i);
    	   
    	    	re_jo.remove("run_state");
    	        re_jo.putQuoted("run_state", "备用");
    	     
    	    }
    }
   
    //如果时间差小于50秒表示备用
    
    for (int x = 0; x < ja.size(); x++){
    	
        //得到一个设备盒子
        JsonO yigeshebei = ja.getJsonO(x);
        yigeshebei.remove("breathdata");
        //////////////////////////
        String insert_date = yigeshebei.getString("insert_date");
        Date insert_date2 = DateTools.StrToDate(insert_date);
        Date now_date = new Date();
        long shijiancha = now_date.getTime() - insert_date2.getTime();
        if (shijiancha < shijain2) {
         
        } else {
        	yigeshebei.remove("run_state");
        	yigeshebei.putQuoted("run_state", "备用");
        }
        
      }
    
    json.remove("root");
    json.put("rows", ja);
    return json;
  }
  
  
  
  
  @Post
  public String addErpRegion(String jstr)  {
    JsonO json = new JsonO(jstr);
    try    {
      DB3.saveJsonOToDB(json, "base_erp_region", "id", "region_id");
      return "success";
    }
    catch (Exception e) {    	
    	
    }
    return "fail";
  }
  
  @Post
  public String delErpRegion(String equi_no)  {
    String retun_str = "fail";
    try    {
      DB3.update(Template.apply("delete from base_erp_region where equi_no='${equi_no}'", new Object[] { equi_no }));
      retun_str = "success";
    }
    catch (Exception e)    {
      retun_str = "fail";
    }
    return retun_str;
  }
  
  public static void main(String[] args)  {
    Erp e = new Erp();
    System.out.println(e.real_info_nopage("", "", "", "", ""));
  }
}
