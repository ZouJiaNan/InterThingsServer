package com.casit.bootsystem.mongodb;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.json.JSONObject;

public class MongoDaoImpl
  implements MongoDao
{
  @Override
public Map<String, Integer> queryByID(MongoDatabase db, String table, Object Id)
    throws Exception
  {
    MongoCollection<Document> collection = db.getCollection(table);
    BasicDBObject query = new BasicDBObject("_id", Id);
    
    FindIterable<Document> iterable = collection.find(query);
    
    Map<String, Integer> jsonStrToMap = null;
    MongoCursor<Document> cursor = iterable.iterator();
    while (cursor.hasNext())
    {
      Document user = cursor.next();
      String jsonString = user.toJson();
      jsonStrToMap = JsonStrToMap.jsonStrToMap(jsonString);
    }
    System.out.println("检索ID完毕");
    
    return jsonStrToMap;
  }
  //一般条件查询
  public List<Map<String, Integer>> queryByDoc(MongoDatabase db, String table, BasicDBObject doc, BasicDBObject doc2)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    FindIterable<Document> iterable = collection.find(doc).projection(doc2);
    
    List<Map<String, Integer>> list = new ArrayList();
    MongoCursor<Document> cursor = iterable.iterator();
    while (cursor.hasNext())
    {
      Document user = cursor.next();
      String jsonString = user.toJson();
      Map<String, Integer> jsonStrToMap = JsonStrToMap.jsonStrToMap(jsonString);
      list.add(jsonStrToMap);
    }
    System.out.println("检索doc完毕");
    return list;
  }
  
  //分组查询
  public JsonA  queryByDocGroup(MongoDatabase db, String table, Document sub_match,  Document sub_group)  {
	  
    MongoCollection<Document> collection = db.getCollection(table);
    
    //第一个条件
    //Document sub_match = new Document();
	//sub_match.put("appId", app_id);//可以多加几个条件
	//sub_match.put("leaveTime", new Document("$gt", beginDate).append("$lt", endDate));

    //第二个条件
    //  Document sub_group = new Document();
    //	sub_group.put("_id", "$Protocol");
    //	sub_group.put("count", new Document("$sum", 1));
    Document group2 = new Document("$group", sub_group);
	//条件查询
	Document match = new Document("$match", sub_match);
	//排序
	//Document sort = new Document("$sort", new Document("_id", 1));
	
	List<Document> aggregateList = new ArrayList<Document>();
	aggregateList.add(match);
	aggregateList.add(group2); 
	
	//再加入时间

	AggregateIterable<Document> resultset = collection.aggregate(aggregateList);
	MongoCursor<Document> cursor = resultset.iterator();
	JsonA ja=new JsonA();
	try {
		while(cursor.hasNext()) {
			Document item_doc = cursor.next();
			//int leaveMethod = item_doc.getInteger("_id", 0);
			int count = item_doc.getInteger("count", 0);
			//item_doc.getString("_id");
			JsonO jobj=new JsonO();
			jobj.putQuoted("static_name", item_doc.getString("_id").trim());
			jobj.putUnQuoted("count", item_doc.getInteger("count").toString());
			ja.add(jobj);
			
		}
		
		System.out.println(ja);
	} finally {
		cursor.close();
	}

	return ja;
  }
  
  
  public int queryByDocNums(MongoDatabase db, String table, BasicDBObject doc, BasicDBObject doc2)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    FindIterable<Document> iterable = collection.find(doc);
    
   int i=0;
    MongoCursor<Document> cursor = iterable.iterator();
    while (cursor.hasNext())
    {
      cursor.next();
      i=i+1;
    }
   
    return i;
  }
  
  //分页查询数据
  
  public List<Map<String, Integer>> queryByDocByPage(MongoDatabase db, String table, BasicDBObject doc, BasicDBObject doc2,int limit,int offset)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    int iii=offset*limit;
    FindIterable<Document> iterable = collection.find(doc).projection(doc2).skip(iii).limit(limit);
    System.out.println("****"+offset*limit);
    List<Map<String, Integer>> list = new ArrayList();
    MongoCursor<Document> cursor = iterable.iterator();
    while (cursor.hasNext())
    {
      Document user = cursor.next();
      String jsonString = user.toJson();
      Map<String, Integer> jsonStrToMap = JsonStrToMap.jsonStrToMap(jsonString);
      list.add(jsonStrToMap);
    }
   // System.out.println(list);
    return list;
  }
  
  //查询总数
  public long queryByDocNum(MongoDatabase db, String table, BasicDBObject doc, BasicDBObject doc2)
  {
    MongoCollection<Document> collection = db.getCollection(table);
 
//    MongoCursor<Document> cursor = iterable.iterator();
//    int i=0;
//    while (cursor.hasNext())
//    {
//    	i=i+1;
//    }
  
  

    //doc.put("count", new Document("$bianhao", 1));
    
    FindIterable<Document> iterable = collection.find(doc);
    
    MongoCursor<Document> cursor = iterable.iterator();
    int i=0;
    while (cursor.hasNext())
    {
    	 Document user = cursor.next();
    	 //System.out.println(user);
    	 i=i+1;
    }
    
    long zongsu=collection.count();
    
    return i;
  }

  
  
  public List<Map<String, Integer>> queryAll(MongoDatabase db, String table)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    FindIterable<Document> iterable = collection.find();
    
    List<Map<String, Integer>> list = new ArrayList();
    MongoCursor<Document> cursor = iterable.iterator();
    while (cursor.hasNext())
    {
      Document user = cursor.next();
      String jsonString = user.toJson();
      Map<String, Integer> jsonStrToMap = JsonStrToMap.jsonStrToMap(jsonString);
      list.add(jsonStrToMap);
    }
    System.out.println("检索全部完毕");
    return list;
  }
  
  public void printFindIterable(FindIterable<Document> iterable)
  {
    MongoCursor<Document> cursor = iterable.iterator();
    while (cursor.hasNext())
    {
      Document user = cursor.next();
      System.out.println(user.toJson());
    }
    cursor.close();
  }
  
  @Override
