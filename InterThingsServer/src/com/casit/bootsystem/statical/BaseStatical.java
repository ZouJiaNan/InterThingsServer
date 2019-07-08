package com.casit.bootsystem.statical;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import com.casit.bootsystem.config.SysConfig;
import com.casit.bootsystem.ct.Ct;
import com.casit.bootsystem.erp.Erp;
import com.casit.bootsystem.mongodb.MongoDaoImpl;
import com.casit.bootsystem.mongodb.MongoHelper;
import com.casit.bootsystem.monitor.Monitor;
import com.casit.bootsystem.mri.Mri;
import com.casit.bootsystem.util.DateTools;
import com.casit.bootsystem.util.DateUtil;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Path
public class BaseStatical {
		
	
	//查询一个设备当天时间的历史数据
	
	 @Post
	  public JsonA  history_info(String device_id)  {
		 
		    MongoHelper mongoHelper = new MongoHelper();
		    MongoClient mongoClient = mongoHelper.getMongoClient();
		    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
		    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();

		    BasicDBObject dbObject = new BasicDBObject();
			dbObject.put("$gte", DateUtil.getStartTime());
		    dbObject.put("$lte", DateUtil.getEndTime());
		    
			//获取指定字段
		    BasicDBObject key=new BasicDBObject();//指定需要显示列
//		    key.append("low_tidal_volume_alarm_status", true);
//		    key.append("ventilator_system_clock", true);    
//		    key.append("DEVICE_ID", true); 
		    
		    BasicDBObject queryObject = new BasicDBObject("mydate2",dbObject);		    
		    //再加一个条件
		    queryObject.append("DEVICE_ID", device_id);//"02010040366"
		    List<Map<String, Integer>>  ee= mongoDaoImpl.queryByDoc(mongoDataBase, "breath_info", queryObject,key);
		    //System.out.println( "总共:"+ ee.size());
		    JsonA  ja=new JsonA();
		      for(int i=0;i<ee.size();i=i+2){  
		    	  JSONObject joo = new JSONObject(ee.get(i).toString());
		    	  ja.add(joo);
		      }
		      mongoHelper.closeMongoClient(mongoDataBase, mongoClient);
		      return ja;
	     }

		//查询一个设备一个时间段的历史数据
		
		 @Post
		  public JsonA  history_info_qujian(String device_id,String start_date,String end_date)  {
			 
			    MongoHelper mongoHelper = new MongoHelper();
			    MongoClient mongoClient = mongoHelper.getMongoClient();
			    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
			    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();

			    BasicDBObject dbObject = new BasicDBObject();
			    
			    //查询一个时间段的 
			    if(start_date!=null&&end_date!=null&&!"".equals(end_date)&&!"".equals(start_date)&&!"null".equals(end_date)&&!"null".equals(start_date)){
			    	dbObject.put("$gte", DateTools.StrToDate(start_date));
				    dbObject.put("$lte", DateTools.StrToDate(end_date));
			    }
			    else{
			    	//查询一天的 
			    	dbObject.put("$gte", DateUtil.getStartTime());
				    dbObject.put("$lte", DateUtil.getEndTime());
			    }
			    
				  //获取指定字段
			    BasicDBObject key=new BasicDBObject();//指定需要显示列
//			    key.append("low_tidal_volume_alarm_status", true);
//			    key.append("ventilator_system_clock", true);    
//			    key.append("DEVICE_ID", true);    
			    
			    BasicDBObject queryObject = new BasicDBObject("mydate2",dbObject);		    
			    //再加一个条件
			    queryObject.append("DEVICE_ID", device_id);//"02010040366"

			    List<Map<String, Integer>>  ee= mongoDaoImpl.queryByDoc(mongoDataBase, "breath_info", queryObject,key);
			    //System.out.println( "总共:"+ ee.size());
			    JsonA  ja=new JsonA();
			      for(int i=0;i<ee.size();i=i+2){   			    	
			    	  JSONObject joo = new JSONObject(ee.get(i).toString());
			    	  ja.add(joo);
			      }
			      mongoHelper.closeMongoClient(mongoDataBase, mongoClient);
			      return ja;
		 }
		 
		 
	 
