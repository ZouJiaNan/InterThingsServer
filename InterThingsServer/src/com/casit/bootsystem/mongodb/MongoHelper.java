package com.casit.bootsystem.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoHelper
{
  static final String DBName = "huaxi_mongodb";
  static final String ServerAddress = "127.0.0.1";
  static final int PORT = 27017;
  
  public MongoClient getMongoClient()
  {
    MongoClient mongoClient = null;
    try
    {
      mongoClient = new MongoClient("172.21.3.113", 27017);
      System.out.println("Connect to mongodb successfully");
    }
    catch (Exception e)
    {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return mongoClient;
  }
  
  public MongoDatabase getMongoDataBase(MongoClient mongoClient)
  {
    MongoDatabase mongoDataBase = null;
    try
    {
      if (mongoClient != null)
      {
        mongoDataBase = mongoClient.getDatabase("huaxi_mongodb");
        System.out.println("Connect to DataBase successfully");
      }
      else
      {
        throw new RuntimeException("MongoClient不能够为空");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return mongoDataBase;
  }
  
  public void closeMongoClient(MongoDatabase mongoDataBase, MongoClient mongoClient)
  {
    if (mongoDataBase != null) {
      mongoDataBase = null;
    }
    if (mongoClient != null) {
      mongoClient.close();
    }
    System.out.println("CloseMongoClient successfully");
  }
}
