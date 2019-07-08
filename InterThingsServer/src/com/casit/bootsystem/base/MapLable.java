package com.casit.bootsystem.base;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;

@Path
public class MapLable {
	
	// 根据区域编号查询该区域下所有的lable标注
	  @Post
	  public JsonA info_by_region_id(String region_id)  {
		   
		    JsonA json_all = DB3.getResultAsJsonA("select * from base_map_lable where region_id=" + region_id + " order by id", "");
		    
		    return json_all; 
		    
	  }
	  //添加lable标注
	  @Post
	  public String addMapLable(String jstr)  {
		    JsonO json = new JsonO(jstr);
		    try{
		      DB3.saveJsonOToDB(json, "base_map_lable", "id", "region_id");
		      return "success";
		    }
		    catch (Exception e) {}
		    return "fail";
	  }
	  
	  //删除lable标注
	  @Post
	  public String delMapLable(String id)  {
		    String retun_str = "fail";
		    try    {
		      DB3.update(Template.apply("delete from base_map_lable where id=${id}", new Object[] { id }));
		      retun_str = "success";
		    }
		    catch (Exception e)    {
		      retun_str = "fail";
		    }
		    return retun_str;
	  }
}