	 //查询一台设备一个时间段的运行情况统计（当天）  只提取3个指标
	 

	 @Post
	  public JsonA  history_info2(String device_id,String start_date,String end_date)  {
		 
		 BaseStatical bs=new BaseStatical();
		 
		 //查询一天的所有历史数据 
		 JsonA  ja=bs.history_info_qujian(device_id,start_date,end_date);
		 
		 long d1=DateUtil.getStartTime().getTime();
		 long d2;		 
		 
		 JsonA  newja=new JsonA();
		
		 for(int i=0;i<ja.size();i++){
			  
			String jj=ja.getObject(i).toString();
			JsonO jobj=new JsonO(jj);
			String shijina= jobj.getString("INSERT_DATE");		
			d2=	DateTools.StrToDate(shijina).getTime();	
			long shijiancha=d2-d1;		
			long jiange_num=shijiancha/60000;	//间隔1分钟 		
			
			//补上时间 
			for(int j=0;j<jiange_num-1;j++){
				
				 //System.out.println("补上:"+j);
				 JsonO bb=new JsonO();
				 
				 bb.putQuoted("tidal_volume", "0");
				 bb.putQuoted("total_minute_volume", "0");
				 bb.putQuoted("peak_inhalation_pressure", "0");
				 bb.putQuoted("INSERT_DATE",  DateTools.DateToStr(new Date(d1)));
				 newja.add(bb);
				 d1=d1+60000;
			}			
			
			JsonO jobj2=new JsonO();
			jobj2.putQuoted("tidal_volume", jobj.getString("tidal_volume"));
			jobj2.putQuoted("total_minute_volume", jobj.getString("total_minute_volume"));
			jobj2.putQuoted("peak_inhalation_pressure", jobj.getString("peak_inhalation_pressure"));
			jobj2.putQuoted("INSERT_DATE",  jobj.getString("INSERT_DATE"));
			newja.add(jobj2);
			d1=d2;
			 
		 }
		 System.out.println(newja);
		 return newja;
		
	 }	 
	 
	 
	 //统计一个设备的使用效率情况（一个时间段）   开机次数 和运行时间数
	 @Post
	 public JsonA  staticUseing(String device_id,String start_date,String end_date){
		 
		 JsonA   ja=new JsonA();
		 
		 //一个时间段
		 
		 String tp3 = "select  su.device_id ,su.static_date ,su.start_times  ,su.total_run_time ,dept.deptnm    from  static_useing su     left join real_data r on r.DEVICE_ID = su.device_id   left join base_ap ap on ap.mac = r.MAC     left join sys_dept dept on dept.deptid = ap.dept_id      where su.device_id ='"+device_id+"'   and su.static_date >='"+start_date+"'  and  su.static_date <= '"+end_date+"'";
		 ja = DB3.getResultAsJsonA(Template.apply(tp3), "total_run_time,start_times");
		 
		//-- 近7天  
		//select  su.* from  static_useing su where su.device_id ='"+device_id+"' and  date_sub(CURDATE(),INTERVAL 7 DAY) <= DATE(su.static_date);  
		//String tp4 = "select  su.* from  static_useing su where su.device_id ='"+device_id+"'  and  date_sub(CURDATE(),INTERVAL 7 DAY) <= DATE(su.static_date)";
		//ja = DB3.getResultAsJsonA(Template.apply(tp4), "total_run_time,start_times");
		 
		 return ja;
	 }
	 
