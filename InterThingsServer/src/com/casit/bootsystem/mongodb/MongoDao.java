package com.casit.bootsystem.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import java.util.Map;
import org.bson.Document;

public abstract interface MongoDao
{
  public abstract Map<String, Integer> queryByID(MongoDatabase paramMongoDatabase, String paramString, Object paramObject)
    throws Exception;

  public abstract boolean insert(MongoDatabase paramMongoDatabase, String paramString, Document paramDocument);

  public abstract boolean delete(MongoDatabase paramMongoDatabase, String paramString, BasicDBObject paramBasicDBObject);

  public abstract boolean update(MongoDatabase paramMongoDatabase, String paramString, BasicDBObject paramBasicDBObject1, BasicDBObject paramBasicDBObject2);
}