package com.casit.bootsystem.business;

import java.util.ArrayList;
import java.util.Date;

import com.casit.bootsystem.config.SysConfig;
import com.casit.bootsystem.util.DateTools;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;



//调配

@Path
public class Allocate {
	
	public static long shicha=SysConfig.REAL_SPACE_MIL_TIME;//SysConfig
	
	  //根据区域编号自动筛选设备推荐
	  @Post
	  public JsonO getAllocateInfo(String region_id,String num,String  device_type,String device_name,String deptid)  {
		  
		  JsonO jo=new JsonO();
		  JsonA ja_new =new JsonA();
		  
		  String[] device_types=device_type.split(",");
		  String device_type_name="";
		  if(device_types.length>1){
			  //查询所有
		  }
		  else{
			  device_type_name=device_types[0];
		  }
		  
		  
		  //如果没有region_id    查询deptid所属的区域id
		  
		    
		  
		  
		  
		  //得到属于那个住院大楼
		  String tpl = "select  id,name,parents,mapserver_name ,sortno  ,tel from  base_region where  id=" + region_id + "   ORDER BY sortno ASC ";
		  JsonA ja = DB3.getResultAsJsonA(Template.apply(tpl), "state");
		  
		  System.out.println("当前楼层："+ja);
		  //取得大楼的id
		  String parents_id="";
		  if(ja.size()>0){
			  parents_id=  ja.getJsonO(0).getString("parents");
			  
			  String sortno=ja.getJsonO(0).getString("sortno");
		 
			  
			   ArrayList   sou_arr= paixu(sortno);
			  
			  for(int i=0;i<sou_arr.size();i++){
				  String sortno_one=sou_arr.get(i).toString();
				  queryEquByid(sortno_one,ja_new);
				  
			  }
	           
		  }
		  //System.out.println(ja_new.size());
		  
		  //过滤设备只保留12个
		  int num_s=12;
		  if(num==null||"".equals(num)){
			  num_s=12;
		  }
		  else{
			  num_s=Integer.valueOf(num);
		  }

		  JsonA ja_new_new=new JsonA();
		  System.out.println(ja_new.size());
		  if(ja_new.size()>num_s){
			  for(int i=0;i<num_s;i++){
				  //ja_new.remove(i);
				  ja_new_new.add(ja_new.getObject(i));
				
			  }
			  
		  }
		  
		  else{
			  for(int i=0;i<ja_new.size();i++){
				  //ja_new.remove(i);
				  ja_new_new.add(ja_new.getObject(i));
				
			  }
		  }
		  
		  jo.put("result", ja_new_new);
		
		  
		  return jo;
		  
	  }
	  
	  
	  //根据楼层序号进行排序  "sortno":"1-05"
	  