	 //查询所有设备的运行情况统计  按 天   安周  按 月  按时间间隔
	 @Post
	 public JsonA  staticUseingAllDecices(String start_date,String end_date){
		 
		 JsonA   ja=new JsonA();
//		 String ssss="SELECT * FROM ((SELECT SUM(start_times) as start_times ,device_id FROM "+"static_useing WHERE device_id='20130500765' and static_date='2019-03-12 00:00:00') a  ,"+
//"(SELECT SUM(total_run_time) as total_run_time  FROM static_useing WHERE device_id='20130500765') b) ";
		 
		 String tp3 = "select  DEVICE_ID from  real_data  ";
		 ja = DB3.getResultAsJsonA(Template.apply(tp3), "total_run_time");
		 String ssss="";
		 //拼接sql字符串
		 //System.out.println(ja.size());
		 for(int i=0;i<ja.size();i++){
			String device_id= ja.getJsonO(i).getString("device_id");
			String ssss1="  SELECT * FROM ("+
"(SELECT dept.deptnm ,  sta_use.device_id ,rd.DEVICE_TYPE ,br2.name as build_name,br.name as floor_name,rd.MAC ,SUM(sta_use.start_times) as start_times ,     TRUNCATE(SUM(sta_use.total_run_time) /60,1)   as total_run_time FROM static_useing sta_use" +
" LEFT JOIN real_data rd ON rd.DEVICE_ID=sta_use.device_id    " +
"left join base_ap ap on ap.mac = rd.MAC   " +
" left join sys_dept dept on dept.deptid = ap.dept_id    " +
"left join base_region br on br.id = ap.region_id   " +
"left join base_region br2 on br2.id = br.parents " +
"WHERE sta_use.device_id='"+device_id+"' and " +
"sta_use.static_date >='"+start_date+"'  and sta_use.static_date <='"+end_date+"' ) a  )   WHERE device_id is NOT NULL ";
			 if(i==ja.size()-1){
				 ssss=ssss+ssss1+"  ";
			 }
			 else{
				 ssss=ssss+ssss1+"  UNION ";
			 }
		 }
		//System.out.println(ssss);
		 //排序  
		 ssss=ssss+"  order by total_run_time DESC ";
		 
	 JsonA   ja2 = DB3.getResultAsJsonA(Template.apply(ssss), "total_run_time");
	 //System.out.println(ja2);
		 return ja2;
	 }
	 
