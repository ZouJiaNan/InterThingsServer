package com.casit.bootsystem.mongodb;

import com.casit.bootsystem.util.DateUtil;
import com.casit.json.JsonA;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

public class MainMonHelperImpl
{
  public static void main(String[] args)
  {
	//  System.out.println("dangqian:"+getEndTime().toString());
	test4();
	 
  } 
  
  
  //按时间查询   特定字段
  public static void test1() {
	  
	    MongoHelper mongoHelper = new MongoHelper();
	    MongoClient mongoClient = mongoHelper.getMongoClient();
	    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
	    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();

	    BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("$gte", DateUtil.getStartTime());
	    dbObject.put("$lte", DateUtil.getEndTime());
	    
	  //获取指定字段
	    BasicDBObject key=new BasicDBObject();//指定需要显示列
//	    key.append("low_tidal_volume_alarm_status", true);
//	    key.append("ventilator_system_clock", true);    
//	    key.append("DEVICE_ID", true);    
	    

	    BasicDBObject queryObject = new BasicDBObject("mydate2",dbObject);
	    
	    //再加一个条件
	    queryObject.append("DEVICE_ID", "20140700079");

	    List<Map<String, Integer>>  ee= mongoDaoImpl.queryByDoc(mongoDataBase, "breath_info", queryObject,key);
	    System.out.println( "总共:"+ ee.size());
	    
	    JsonA  ja=new JsonA();
	      for(int i=0;i<ee.size();i=i+2){  
	    	  JSONObject joo = new JSONObject(ee.get(i).toString());
	    	  ja.add(joo);
	      }
	      System.out.println( ja);
	      
  }
  
  
  
  //按时间查询   历史数据  CT
  public static void test2() {
	  
	    MongoHelper mongoHelper = new MongoHelper();
	    MongoClient mongoClient = mongoHelper.getMongoClient();
	    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
	    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();


	    BasicDBObject key=new BasicDBObject();//指定需要显示列

	    
	    BasicDBObject queryObject = new BasicDBObject();

	    queryObject.put("code", "200109109");

	    List<Map<String, Integer>>  ee= mongoDaoImpl.queryByDocByPage(mongoDataBase, "ct_info", queryObject,key,10,1);
	    System.out.println( "总共:"+ ee.size());
	    
	    JsonA  ja=new JsonA();
	      for(int i=0;i<ee.size();i=i+2){  
	    	  JSONObject joo = new JSONObject(ee.get(i).toString());
	    	  ja.add(joo);
	      }
	    System.out.println( ja);
	      
  }  

	 
	 

//按时间查询   特定字段
  public static void test3() {
	  
	    MongoHelper mongoHelper = new MongoHelper();
	    MongoClient mongoClient = mongoHelper.getMongoClient();
	    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
	    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();

	    BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("$gte", DateUtil.parse("yyyy-MM-dd hh:mm:ss", "2019-03-14 14:00"));
	    dbObject.put("$lte",  DateUtil.parse("yyyy-MM-dd hh:mm:ss", "2019-03-14 15:00"));
	    
	  //获取指定字段
	    BasicDBObject key=new BasicDBObject();//指定需要显示列
//	    key.append("low_tidal_volume_alarm_status", true);
//	    key.append("ventilator_system_clock", true);    
//	    key.append("DEVICE_ID", true);    
	    

	    BasicDBObject queryObject = new BasicDBObject("mydate2",dbObject);
	    
	    //再加一个条件
	    queryObject.append("DEVICE_ID", "20140700079");

	    for(int j=0;j<48;j++){
	    	  int ee= mongoDaoImpl.queryByDocNums(mongoDataBase, "breath_info", queryObject,key);
	   	    System.out.println( "总共:"+j+ ee);
	    }
	 
	    
//	    JsonA  ja=new JsonA();
//	      for(int i=0;i<ee.size();i=i++){  
//	    	  JSONObject joo = new JSONObject(ee.get(i).toString());
//	    	  ja.add(joo);
//	      }
//	      System.out.println( ja);
	      
  }
  
  
  //按时间查询一段时间内ct的扫描部位的次数
  
  public static void test4() {
	  
	    MongoHelper mongoHelper = new MongoHelper();
	    MongoClient mongoClient = mongoHelper.getMongoClient();
	    MongoDatabase mongoDataBase = mongoHelper.getMongoDataBase(mongoClient);
	    MongoDaoImpl mongoDaoImpl = new MongoDaoImpl();
	    
	    Document sub_match = new Document();
		sub_match.put("bianhao", "02003110365");//可以多加几个条件
		sub_match.put("time", new Document("$gte", DateUtil.parse("yyyy-MM-dd hh:mm:ss", "2018-02-11 14:00:00")).append("$lte", DateUtil.parse("yyyy-MM-dd hh:mm:ss", "2019-03-11 14:10:00")));
		
		Document sub_group = new Document();
	    sub_group.put("_id", "$Coil");
	    sub_group.put("count", new Document("$sum", 1));
		
	    JsonA ee= mongoDaoImpl.queryByDocGroup(mongoDataBase, "mri_info", sub_match,sub_group);

	      
}  
  
}
