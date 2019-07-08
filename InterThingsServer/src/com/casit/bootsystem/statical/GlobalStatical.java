package com.casit.bootsystem.statical;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;




@Path
public class GlobalStatical {
	
	
	
	//统计全院的设备使用情况
	@Post
	public JsonO info_all(){		
		
		 JsonO j_obj=new JsonO();
		 
		 //统计所有的呼吸机的使用情况
		 
         String tp2 = " SELECT count(*) as count  ,'全院' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU     "

			+" Union "
			
			+" SELECT count(*) as count  ,'使用' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU    WHERE  INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='1'"
			
			+" Union "
			
			+" SELECT count(*) as count  ,'待机' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU    WHERE  INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='2'"
			 
			+" Union "
			
			+" SELECT count(*) as count  ,'关机' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU    WHERE  INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='3'"
			
			+" UNION "
			
			+" SELECT count(*) as count,'备用' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU    WHERE  INSERT_DATE < (select SUBDATE(now(),interval 50 second)) ";
			         
         String tp3 = Template.apply(tp2);
         JsonA json = DB3.getResultAsJsonA(tp3, "");		
         
         //加入报警信息
         
         
         
         j_obj.put("result", json);
		 return j_obj;
		
	}
	
	
	
	
	
	//统计一个部门的使用情况//175291002
	
	
	@Post
 	public JsonO info_dept(String deptid){	
		
        JsonO j_obj=new JsonO();
		 
		 //统计所有的呼吸机的使用情况
		 
         String tp2 = " SELECT count(*) as count  ,'全部' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id    where dept.deptid='"+deptid+"'"

			+" Union "
			
			+" SELECT count(*) as count  ,'使用' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id    WHERE dept.deptid='"+deptid+"' and r.INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='1'"
			
			+" Union "
			
			+" SELECT count(*) as count  ,'待机' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id     WHERE   dept.deptid='"+deptid+"' and r.INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='2'"
			 
			+" Union "
			
			+" SELECT count(*) as count  ,'关机' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id    WHERE   dept.deptid='"+deptid+"' and r.INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='3'"
			
			+" UNION "
			
			+" SELECT count(*) as count,'备用' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  LEFT JOIN sys_dept dept  ON dept.deptid= ap.dept_id     WHERE   dept.deptid='"+deptid+"' and r.INSERT_DATE < (select SUBDATE(now(),interval 50 second)) "; 
			

         
         String tp3 = Template.apply(tp2);
         JsonA json = DB3.getResultAsJsonA(tp3, "");		 
         j_obj.put("result", json);
		 return j_obj;
	}
	
	
	
	//查询所有部门的信息
	@Post
 	public JsonO info_depts(){	
		 JsonO j_obj=new JsonO();
		 
		 JsonA j_a=new JsonA();
		 
		 
		 JsonA json = DB3.getResultAsJsonA("SELECT * FROM sys_dept where parents='175291000'", "");	
		 GlobalStatical g=new GlobalStatical();
		 
		 for(int i=0;i<json.size();i++){
			 
			 //部门id
			 String dept_id=json.getJsonO(i).getString("deptid");
			 JsonO jo= g.info_dept(dept_id);
			 
			 jo.putQuoted("dept_id", dept_id);
			 jo.putQuoted("dept_name", json.getJsonO(i).getString("deptnm"));
			 j_a.add(jo);
			
			 
		 }
		
		 j_obj.put("result", j_a);
		 return j_obj;
	}
	
	
	//查询所有大楼分布的运行情况
	@Post
 	public JsonO info_builds(String region_id){	
		JsonO j_obj=new JsonO();
		 
		 JsonA j_a=new JsonA();
		 
		 //默认是查询所有大楼
		 
		 
		 JsonA json = new JsonA();	
		 
		 //如果传入的参数是大楼的id则查询每一层楼
		 
		 if(region_id!=null&&!"".equals(region_id)){
			 json= DB3.getResultAsJsonA("SELECT * FROM base_region where parents='"+region_id+"'", "");	
		 }
		 else{
			 
			 json= DB3.getResultAsJsonA("SELECT * FROM base_region where parents is null", "");	
		 }
		 
		
		 
		 GlobalStatical g=new GlobalStatical();
		 
		 for(int i=0;i<json.size();i++){
			 
			 //部门id
			 String region_id2=json.getJsonO(i).getString("id");
			 JsonO jo= g.info_build(region_id2);
			 
			 jo.putQuoted("region_id", region_id2);
			 jo.putQuoted("name", json.getJsonO(i).getString("name"));
			 jo.putQuoted("coordinates", json.getJsonO(i).getString("coordinates"));
			 j_a.add(jo);
			
			 
		 }
		
		 j_obj.put("result", j_a);
		 return j_obj;
		
	}
	
