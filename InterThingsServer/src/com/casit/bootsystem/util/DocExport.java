package com.casit.bootsystem.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;


@Path
public class DocExport {
	
	
	/**
     * 导出Excel
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName,String []title,ArrayList values, HSSFWorkbook wb){

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for(int i=0;i<values.size();i++){
            row = sheet.createRow(i + 1);
            
            String[] dd=(String[])values.get(i);
           
            for(int j=0;j<dd.length;j++){
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(dd[j]);
            }
        }
        return wb;
    }
    
    @Post
    public void up(HttpServletRequest request, HttpServletResponse response)
    {
    	 //获取数据
    	List<JsonO> list=new ArrayList<JsonO>();
    	JsonO  jj=new JsonO();
    	
    	list.add(jj);list.add(jj);list.add(jj);

     ArrayList content=new ArrayList();

    //excel标题
    String[] title = {"名称","性别","年龄","学校","班级"};
  
     //excel文件名
    String fileName = "信息表"+System.currentTimeMillis()+".xls";

    //sheet名
    String sheetName = "信息表";

		for (int i = 0; i < list.size(); i++) {
			
			      String[]  one= new String[title.length];
			      one[0] = "44";
			      one[1] = "555";
			      one[2] = "777";
			      one[3] = "899";
			      one[4] = "5566666";
		         
		         content.add(one);
		}

		//创建HSSFWorkbook 
		HSSFWorkbook wb = getHSSFWorkbook(sheetName, title, content, null);
		
		//响应到客户端

	setResponseHeader(response, fileName);
	OutputStream os;
	try {
		os = response.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}



    }   
    
    //发送响应流方法
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
   
  
    
    

}
