package com.casit.bootsystem.base;

import java.util.HashMap;
import java.util.Map;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;


@Path
public class Warning {
	
	//报警历史数据查看
	  @Post
	  public JsonO info(String keys, String limit, String offset)  {
		    JsonO json = new JsonO();
		    String tpl = "";
		    if ((keys != "") && (keys != null) && (keys != "null"))  {
		      tpl = "SELECT w.* ,r.DEVICE_TYPE  ,dept.deptnm ,ba.filed_name_zh FROM base_warning w  LEFT JOIN real_data r on r.DEVICE_ID=w.device_id      LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id  LEFT JOIN sys_breath_attr ba  ON ba.filed_name= w.warning_field     where dept.deptnm like '%${keys}%' or r.DEVICE_TYPE like '%${keys}%' or w.device_id like '%${keys}%' or ba.filed_name_zh like '%${keys}%'  order by w.warning_date desc";	      
		      tpl = Template.apply(tpl, new Object[] { keys, keys, keys, keys });
		    }
		    else {
		      tpl = "SELECT w.* ,r.DEVICE_TYPE  ,dept.deptnm ,ba.filed_name_zh FROM base_warning w  LEFT JOIN real_data r on r.DEVICE_ID=w.device_id      LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id  LEFT JOIN sys_breath_attr ba  ON ba.filed_name= w.warning_field      order by w.warning_date desc";
		      tpl = Template.apply(tpl);
		    }
		    Map<String, String> hm = new HashMap();
		    hm.put("start", String.valueOf(Integer.valueOf(offset).intValue() - 1));
		    hm.put("limit", limit);
		    json = DB3.getAutoStore(tpl, "", "", hm, null);
		    JsonA ja = json.getJsonA("root");
		    json.remove("root");
		    json.put("rows", ja);
		    return json;
	  }
	  
	  
	  
		@Post
		public JsonO StaticInfo(){
			
			//今日警告类型统计
			String tpl = "SELECT count(*) num ,ba.filed_name_zh FROM base_warning w  LEFT JOIN real_data r on r.DEVICE_ID=w.device_id      LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id  LEFT JOIN sys_breath_attr ba  ON ba.filed_name= w.warning_field where to_days(w.warning_date) = to_days(now()) GROUP BY ba.filed_name_zh";

			//今日设备的类型警告统计
			String tp2 = "SELECT count(*) num ,r.DEVICE_TYPE  device_type FROM base_warning w  LEFT JOIN real_data r on r.DEVICE_ID=w.device_id      LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id  LEFT JOIN sys_breath_attr ba  ON ba.filed_name= w.warning_field where to_days(w.warning_date) = to_days(now())  GROUP BY r.DEVICE_TYPE";

			//今日部门警告统计
			String tp3 = "SELECT count(*) num ,dept.deptnm deptnm FROM base_warning w  LEFT JOIN real_data r on r.DEVICE_ID=w.device_id      LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id  LEFT JOIN sys_breath_attr ba  ON ba.filed_name= w.warning_field where to_days(w.warning_date) = to_days(now()) and dept.deptnm is not NULL  GROUP BY dept.deptnm";

			//查询当前设备的实时   报警信息
			String tp4 = "SELECT region2.coordinates , w.* ,r.DEVICE_TYPE  ,dept.deptnm ,ba.filed_name_zh  ,region.name as floor_name ,region.id as  region_id , region2.name as region_father_name , region2.id as region_father_id   FROM base_warning w  LEFT JOIN real_data r on r.DEVICE_ID=w.device_id      LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id  LEFT JOIN sys_breath_attr ba  ON ba.filed_name= w.warning_field     left join base_region region on region.id = ap.region_id      left join base_region region2 on region2.id = region.parents   where  w.warning_date > (select SUBDATE(now(),interval 50 second)) ";

			

					
					
			JsonA  ja1=DB3.getResultAsJsonA(Template.apply(tpl), "");
			JsonA  ja2=DB3.getResultAsJsonA(Template.apply(tp2), "");
			JsonA  ja3=DB3.getResultAsJsonA(Template.apply(tp3), "");
			JsonA  ja4=DB3.getResultAsJsonA(Template.apply(tp4), "");
			
			JsonO   newjob=new JsonO();
			newjob.put("warning_type", ja1);
			newjob.put("warning_device_type", ja2);
			newjob.put("warning_deptnm", ja3);
			newjob.put("warning_realtime", ja4);
			
			
			return newjob;
			
		} 
		
		public static void main(String args[]){
			
			Warning w=new Warning();
			//System.out.println(w.info("","10","1"));
			System.out.println(w.StaticInfo());
		}

}
