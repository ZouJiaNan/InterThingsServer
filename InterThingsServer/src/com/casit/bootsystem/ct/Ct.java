package com.casit.bootsystem.ct;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.casit.bootsystem.mongodb.MongoDaoImpl;
import com.casit.bootsystem.mongodb.MongoHelper;
import com.casit.bootsystem.util.DateTools;
import com.casit.bootsystem.util.DateUtil;
import com.casit.bootsystem.util.HttpRequest;
import com.casit.bootsystem.util.SysSetting;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Path
public class Ct {
	  
	  //从erp的视图中去取数据
	  @Post
	  public JsonO getErpInfo(String keys, String limit, String offset)  {
	    String ddd = "";
	    JsonO json = new JsonO();

	     //keys="02002120053,02003110364,02003110365,02006120495,02006120496,02006120497,02006120922,20151100412,20151100414,20161200028";
	     //keys="ZCKP20180100341,ZCKP20180700446,ZCKP20180700447";
	     keys="";
	     
	     //从数据库中读取要集成的ERP的编号
	     
	     JsonA erp_things_ids = DB3.getResultAsJsonA("select * from other_erp_things where type_name='ct' order by id", "");
	     
	     for(int i=0;i<erp_things_ids.size();i++){
	    	  
	    	String device_id= "ZCKP"+erp_things_ids.getJsonO(i).getString("device_id");
	    	
	    	if(i==erp_things_ids.size()-1){
	    		
	    		keys=keys+device_id;
	    	}
	    	else{
	    		
	    		keys=keys+device_id+",";
	    	}
	    	
	     }
		    
	     
	     
	     
	      try {
			ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/getInfoAllByNums?equi_arch_nos=" + URLEncoder.encode(keys, "UTF-8"), "");
		} catch (UnsupportedEncodingException e) {
			ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/getInfoAllByNums?equi_arch_nos=" +keys, "");;
		}      
	       JSONArray joo = new JSONArray(ddd);
	       JsonA ja = new JsonA(ddd.toString());
	       json.put("rows", ja);
	 	  
	       json.putUnQuoted("total", keys.split(",").length+"");
	  
	      return json;
	  }
	  
	  //分页查询历史纪录
	  @Post
	  public JsonO  history_info(String device_id,String limit,String offset)  {
		    MongoHelper mongoHelper = new MongoHelper();
		    MongoClient mongoClient = mongoHelper.getMongoClient();
		    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
		    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();

		    BasicDBObject queryObject = new BasicDBObject();
		    BasicDBObject key=new BasicDBObject();//指定需要显示列

		    //queryObject.put("bianhao", device_id);//02003110365
		    //queryObject.put("code", "200109109");
		    queryObject.put("bianhao", "02003110365");//先默认一个固定的
		    
		    
		    List<Map<String, Integer>>  ee= mongoDaoImpl.queryByDocByPage(mongoDataBase, "ct_info", queryObject,key,Integer.valueOf(limit),Integer.valueOf(offset));
		    System.out.println( "总共:"+ ee.size());
		    JsonA  ja=new JsonA();
		      for(int i=0;i<ee.size();i++){  
		    	  JSONObject joo = new JSONObject(ee.get(i).toString());
		    	  ja.add(joo);
		      }
		      
		     //查询总数  
		      
		      long  zongshu= mongoDaoImpl.queryByDocNum(mongoDataBase, "ct_info", queryObject,key);
		      
		      JsonO  ja_obj=new JsonO();
		      ja_obj.putUnQuoted("total", String.valueOf(zongshu));
		      ja_obj.put("rows", ja);
		      
		      mongoHelper.closeMongoClient(mongoDataBase, mongoClient);
		      return ja_obj;
	  }
	  
	  //统计一个设备扫描部位的次数（在一个时间段里面）
	  @Post
	  public JsonA  static_info(String device_id,String start_date,String end_date)  {
		  
		    MongoHelper mongoHelper = new MongoHelper();
		    MongoClient mongoClient = mongoHelper.getMongoClient();
		    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
		    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();
		    
		    Document sub_match = new Document();
			sub_match.put("bianhao", device_id);//可以多加几个条件   02003110365
			sub_match.put("time", new Document("$gte",DateTools.StrToDate(start_date)).append("$lte", DateTools.StrToDate(end_date)));
			
			Document sub_group = new Document();
		    sub_group.put("_id", "$Protocol");
		    sub_group.put("count", new Document("$sum", 1));
			
		    JsonA ee= mongoDaoImpl.queryByDocGroup(mongoDataBase, "ct_info", sub_match,sub_group);
		    
		    //记得关闭
		    mongoHelper.closeMongoClient(mongoDataBase, mongoClient);
		    
		    return ee;
		  
	  }
	  
	  
	  public static void main(String args[]){
		  
		  Ct ct=new Ct();
		  
		  JsonO  jj= ct.getErpInfo("ZCKP20180700447","12","0");
		  
		  System.out.println(jj);
		    
	  }

}
