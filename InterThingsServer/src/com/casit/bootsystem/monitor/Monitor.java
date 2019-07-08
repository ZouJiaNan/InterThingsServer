package com.casit.bootsystem.monitor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;

import com.casit.bootsystem.util.HttpRequest;
import com.casit.bootsystem.util.SysSetting;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;


@Path
public class Monitor {

	  //从erp的视图中去取数据
	  @Post
	  public JsonO getErpInfo()  {
	    String ddd = "";
	    JsonO json = new JsonO();

	    //String keys="02004020018,02004020019,02004020020,02004020021,02004020022,02004020023,02004020024,02004030136,02004030137,02004030138,02004030139";
	     
	    String keys="ZCKP02004020018,ZCKP02004020019,ZCKP02004020020,ZCKP02004020021,ZCKP02004020022,ZCKP02004020023,ZCKP02004020024,ZCKP02004030136,ZCKP02004030137,ZCKP02004030138,ZCKP02004030139";
		   
	    try {
			ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/getInfoAllByNums?equi_arch_nos=" + URLEncoder.encode(keys, "UTF-8"), "");
		} catch (UnsupportedEncodingException e) {
			ddd = HttpRequest.sendPost(SysSetting.ERP_URL+"Erp/getInfoAllByNums?equi_arch_nos=" +keys, "");;
		}      
	       JSONArray joo = new JSONArray(ddd);
	       JsonA ja = new JsonA(ddd.toString());
	       json.put("rows", ja);
	  
	       json.putUnQuoted("total", keys.split(",").length+"");
	      return json;
	  }
}