	 //设备类型数量统计,联网的设备
	 @Post
	 public  JsonO  staticTypeNumber(){
		 
		 Erp ep=new Erp();
		 Ct ct=new Ct();
		 Mri mri=new Mri();
		 Monitor monitor=new Monitor();
		 
		 //查询V60数量
		 int v60_num=0;
		 JsonO  jo1= ep.real_info("V60", "10", "0",null,null,null,null);
		 v60_num=jo1.getInt("total");
		 
		 //查询PB840数量
		 int pb840_num=0;
		 JsonO  jo2= ep.real_info("PB840", "10", "0",null,null,null,null);
		 pb840_num= jo2.getInt("total");
		 
		 //查询CT数量
		 int ct_num=0;
		 JsonO  jo3= ct.getErpInfo(null,null,null);
		 ct_num=Integer.valueOf(jo3.getInt("total")) ;
		 
		 //查询MRI数量
		 int mri_num=0;
		 JsonO  jo4= mri.getErpInfo();
		 mri_num= Integer.valueOf(jo4.getString("total")) ;
		 
		 //查询监护仪数量
		 
		 int monitor_num=0;
		 JsonO  jo5= monitor.getErpInfo();
		 monitor_num= Integer.valueOf(jo5.getString("total")) ;
		 /////////////////////////////////////////////////////////////////////////////////////////////////////////
		 int breath_all_number=0;
		 
		 breath_all_number=v60_num+pb840_num;
		 JsonA all=new JsonA();
		 
		 //统计呼吸机
		 JsonO   obj_breath=new JsonO();		 
		 obj_breath.putUnQuoted("total", String.valueOf(breath_all_number));
		 obj_breath.putQuoted("name", "呼吸机");
		 JsonA all_breath=new JsonA();
		      
		     JsonO   obj_breath_v60=new JsonO();
		     obj_breath_v60.putQuoted("type_name", "V60");
		     obj_breath_v60.putQuoted("name", "飞利浦"+"V60");
		     obj_breath_v60.putUnQuoted("total_number", String.valueOf(v60_num));		 
		     all_breath.add(obj_breath_v60);		     
		     
		     JsonO   obj_breath_pb840=new JsonO();
		     obj_breath_pb840.putQuoted("type_name", "PB840");
		     obj_breath_pb840.putQuoted("name", "泰科"+"PB840");
		     obj_breath_pb840.putUnQuoted("total_number",  String.valueOf(pb840_num));		 
		     all_breath.add(obj_breath_pb840);
		     
		     JsonO   obj_breath_mairui=new JsonO();
		     obj_breath_mairui.putQuoted("type_name", "SV350");
		     obj_breath_mairui.putQuoted("name", "迈瑞"+ "SV350");
		     obj_breath_mairui.putUnQuoted("total_number", "0");		 
		     all_breath.add(obj_breath_mairui);		     
		     
		  obj_breath.put("types", all_breath);
		  
		  
		  //统计ct		 
		  JsonO   obj_ct=new JsonO();		 
		  obj_ct.putUnQuoted("total", String.valueOf(ct_num));
		  obj_ct.putQuoted("name", "CT");
		  JsonA all_ct=new JsonA();
	      
		     JsonO   obj_ct_ge=new JsonO();
		     obj_ct_ge.putQuoted("type_name", "GE009");
		     obj_ct_ge.putQuoted("name", "GE"+"GE009");
		     obj_ct_ge.putUnQuoted("total_number", String.valueOf(ct_num));		 
		     all_ct.add(obj_ct_ge);	
		 
		     obj_ct.put("types", all_ct);

		     
		   //统计mri		 
			  JsonO   obj_mri=new JsonO();		 
			  obj_mri.putUnQuoted("total",  String.valueOf(mri_num));
			  obj_mri.putQuoted("name", "MRI");
			  JsonA all_mri=new JsonA();
		      
			     JsonO   obj_mri_philip=new JsonO();
			     obj_mri_philip.putQuoted("type_name", "PH009");
			     obj_mri_philip.putQuoted("name", "飞利浦"+"PH009");
			     obj_mri_philip.putUnQuoted("total_number", String.valueOf(mri_num));		 
			     all_mri.add(obj_mri_philip);	
			 
			     obj_mri.put("types", all_mri);
			     
			  
			     //统计监护仪	 
				  JsonO   obj_monitor=new JsonO();		 
				  obj_monitor.putUnQuoted("total", String.valueOf(monitor_num));
				  obj_monitor.putQuoted("name", "监护仪");
				  JsonA all_monitor=new JsonA();
			      
				     JsonO   obj_monitor_mairui=new JsonO();
				     obj_monitor_mairui.putQuoted("type_name", "MR009");
				     obj_monitor_mairui.putQuoted("name", "迈瑞"+"MR009");
				     obj_monitor_mairui.putUnQuoted("total_number", String.valueOf(monitor_num));		 
				     all_monitor.add(obj_monitor_mairui);	
				 
				     obj_monitor.put("types", all_monitor);     
			     
		 //完成
		     
		     all.add(obj_breath);
		     all.add(obj_ct);
		     all.add(obj_mri);
		     all.add(obj_monitor);
		     
		     
		     JsonO all_obj=new JsonO();
		     all_obj.put("result", all);
		     //所有设备数量
		    int t_all_num= breath_all_number+monitor_num+mri_num+ct_num;
		    all_obj.putUnQuoted("total", String.valueOf(t_all_num));
		 
		 return  all_obj;
	 }
	 
	 
	 
	 //按统计每个住院大楼设备运行数据
	 @Post
	 public  JsonA  staticRegionNumber(){
		 JsonA ja=new JsonA();
		 
		//查询有多少个楼
		 String tpl = "select  id,name from  base_region where parents is  null  ORDER BY sortno ASC ";
		 JsonA ja_builds = DB3.getResultAsJsonA(Template.apply(tpl), "state");
		   
		    BaseStatical bs=new BaseStatical();
		
		    for (int i = 0; i < ja_builds.size(); i++){		    	  
		        JsonO re_jo = ja_builds.getJsonO(i);
		        //得到一级区域的id
		         
		        JsonO jj=bs.getRunningNumByRegionID(re_jo.getString("id"));
		        jj.putUnQuoted("region_id", re_jo.getString("id"));
		        jj.putQuoted("region_name", re_jo.getString("name"));
		       
		        ja.add(jj);
		    }
		    
		
		    System.out.println(ja);
		 
		 
		 return ja;
	 }
	 
	 
	 
