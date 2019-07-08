package com.casit.bootsystem.statical;

import java.util.ArrayList;
import java.util.List;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;


@Path
public class IdleStatical {
	
	//闲置分析
	//统计一个时间段 所有设备关机机的数量  看看 闲置情况是什么   峰值
	
	@Post
	public JsonO staticData(String start_date,String end_date){
		
        List<String> ziduan=new ArrayList<String>();
		 
		 for(int i=0;i<24;i++){
			 String dd="d";
			 if(i<10){
				 dd=dd+"0"+i;
			 }
			 else{
				 dd=dd+i;
			 }
			 String ss=dd+"00";
			 ziduan.add(ss);
			 String ss2=dd+"30";
			 ziduan.add(ss2);
		 }
		 
		 String sql="";
		 for(int x=0;x<ziduan.size();x++){
			 String shijian=ziduan.get(x);
			 String s1=shijian.substring(1, 3);
			 String s2=shijian.substring(3, 5);
			 String s3=s1+":"+s2;
			 
			 String sql2=" SELECT COUNT(*) as count ,'"+s3+"' as date_region FROM static_useing WHERE  add_date > '"+start_date+"' and add_date < '"+end_date+"'  and "+ziduan.get(x)+"!='1'  ";
			 
			 if(x==ziduan.size()-1){
				 sql=sql+sql2;
			 }
			 else{
				 sql=sql+sql2+" UNION ALL ";
			 }
		 }
		 
		
	    //拼接字符串		 zd
		 JsonA ja = DB3.getResultAsJsonA(Template.apply(sql), "total_run_time,start_times");
		 
		 JsonO j_obj=new JsonO();
		 j_obj.put("history_peak", ja);
		 
	
		 return j_obj;
		
	}
	
	
	@Post
	public JsonO  staticStartTime(String start_date,String end_date){
		 JsonO j_obj=new JsonO();
		 // 一次都没开过的设备数量（一个时间区间里面）
		 
		 String  sql3=" SELECT COUNT(*) as num ,static_date FROM static_useing WHERE start_times=0  and  static_date >= '"+start_date+"' and static_date <= '"+end_date+"'  GROUP BY static_date ";
			
	
		//一次都没开过的设备数量（一个时间区间里面）
		// SELECT COUNT(*) ,static_date FROM static_useing WHERE start_times='0'  and  static_date >= '2019-04-1 00:00:00' and static_date <= '2019-04-20 00:00:00' GROUP BY static_date
		 	 
		 JsonA ja4 = DB3.getResultAsJsonA(Template.apply(sql3), "num");
		 j_obj.put("StartTimeCount", ja4);
		 return j_obj;
	}
	
	 public static void main(String args[]){
		 
		 IdleStatical   pe=new IdleStatical();		 
		 JsonO  jo= pe.staticStartTime( "2019-04-1 00:00:00", "2019-04-16 23:59:59");
		 System.out.println(jo);
		 
	 }


}
