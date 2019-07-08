package com.casit.bootsystem.base;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import java.util.HashMap;
import java.util.Map;

@Path
public class Ap{
	
	//查询所有AP点位信息
  @Post
  public JsonO info(String keys, String limit, String offset)  {
	    JsonO json = new JsonO();
	    String tpl = "";
	    if ((keys != "") && (keys != null) && (keys != "null"))  {
	      tpl = "select  dept.deptnm ,  dept.parents ,  u.*  , br.name as region_name ,  br.mapserver_name as mapserver_name ,br2.id as region_parents_id , br2.name as region_parents_name   from base_ap u   left join base_region br on br.id = u.region_id   left join base_region br2 on br2.id = br.parents    left join sys_dept dept on dept.deptid = u.dept_id    where u.name like '%${keys}%' or u.mac like '%${keys}%' or br.name like '%${keys}%' or br2.name like '%${keys}%'  order by u.id desc";	      
	      tpl = Template.apply(tpl, new Object[] { keys, keys, keys, keys });
	    }
	    else {
	      tpl = "select  dept.deptnm ,  dept.parents , u.*  , br.name as region_name ,  br.mapserver_name as mapserver_name ,br2.id as region_parents_id , br2.name as region_parents_name   from base_ap u   left join base_region br on br.id = u.region_id   left join base_region br2 on br2.id = br.parents   left join sys_dept dept on dept.deptid = u.dept_id   order by u.id desc";
	      tpl = Template.apply(tpl);
	    }
	    Map<String, String> hm = new HashMap();
	    hm.put("start", String.valueOf(Integer.valueOf(offset).intValue() - 1));
	    hm.put("limit", limit);
	    json = DB3.getAutoStore(tpl, "", "", hm, null);
	    JsonA ja = json.getJsonA("root");
	    json.remove("root");
	    json.put("rows", ja);
       return json;
  }
  
// 根据区域编号查询该区域下所有的AP点位
  @Post
  public JsonO info_by_region_id(String keys, String limit, String offset)  {
	    JsonO json = new JsonO();
	    String tpl = "";
	    if ((keys != "") && (keys != null) && (keys != "null"))    {
	      tpl = "select  dept.deptnm ,  dept.parents , u.*  , br.name as region_name ,  br.mapserver_name as mapserver_name ,br2.id as region_parents_id , br2.name as region_parents_name   from base_ap u   left join base_region br on br.id = u.region_id   left join base_region br2 on br2.id = br.parents  left join sys_dept dept on dept.deptid = u.dept_id    where  br.id =  '${keys}'  order by u.id desc";
	      tpl = Template.apply(tpl, new Object[] { keys });
	    }
	    else    {
	      tpl = "select dept.deptnm ,  dept.parents , u.*  , br.name as region_name ,  br.mapserver_name as mapserver_name ,br2.id as region_parents_id , br2.name as region_parents_name   from base_ap u   left join base_region br on br.id = u.region_id   left join base_region br2 on br2.id = br.parents    left join sys_dept dept on dept.deptid = u.dept_id   order by u.id desc";
	      tpl = Template.apply(tpl);
	    }
	    Map<String, String> hm = new HashMap();
	    int cc = Integer.valueOf(offset).intValue();
	    if (cc == 0) {
	      cc = 1;
	    }
	    hm.put("start", String.valueOf(cc - 1));
	    hm.put("limit", limit);
	    json = DB3.getAutoStore(tpl, "", "", hm, null);
	    JsonA ja = json.getJsonA("root");
	    json.remove("root");
	    json.put("rows", ja);
	    return json;
  }
  
  
//根据父区域编号查询该区域下所有的AP点位
  @Post
  public JsonO info_by_region_parentid(String keys, String limit, String offset)  {
	    JsonO json = new JsonO();
	    String tpl = "";
	    if ((keys != "") && (keys != null) && (keys != "null"))    {
	      tpl = "select dept.deptnm ,  dept.parents , u.*  , br.name as region_name ,  br.mapserver_name as mapserver_name ,br2.id as region_parents_id , br2.name as region_parents_name   from base_ap u   left join base_region br on br.id = u.region_id   left join base_region br2 on br2.id = br.parents   left join sys_dept dept on dept.deptid = u.dept_id  where  br2.id =  '${keys}'  order by u.id desc";
	      
	      tpl = Template.apply(tpl, new Object[] { keys });
	    }
	    else    {
	      tpl = "select dept.deptnm ,  dept.parents , u.*  , br.name as region_name ,  br.mapserver_name as mapserver_name ,br2.id as region_parents_id , br2.name as region_parents_name   from base_ap u   left join base_region br on br.id = u.region_id   left join base_region br2 on br2.id = br.parents    left join sys_dept dept on dept.deptid = u.dept_id   order by u.id desc";
	      tpl = Template.apply(tpl);
	    }
	    Map<String, String> hm = new HashMap();
	    hm.put("start", String.valueOf(Integer.valueOf(offset).intValue() - 1));
	    hm.put("limit", limit);
	    json = DB3.getAutoStore(tpl, "", "", hm, null);
	    JsonA ja = json.getJsonA("root");
	    json.remove("root");
	    json.put("rows", ja);
	    return json;
  }
  
  
  //添加AP点位
  @Post
  public String addAp(String jstr)  {
	    JsonO json = new JsonO(jstr);
	    try    {
	      DB3.saveJsonOToDB(json, "base_ap", "id", "region_id,dept_id,x,y");
	      return "success";
	    }
	    catch (Exception e) {}
	    return "fail";
  }
  
  //删除AP点位
  @Post
  public String delAp(String id)  {
	    String retun_str = "fail";
	    try    {
	      DB3.update(Template.apply("delete from base_ap where id=${id}", new Object[] { id }));
	      retun_str = "success";
	    }
	    catch (Exception e)    {
	      retun_str = "fail";
	    }
	    return retun_str;
  }
  
  public static void main(String[] args)  {
    Ap ap = new Ap();
    System.out.println(ap.info_by_region_id("175391001", "10", "1"));
  }
}