	//查询一个大楼中每个楼层的运行情况  大楼的id   //或者是楼层id也行
	@Post
	public JsonO  info_build(String region_id){
		 JsonO j_obj=new JsonO();
		 
		 //如果是大楼id   为br2.id
		 
		 //如果是楼层id   为 br.id
		 
		  String tpl = "select  id,name,parents,mapserver_name from  base_region where parents is  null  ORDER BY sortno ASC ";
		    JsonA ja = DB3.getResultAsJsonA(Template.apply(tpl), "state");
		    if (region_id == null) {
		      //如果为空设置默认为1住
		    	region_id = ja.getJsonO(0).getString("id");
		    }    
		    //是否为1级菜单
		    boolean is_father = false;
		    for (int x = 0; x < ja.size(); x++){
		      JsonO re_jo_x = ja.getJsonO(x);
		      if (region_id.equals(re_jo_x.getString("id"))) {
		        is_father = true;
		      }
		    }
		    
		    String dddd="br2";
		    if (is_father) {
		    	
		    }
		    else{
		    	dddd="br";
		    }
		    
		 
              String tp2="SELECT count(*) as count  ,'全部' as state_name from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac   left join base_region br on br.id = ap.region_id  left join base_region br2 on br2.id = br.parents    where "+dddd+".id='"+region_id+"'"

+" Union "

+" SELECT count(*) as count  ,'使用' as state_name   from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  left join base_region br on br.id = ap.region_id  left join base_region br2 on br2.id = br.parents      WHERE  "+dddd+".id='"+region_id+"' and r.INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='1'"

+" Union "

+" SELECT count(*) as count  ,'待机' as state_name   from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  left join base_region br on br.id = ap.region_id  left join base_region br2 on br2.id = br.parents       WHERE   "+dddd+".id='"+region_id+"'   and r.INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='2'"
 
+" Union "

+" SELECT count(*) as count  ,'关机' as state_name   from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  left join base_region br on br.id = ap.region_id  left join base_region br2 on br2.id = br.parents      WHERE     "+dddd+".id='"+region_id+"'   and r.INSERT_DATE > (select SUBDATE(now(),interval 50 second))  and r.USEING_STATU='3'"

+" UNION "

+" SELECT count(*) as count,'备用' as state_name    from real_data r     LEFT JOIN base_state state  ON state.value= r.USEING_STATU   LEFT JOIN base_ap ap  ON ap.mac= r.mac  left join base_region br on br.id = ap.region_id  left join base_region br2 on br2.id = br.parents      WHERE     "+dddd+".id='"+region_id+"'   and r.INSERT_DATE < (select SUBDATE(now(),interval 50 second))  ";
              String tp3 = Template.apply(tp2);
              JsonA json = DB3.getResultAsJsonA(tp3, "");	
              j_obj.put("result", json);
              
              return j_obj;
	}
	
	public static void main(String args[]){
		
		GlobalStatical  g=new GlobalStatical();
		JsonO   j=g.info_builds(null);
		System.out.println(j);
		
	}
	

}