	  public static ArrayList paixu(String sortno){
		  
		  String[]  sss= sortno.split("-");
		  ArrayList new_arr_end=new ArrayList();
		  
		  if(sss.length>1){
			  
			  String qianzui=sss[0];
			  
			  int xuhao=Integer.valueOf(sss[1]);
			  int max_int=16;//最大楼层
			  
			  ArrayList  xia_arr=new ArrayList();
			  ArrayList up_arr=new ArrayList();
			  for(int i=xuhao;i>0;i--){
				 
				  xia_arr.add(i);
			  }
			  
			  for(int i=xuhao+1;i<max_int;i++){
					 
				  up_arr.add(i);
			  }
			  //System.out.println(xia_arr);
			  //System.out.println(up_arr);
			  
			  //从新排序
			  ArrayList new_arr=new ArrayList();
			  
			  if(xia_arr.size()<up_arr.size()){
				  
				  for(int x=0;x<xia_arr.size();x++){
					  new_arr.add(xia_arr.get(x));
					  new_arr.add(up_arr.get(x));
				  }
				  //然后加入剩下的
				  for(int y=xia_arr.size();y<up_arr.size();y++){
					  new_arr.add(up_arr.get(y));
				  }
			  }
			  
	         if(xia_arr.size()>up_arr.size()){
				  
				  for(int x=0;x<up_arr.size();x++){
					  new_arr.add(xia_arr.get(x));
					  new_arr.add(up_arr.get(x));
				  }
				  //然后加入剩下的
				  for(int y=up_arr.size();y<xia_arr.size();y++){
					  new_arr.add(xia_arr.get(y));
				  }
			  }
	  
	  
			 // System.out.println(new_arr);
			  
			  //上面只是排了当前住院大楼
			  //下面将排序其他住院大楼
			  int[]  dalou=new int[]{1,3};  //有设备的大楼
			  
			  
			  
			  
			  
			  //加入前缀
			  for(int j=0;j<new_arr.size();j++){
				  
				  if((Integer)new_arr.get(j)<10){
					  new_arr_end.add(qianzui+"-0"+new_arr.get(j));
				  }
				  else{
					  new_arr_end.add(qianzui+"-"+new_arr.get(j));
				  }				  
			  }
			  
			  //再搜索其他住院大楼
			  
			  for(int y=0;y<dalou.length;y++){
				  
				  if(!qianzui.equals(String.valueOf(dalou[y]))){
					  
					  
					  for(int i=1;i<max_int;i++){
							 
						  if(i<10){
							  new_arr_end.add(dalou[y]+"-0"+i);
						  }
						  else{
							  new_arr_end.add(dalou[y]+"-"+i);
						  }	
					  }
					  
					  
				  }
				  
			  }
			  			  
			  
			  //过滤掉一些楼层，因为有些楼层是没有设备的
			  
			  String[] guolv=new String[]{"1-01","1-03","1-04","1-07","1-08","1-09","1-12","1-14","1-15","3-01","3-02","3-03","3-04","3-05","3-06","3-07","3-08","3-09","3-13","3-14","3-15"};
			  
			  
			  for(int j=0;j<new_arr_end.size();j++){
				  
				String ssss=  (String) new_arr_end.get(j);
				
				  for(int i=0;i<guolv.length;i++){
					  
					  if(guolv[i].equals(ssss)){
						  //移除
						  new_arr_end.remove(j);
					  }
					  
				  }
			  }
			  
			  
			  
		  }
		  
//		  System.out.println(new_arr_end);
		  return new_arr_end;
	  }
	  
	  
	  //根据当前楼层的区域id查询备用设备的信息
	  
	  public static JsonA  queryEquByid(String sortno, JsonA ja_new){
		  
		  
		//得到一个楼层的当前所有 的设备
          String tp2 = "SELECT  br.tel, br.id as region_id, br.sortno,  dept.deptid , dept.deptnm as dept_name ,   dept.parents , br.name, region2.name as region_father_name , r.DEVICE_ID ,r.DEVICE_TYPE ,r.MAC,r.INSERT_DATE  ,bs.name as run_state ,r.USEING_STATU  FROM real_data r   " +
          		"" +
          		"" +
          		" left join base_state bs on bs.value = r.USEING_STATU    left join base_ap ap on ap.mac = r.MAC   left join base_region br on br.id = ap.region_id    left join base_region region2 on region2.id = br.parents     left join sys_dept dept on dept.deptid = ap.dept_id   " +
          		"" +
          		"" +
          		"" +
          		"  WHERE  br.sortno='" + sortno + "'    Group By DEVICE_ID;";
          
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
            
            //如果时间差大于   则是备用状态
	            if (shijiancha > shicha) {
	            	yigeshebei.putQuoted("run_state", "备用");
	            	// System.out.println(yigeshebei);
	            	//a按楼层的序号（） 进行排序  比如108
	            	//先推荐本楼层的设备		                 
	                ja_new.add(yigeshebei);
	            	
	            }     
          }
          return ja_new;
		  
	  }
	  
	  
	  
	  //查询一个部门是属于哪个区域
	  
	  
	  
	  
	  
	  
	  public static void main(String args[]){
		  
		  Allocate   ao=new Allocate();
		 // JsonO jo=  ao.getAllocateInfo("175391001","12");
		  //System.out.println(jo);
//		  
//		  String sss="01";
//		  
//		  System.out.println(Integer.valueOf(sss));
		  
		  
		  String device_type="huang,222";
		  
		  String[] device_types=device_type.split(",");
		  String device_type_name="";
		  if(device_types.length>1){
			  //查询所有
		  }
		  else{
			  device_type_name=device_types[0];
		  }
		  System.out.println(device_type_name);
		  
		// paixu("3-11");
	  }
	
	
	
	

}
