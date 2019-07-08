package com.casit.bootsystem.breath;

import com.casit.bootsystem.mongodb.MongoDaoImpl;
import com.casit.bootsystem.mongodb.MongoHelper;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

@Path("BreathMac")
public class BreathMac{	
 
  SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

  @Post
  public JsonA info(String device_id)  {
    JsonA json = new JsonA();
    String tpl = "";
    if ((!("".equals(device_id))) && (device_id != null)) {
      // tpl = "SELECT * FROM real_data WHERE INSERT_DATE > (select SUBDATE(now(),interval 40 second))   and DEVICE_ID='" + device_id + "' Group By DEVICE_ID   order by DEVICE_ID desc ;";
      tpl = " SELECT r.* ,br.name as floor_name ,br2.name as build_name,br.mapserver_name ,br.id as region_id FROM real_data r   left join base_ap ap on ap.mac = r.MAC   left join base_region br on br.id = ap.region_id   left join base_region br2 on br2.id = br.parents  WHERE r.DEVICE_ID='" + device_id + "' order By r.DEVICE_ID;";
    }
    else    {
     //  tpl = "SELECT r.DEVICE_ID ,r.DEVICE_TYPE ,r.MAC ,r.IP,r.INSERT_DATE ,br.name as floor_name ,br2.name as build_name FROM real_data r   left join base_ap ap on ap.mac = r.MAC   left join base_region br on br.id = ap.region_id   left join base_region br2 on br2.id = br.parents   WHERE INSERT_DATE > (select SUBDATE(now(),interval 40 second))    Group By DEVICE_ID   order by r.DEVICE_ID desc;";
     tpl = "SELECT r.DEVICE_ID ,r.DEVICE_TYPE ,r.MAC ,r.IP,r.INSERT_DATE ,br.name as floor_name ,br2.name as build_name FROM real_data r   left join base_ap ap on ap.mac = r.MAC   left join base_region br on br.id = ap.region_id   left join base_region br2 on br2.id = br.parents     order By r.DEVICE_ID;";
    }
    tpl = Template.apply(tpl);
    json = DB3.getResultAsJsonA(tpl, "");
    
    
    
    //先一次查询出来属性的分类
    String tp2 = "SELECT filed_name_zh  ,filed_name ,class_id  FROM sys_breath_attr  ";
    JsonA  attrs=DB3.getResultAsJsonA(Template.apply(tp2), "");
    //设置每个实时数据参数的分类
    
    if(json.size()==1){
    	JsonO jobj=json.getJsonO(0);
    	String breathdata=jobj.getString("breathdata");
    	
    	JSONObject   jj=new JSONObject(breathdata);
    	
    	
    	JsonO   jj_new=new JsonO();
    	
    	 Iterator keys = jj.keys();
		 while(keys.hasNext()){
				 
			 String key = keys.next().toString();
			 System.out.println(key);
			 JsonO   obj=new JsonO();
			 obj.putQuoted("value", jj.getString(key));
			 
			 //查询CLASS_ID
			 
			 for(int i=0;i<attrs.size();i++){
				 
				if(key.equals(attrs.getJsonO(i).getString("filed_name_zh"))){
					
					obj.putQuoted("class_id", attrs.getJsonO(i).getString("class_id"));
				} 
			 }
			
			 
			 
			 jj_new.put(key, obj);
		 }
    	//从新设置breathdata
		 jobj.put("breathdata", jj_new);
    	
    }
    
    
    
    return json;
  }
  
  
  //分页查询历史纪录根据设备id   和时间区间查询 
  @Post
  public JsonO  history_info(String device_id,String limit,String offset,String start_date,String end_date)  {
	    MongoHelper mongoHelper = new MongoHelper();
	    MongoClient mongoClient = mongoHelper.getMongoClient();
	    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
	    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();

	    
	    BasicDBObject dbObject = new BasicDBObject();
	    //加入时间条件
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
	    BasicDBObject queryObject = new BasicDBObject("mydate2",dbObject);	
	    
	    BasicDBObject key=new BasicDBObject();//指定需要显示列
//	    key.append("low_tidal_volume_alarm_status", true);
//	    key.append("ventilator_system_clock", true);    
//	    key.append("DEVICE_ID", true); 
	    //queryObject.put("bianhao", device_id);//02003110365
	    //queryObject.put("code", "200109109");
	    queryObject.put("DEVICE_ID", device_id);//先默认一个固定的
	 
	   
	    List<Map<String, Integer>>  ee= mongoDaoImpl.queryByDocByPage(mongoDataBase, "breath_info", queryObject,key,Integer.valueOf(limit),Integer.valueOf(offset));
	    //System.out.println( "总共:"+ ee.size());
	    JsonA  ja=new JsonA();
	      for(int i=0;i<ee.size();i++){  
	    	  JSONObject joo = new JSONObject(ee.get(i).toString());
	    	  ja.add(joo);
	      }
	      
	     //查询总数  
	      
	      long  zongshu= mongoDaoImpl.queryByDocNum(mongoDataBase, "breath_info", queryObject,key);
	      
	      JsonO  ja_obj=new JsonO();
	      ja_obj.putUnQuoted("total", String.valueOf(zongshu));
	      ja_obj.put("rows", ja);
	      
	      mongoHelper.closeMongoClient(mongoDataBase, mongoClient);
	      return ja_obj;
  }
  

  public static void main(String[] args)  {
   // JsonA quyu_ids = DB3.getResultAsJsonA("select * from base_ap ap where  ap.mac='11' ", "");	  
	  BreathMac  dd=new BreathMac();
	 // System.out.print(dd.history_info("20160700154","12","0","", ""));
	  System.out.print(dd.info("02004090194"));
  }
}