	 //根据区域id查询一个区域中的设备运行数量
	 @Post
	 public JsonO  getRunningNumByRegionID(String redion_id){
		 
		   String jiedian="br2";
		    String tp3 = "select  id,name,parents,mapserver_name from  base_region where parents is  null  ORDER BY sortno ASC ";
		    JsonA ja3 = DB3.getResultAsJsonA(Template.apply(tp3), "state");
		 
		  //是否为1级菜单
		    boolean is_father = false;
		    for (int x = 0; x < ja3.size(); x++){
		      JsonO re_jo_x = ja3.getJsonO(x);
		      if (redion_id.equals(re_jo_x.getString("id"))) {
		        is_father = true;
		      }
		    }
		    if (is_father) {
		    	jiedian="br2";
		    	
		    }
		    else{
		    	jiedian="br";
		    }
		    
		    
		    
		 JsonO ja=new JsonO();
		 
		 String tpl = "SELECT SUM(all_num) as all_num ,SUM(real_num) as real_num ,SUM(waiting_num) as waiting_num  ,SUM(beiyong_num) as beiyong_num ,SUM(stop_num) as stop_num from ("+

" SELECT  COUNT(*) as all_num ,0 AS real_num ,0 AS waiting_num  ,0 AS beiyong_num ,0 AS stop_num FROM real_data rd  left join base_ap ap on ap.mac = rd.MAC   left join base_region br on br.id = ap.region_id  "+
"   left join base_region br2 on br2.id = br.parents   WHERE "+jiedian+".id="+redion_id+

" UNION"+

" SELECT  0 as all_num ,COUNT(*) AS real_num ,0 AS waiting_num  ,0 AS beiyong_num  ,0 AS stop_num FROM real_data rd  left join base_ap ap on ap.mac = rd.MAC   left join base_region br on br.id = ap.region_id  "+
"   left join base_region br2 on br2.id = br.parents   WHERE "+jiedian+".id="+redion_id+"  and rd.USEING_STATU=1  and rd.INSERT_DATE > (select SUBDATE(now(),interval "+SysConfig.REAL_SPACE_TIME+" second))"+

" UNION"+

" SELECT  0 as all_num ,0 AS real_num ,0 AS waiting_num  ,0 AS beiyong_num ,COUNT(*) AS stop_num FROM real_data rd  left join base_ap ap on ap.mac = rd.MAC   left join base_region br on br.id = ap.region_id  "+
 "  left join base_region br2 on br2.id = br.parents   WHERE "+jiedian+".id= "+redion_id+" and rd.USEING_STATU=3 and  rd.INSERT_DATE > (select SUBDATE(now(),interval "+SysConfig.REAL_SPACE_TIME+" second))"+

" UNION"+


" SELECT  0 as all_num ,0 AS real_num ,0 AS waiting_num ,COUNT(*) AS beiyong_num ,0 AS stop_num FROM real_data rd  left join base_ap ap on ap.mac = rd.MAC   left join base_region br on br.id = ap.region_id  "+
"  left join base_region br2 on br2.id = br.parents   WHERE "+jiedian+".id= "+redion_id+" and  rd.INSERT_DATE < (select SUBDATE(now(),interval "+SysConfig.REAL_SPACE_TIME+" second))"+

" UNION"+



" SELECT  0 as all_num ,0 AS real_num ,COUNT(*) AS waiting_num  ,0 AS beiyong_num  ,0 AS stop_num FROM real_data rd  left join base_ap ap on ap.mac = rd.MAC   left join base_region br on br.id = ap.region_id  "+
"   left join base_region br2 on br2.id = br.parents   WHERE "+jiedian+".id="+redion_id+" and rd.USEING_STATU=2  and rd.INSERT_DATE > (select SUBDATE(now(),interval "+SysConfig.REAL_SPACE_TIME+" second)) ) a";
		    ja = DB3.getSingleRowAsJsonO(Template.apply(tpl), "state");
		   
		 return ja;
	 }
	 
	 public static void main(String args[]){
		 
		
		 BaseStatical bs=new BaseStatical();
		   //for(int i=0;i<10;i++){
			// JsonO jj=bs.getRunningNumByRegionID("175191003");
			 
			
JsonA jjj=bs.staticUseingAllDecices( "2018-1-1 00:00:00", "2019-4-1 00:00:00");
//System.out.println(jjj);	 
		// JsonA jj=bs.staticRegionNumber();

//System.out.println(Math.abs(-10));
	 }
	      
	

}