public boolean insert(MongoDatabase db, String table, Document document)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    collection.insertOne(document);
    long count = collection.count(document);
    
    System.out.println("count: " + count);
    if (count == 1L)
    {
      System.out.println("文档插入成功");
      return true;
    }
    System.out.println("文档插入成功");
    return false;
  }
  
  public boolean insertMany(MongoDatabase db, String table, List<Document> documents)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    long preCount = collection.count();
    collection.insertMany(documents);
    long nowCount = collection.count();
    System.out.println("插入的数量: " + (nowCount - preCount));
    if (nowCount - preCount == documents.size())
    {
      System.out.println("文档插入多个成功");
      return true;
    }
    System.out.println("文档插入多个失败");
    return false;
  }
  
  @Override
public boolean delete(MongoDatabase db, String table, BasicDBObject document)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    DeleteResult deleteManyResult = collection.deleteMany(document);
    long deletedCount = deleteManyResult.getDeletedCount();
    System.out.println("删除的数量: " + deletedCount);
    if (deletedCount > 0L)
    {
      System.out.println("文档删除多个成功");
      return true;
    }
    System.out.println("文档删除多个失败");
    return false;
  }
  
  public boolean deleteOne(MongoDatabase db, String table, BasicDBObject document)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    DeleteResult deleteOneResult = collection.deleteOne(document);
    long deletedCount = deleteOneResult.getDeletedCount();
    System.out.println("删除的数量: " + deletedCount);
    if (deletedCount == 1L)
    {
      System.out.println("文档删除一个成功");
      return true;
    }
    System.out.println("文档删除一个失败");
    return false;
  }
  
  @Override
public boolean update(MongoDatabase db, String table, BasicDBObject whereDoc, BasicDBObject updateDoc)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    UpdateResult updateManyResult = collection.updateMany(whereDoc, new Document("$set", updateDoc));
    long modifiedCount = updateManyResult.getModifiedCount();
    System.out.println("修改的数量: " + modifiedCount);
    if (modifiedCount > 0L)
    {
      System.out.println("文档更新多个成功");
      return true;
    }
    System.out.println("文档更新失败");
    return false;
  }
  
  public boolean updateOne(MongoDatabase db, String table, BasicDBObject whereDoc, BasicDBObject updateDoc)
  {
    MongoCollection<Document> collection = db.getCollection(table);
    UpdateResult updateOneResult = collection.updateOne(whereDoc, new Document("$set", updateDoc));
    long modifiedCount = updateOneResult.getModifiedCount();
    System.out.println("修改的数量: " + modifiedCount);
    if (modifiedCount == 1L)
    {
      System.out.println("文档更新一个成功");
      return true;
    }
    System.out.println("文档更新失败");
    return false;
  }
  
  public void createCol(MongoDatabase db, String table)
  {
    db.createCollection(table);
    System.out.println("集合创建成功");
  }
  
  public void dropCol(MongoDatabase db, String table)
  {
    db.getCollection(table).drop();
    System.out.println("集合删除成功");
  }
  
  




}
