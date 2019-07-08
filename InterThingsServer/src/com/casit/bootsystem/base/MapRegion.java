package com.casit.bootsystem.base;

import com.casit.bootsystem.config.SysConfig;
import com.casit.bootsystem.sys.Log;
import com.casit.bootsystem.util.BootSystemTools;
import com.casit.bootsystem.util.DateTools;
import com.casit.bootsystem.util.MultipartRequest;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.fileupload.FileItem;
import org.json.JSONArray;

@Path
public class MapRegion

 
{
	
	public static long shicha=SysConfig.REAL_SPACE_MIL_TIME;//SysConfig
	
	
  @Post
  public String up(HttpServletRequest request, HttpServletResponse response)
  {
    String re_name = "区域上传保存成功!";
    String timestr = Log.getDT("0");
    
    String path = request.getSession().getServletContext().getRealPath("/upload_pic");
    try
    {
      MultipartRequest mRequest = new MultipartRequest(request);
      String quy_name = mRequest.getString("quyu_name");
      String parents_id = mRequest.getString("parents_id");
      String personnel_deptid = mRequest.getString("personnel_deptid");
      String mapserver_id = mRequest.getString("mapserver_id");
      String coordinates = mRequest.getString("coordinates");
      String mapserver_name = mRequest.getString("mapserver_name");
      String zhuangtai = mRequest.getString("zhuangtai");
      String pics_str = "";
      
      FileItem[] fs = null;
      int result = 0;
      fs = mRequest.getFiles();
      
      JsonO fileJsonO = new JsonO();
      if (fs.length > 0)
      {
        FileItem[] arrayOfFileItem1;
        int j = (arrayOfFileItem1 = fs).length;
        for (int i = 0; i < j; i++)
        {
          FileItem f = arrayOfFileItem1[i];
          if (f.getSize() >= 0L)
          {
            String filesavename = UUID.randomUUID().toString();
            String filename = f.getName();
            if ((filename != null) && (filename.length() > 0)) {
              filename = filename.substring(filename.lastIndexOf(File.separatorChar) + 1);
            }
            try
            {
              if (f != null)
              {
                String extension = BootSystemTools.getFileExtention(filename);
                File saveImage = new File(path, filesavename + extension);
                f.write(saveImage);
                if ((extension.equalsIgnoreCase(".jpg")) || (extension.equalsIgnoreCase(".jpeg")) || 
                  (extension.equalsIgnoreCase(".png")) || (extension.equalsIgnoreCase(".gif")) || (extension.equalsIgnoreCase(".bmp")))
                {
                  String randomThumb = UUID.randomUUID().toString();
                  String thumb = "thumb_" + randomThumb;
                  String thunmPath = path.replace("\\", "/") + "/" + thumb + extension;
                  Thumbnails.of(new File[] { saveImage })
                    .size(200, 200)
                    .toFile(new File(thunmPath));
                }
                String filesize = Long.toString(f.getSize());
                
                pics_str = filesavename + extension;
              }
            }
            catch (Exception e1)
            {
              re_name = "图片没上传!";
            }
          }
        }
      }
      System.out.println("保存到数据库");
      fileJsonO.putQuoted("name", quy_name);
      if ("".equals(parents_id)) {
        fileJsonO.putUnQuoted("parents", null);
      } else {
        fileJsonO.putUnQuoted("parents", parents_id);
      }
      fileJsonO.putUnQuoted("id", personnel_deptid);
      fileJsonO.putUnQuoted("mapserver_id", mapserver_id);
      fileJsonO.putQuoted("coordinates", coordinates);
      fileJsonO.putQuoted("mapserver_name", mapserver_name);
      fileJsonO.putUnQuoted("state", zhuangtai);
      if (!"".equals(pics_str)) {
        fileJsonO.putQuoted("pics", pics_str);
      }
      if ((personnel_deptid != null) && (!"".equals(personnel_deptid)))
      {
        fileJsonO.putUnQuoted("id", personnel_deptid);
        re_name = "修改成功！";
      }
      DB3.saveJsonOToDB(fileJsonO, "base_region", "id", "");
      
      System.out.println("vvvvv:" + fs.length);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    String re = "<script> parent.reloadTree();history.go(-1);</script> <div  style='text-align: center;padding:20px'> " + re_name + "</div>" + 
      "<div style='text-align: center'> " + 
      
      "<a href='../jsp/system/db/base_region_form.html?d='" + timestr + " class='btn btn-xs btn-primary'><i class='fa fa-pencil'></i> 返回继续添加</a>";
    
    return re;
  }
  
  @Post
  public JsonA file_region()
  {
    String tpl = "select id,name as text,parents ,coordinates,mapserver_id,mapserver_name,pics ,sortno ,tel from base_region order by sortno";
    return DB3.getResultAsJsonA(Template.apply(tpl), "");
  }
  
  @Post
  public JSONArray outdoor_region()
  {
    String tpl = "select * from  base_region where parents in (SELECT id FROM base_region where parents is  null )  ORDER BY sortno ASC ";
    
    JsonA ja = DB3.getResultAsJsonA(Template.apply(tpl), "state");
    for (int i = 0; i < ja.size(); i++)
    {
      JsonO re_jo = ja.getJsonO(i);
      int id = re_jo.getInt("id");
      JsonA child_nones = DB3.getResultAsJsonA("select * from base_region where parents=" + id + " order by id", "");
      ArrayList layerIDArr = new ArrayList();
      ArrayList layernames = new ArrayList();
      for (int j = 0; j < child_nones.size(); j++)
      {
        JsonO none_o = child_nones.getJsonO(j);
        layerIDArr.add(none_o.getString("mapserver_id"));
        layernames.add(none_o.getString("name"));
      }
      re_jo.putUnQuoted("layerIDArr", layerIDArr.toString());
      re_jo.putUnQuoted("room", layernames.toString());
      JSONArray JO = new JSONArray(re_jo.getString("coordinates"));
      re_jo.putUnQuoted("x", String.valueOf(JO.getDouble(0)));
      re_jo.putUnQuoted("y", String.valueOf(JO.getDouble(1)));
    }
    JSONArray json = new JSONArray(ja.toString());
    System.out.println(json);
    return json;
  }
  
  @Post
  public JsonO reload_macinfo_real(String quyu_code)  {
	  
	//查询有多少个楼
    String tpl = "select  id,name,parents,mapserver_name from  base_region where parents is  null  ORDER BY sortno ASC ";
    JsonA ja = DB3.getResultAsJsonA(Template.apply(tpl), "state");
    if (quyu_code == null) {
      //如果为空设置默认为1住
      quyu_code = ja.getJsonO(0).getString("id");
    }    
    //是否为1级菜单
    boolean is_father = false;
    for (int x = 0; x < ja.size(); x++){
      JsonO re_jo_x = ja.getJsonO(x);
      if (quyu_code.equals(re_jo_x.getString("id"))) {
        is_father = true;
      }
    }
    if (is_father) {
    	
      for (int i = 0; i < ja.size(); i++){
    	  
    	//得到一个住院大楼
        JsonO re_jo = ja.getJsonO(i);
        int id = re_jo.getInt("id");
        
        //统计每个大楼的设备数量
        
        
        //统计每个大楼的设备数量
        JsonA num_total = DB3.getResultAsJsonA("SELECT COUNT(*) as num  FROM real_data  r    left join base_ap ap on ap.mac = r.MAC  left join base_region br on br.id = ap.region_id WHERE br.parents=" + id , "");
        
        if(num_total.size()>0){        	
        	JsonO mama_obj=num_total.getJsonO(0);
        	if(!"0".equals(mama_obj.getString("num"))){
        		re_jo.putUnQuoted("nums", mama_obj.getString("num"));
        	}
        	
        }
      
        
        
        if (quyu_code.equals(re_jo.getString("id"))){
        	
           //查询1住有多少楼层
          JsonA child_nones = DB3.getResultAsJsonA("select id,name,parents,mapserver_name from base_region where parents=" + id + " order by sortno", "");
          
          int bei_yong_num_all=0;
          int dai_ji_num_all=0;
          int guan_ji_num_all=0;
          int yun_xing_num_all=0;
          int pb840_num_all=0;
          int v60_num_all=0;
          
          
          
          
          for (int j = 0; j < child_nones.size(); j++)  {
        	  
        	  //得到一个楼层
            JsonO none_o = child_nones.getJsonO(j);
            
            String region_id = none_o.getString("id");
            
            
     
            
            //得到一个楼层的当前所有 的设备
            String tp2 = "SELECT r.DEVICE_ID ,r.DEVICE_TYPE ,r.MAC,r.INSERT_DATE  ,bs.name as run_state ,r.USEING_STATU   FROM real_data r    left join base_state bs on bs.value = r.USEING_STATU    left join base_ap ap on ap.mac = r.MAC   left join base_region br on br.id = ap.region_id    WHERE  br.id='" + region_id + "'    Group By DEVICE_ID;";
            
            String tp3 = Template.apply(tp2);
            JsonA json = DB3.getResultAsJsonA(tp3, "");
            
            
          
            int bei_yong=0;
            int dai_ji=0;
            int guan_ji=0;
            int yun_xing=0;
            int pb840_num=0;
            int v60_num=0;
            int sv300_num=0;
            
            
            
            
            for (int x = 0; x < json.size(); x++){
            	
              //得到一个设备盒子
              JsonO yigeshebei = json.getJsonO(x);
              //////////////////////////
              String insert_date = yigeshebei.getString("insert_date");
              Date insert_date2 = DateTools.StrToDate(insert_date);
              Date now_date = new Date();
              long shijiancha = now_date.getTime() - insert_date2.getTime();
              
              //统计类型
              if("PB840".equals(yigeshebei.getString("device_type"))){//
            	  pb840_num=pb840_num+1;
        	  }
              if("V60".equals(yigeshebei.getString("device_type"))){//
            	  v60_num=v60_num+1;
        	  }
              if("SV300".equals(yigeshebei.getString("device_type"))){//
            	  sv300_num=sv300_num+1;
        	  }
              
              
              //统计运行状态数量
              if (shijiancha < shicha) {
            	  
            	  if("1".equals(yigeshebei.getString("useing_statu"))){//运行
            		  yun_xing=yun_xing+1;
            	  }
                  if("2".equals(yigeshebei.getString("useing_statu"))){//待机
                	  dai_ji=dai_ji+1;
            	  }
                  if("3".equals(yigeshebei.getString("useing_statu"))){//关机
                	  guan_ji=guan_ji+1;
            	  }
                 
            	  
            	  
              } else {
            	  yigeshebei.remove("run_state");
            	  yigeshebei.putQuoted("run_state", "备用");
            	  bei_yong=bei_yong+1;//备用
              }
              
            }
            none_o.put("shebei_arr", json);//当前所有 的设备
            none_o.putUnQuoted("bei_yong_num", String.valueOf(bei_yong));
            none_o.putUnQuoted("dai_ji_num", String.valueOf(dai_ji));
            none_o.putUnQuoted("guan_ji_num", String.valueOf(guan_ji));
            none_o.putUnQuoted("yun_xing_num", String.valueOf(yun_xing));
            
            none_o.putUnQuoted("pb840_num", String.valueOf(pb840_num));
            none_o.putUnQuoted("v60_num", String.valueOf(v60_num));
            none_o.putUnQuoted("sv300_num", String.valueOf(sv300_num));
            
            
             bei_yong_num_all=bei_yong_num_all+bei_yong;
             dai_ji_num_all=dai_ji_num_all+dai_ji;
             guan_ji_num_all=guan_ji_num_all+guan_ji;
             yun_xing_num_all=yun_xing_num_all+yun_xing;
             pb840_num_all=pb840_num_all+pb840_num;
             v60_num_all=v60_num_all+v60_num;
            
            
            
            
          }
          re_jo.put("louceng_arr", child_nones);
        		  
          //再把数据汇总一下统计到一个大楼中
          re_jo.putUnQuoted("bei_yong_num_all", String.valueOf(bei_yong_num_all));
          re_jo.putUnQuoted("dai_ji_num_all", String.valueOf(dai_ji_num_all));
          re_jo.putUnQuoted("guan_ji_num_all", String.valueOf(guan_ji_num_all));
          re_jo.putUnQuoted("yun_xing_num_all", String.valueOf(yun_xing_num_all));
          re_jo.putUnQuoted("pb840_num_all", String.valueOf(pb840_num_all));
          re_jo.putUnQuoted("v60_num_all", String.valueOf(v60_num_all));
          
        }
      }
    }
    else    {
    	
      JsonA child_nones_louceng = DB3.getResultAsJsonA("select  id,name,parents,mapserver_name from base_region where id=" + quyu_code + " order by sortno", "");
      for (int j = 0; j < child_nones_louceng.size(); j++)
      {
        JsonO none_o = child_nones_louceng.getJsonO(j);
        
        String region_id = none_o.getString("id");
        
        String tp2 = "SELECT r.DEVICE_ID ,r.DEVICE_TYPE ,r.MAC ,r.INSERT_DATE ,bs.name as run_state  FROM real_data r   left join base_state bs on bs.value = r.USEING_STATU  left join base_ap ap on ap.mac = r.MAC    left join base_region br on br.id = ap.region_id     WHERE  br.id='" + region_id + "'    Group By DEVICE_ID;";
        
        String tp3 = Template.apply(tp2);
        JsonA json = DB3.getResultAsJsonA(tp3, "");
        
        
        for (int x = 0; x < json.size(); x++){
        	
            //得到一个设备盒子
            JsonO yigeshebei = json.getJsonO(x);
            //////////////////////////
            String insert_date = yigeshebei.getString("insert_date");
            Date insert_date2 = DateTools.StrToDate(insert_date);
            Date now_date = new Date();
            long shijiancha = now_date.getTime() - insert_date2.getTime();
            if (shijiancha < shicha) {
             
            } else {
            	yigeshebei.remove("run_state");
            	yigeshebei.putQuoted("run_state", "备用");
            }
            
          }
        
        
        none_o.put("shebei_arr", json);
      }
      ja = child_nones_louceng;
    }
    
    JsonO obj = new JsonO();
    JSONArray json = new JSONArray(ja.toString());
    if (!is_father)
    {
     
      obj.put("region", json);
      
    }
    else
    {
      obj.put("region", json);
      
    }
    

    
    
    return obj;
  }
  
  
  //根据AP来查询渲染
  @Post
  public JsonO query_ap_equi(String quyu_code)
  {
    JsonO json = new JsonO();
    //查询这个区域有多少AP
    JsonA ap_arr = DB3.getResultAsJsonA("select  ap.* ,br.name as floor_name ,br2.name as build_name from base_ap ap    left join base_region br on br.id = ap.region_id   left join base_region br2 on br2.id = br.parents where region_id=" + quyu_code + " order by id", "");
    

    
    //查询这个区域中所有的设备
    String tp5 = "SELECT r.DEVICE_ID ,r.MAC ,r.DEVICE_TYPE ,r.INQUIRE_TIME  , r.CONNECT_TYPE , r.CONNECT_STATU , r.INSERT_DATE  , r.Y_RUN  , r.OPEN ,bs.name as run_state   FROM real_data r   left join base_state bs on bs.value = r.USEING_STATU    left join base_ap ap on ap.mac = r.MAC    left join base_region br on br.id = ap.region_id       WHERE br.id='" + quyu_code + "'  ;";
    String tp6 = Template.apply(tp5);
    JsonA equipment_arr_bushu = DB3.getResultAsJsonA(tp6, "");
    
    //循环所有的AP得到ap中的设备情况
    for (int j = 0; j < ap_arr.size(); j++) {
    	
      JsonO ap_o = ap_arr.getJsonO(j);
      JsonA equipments = new JsonA();
      for (int y = 0; y < equipment_arr_bushu.size(); y++)  {
    	  
        JsonO equipment_o = equipment_arr_bushu.getJsonO(y);
        
        
        //////////////无论如何计算设备的状态/////////////////////////////////////////////
        String insert_date = equipment_o.getString("insert_date");
        Date insert_date2 = DateTools.StrToDate(insert_date);
        Date now_date = new Date();
        long shijiancha = now_date.getTime() - insert_date2.getTime();
        if (shijiancha < shicha) {
         
        } else {
      	  equipment_o.remove("run_state");
      	  equipment_o.putQuoted("run_state", "备用");
        }
        
        //////////////////////////////////////////////////////////
        
        //匹配了mac才能把设备加入AP中
        if (equipment_o.getString("mac").equals(ap_o.getString("mac")))   {  
          
          equipments.add(equipment_o);
        }
      }
      ap_o.put("equipments_in_ap", equipments);
    }
    json.put("ap_arr", ap_arr);
    json.put("equipments_arr", equipment_arr_bushu);
    
    return json;
  }
  
  public static void main(String[] args)
  {
  MapRegion  mr=new MapRegion();
	  
	  JsonO  jj=  mr.reload_macinfo_real("3");
	  System.out.println(jj);
  }
  
  @Post
  public JsonA indoor_region_byid(String id)
  {
    String tpl = "select * from base_region where parents=" + id + " order by id";
    return DB3.getResultAsJsonA(Template.apply(tpl), "");
  }
  
  @Post
  public String delNode(String node)
  {
    String retun_str = "fail";
    try
    {
      JsonA child_nones = DB3.getResultAsJsonA("select * from base_region where parents=" + node + " order by id", "");
      if (child_nones.size() == 0)
      {
        DB3.update(Template.apply("delete from base_region where id=${node}", new Object[] { node }));
        retun_str = "success";
      }
    }
    catch (Exception e)
    {
      retun_str = "fail";
    }
    return retun_str;
  }
  
  @Post
  public String addNode(String jstr)
  {
    JsonO json = new JsonO(jstr);
    try
    {
      DB3.saveJsonOToDB(json, "base_region", "id", "parents");
      return "success";
    }
    catch (Exception e) {}
    return "fail";
  }
  
  
 
}
