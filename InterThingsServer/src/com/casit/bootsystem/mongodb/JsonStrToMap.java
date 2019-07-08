package com.casit.bootsystem.mongodb;

import com.mongodb.util.JSON;
import java.util.HashMap;
import java.util.Map;

public class JsonStrToMap
{
  public static Map<String, Integer> jsonStrToMap(String jsonString)
  {
    Object parseObj = JSON.parse(jsonString);
    Map map = (HashMap)parseObj;
    return map; }

  public static Map<String, Object> jsonStrToMap2(String jsonString) {
    Object parseObj = JSON.parse(jsonString);
    Map map = (HashMap)parseObj;
    return map;
  }
}