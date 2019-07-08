package com.casit.bootsystem.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.casit.bootsystem.erp.Erp;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.Template;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
@Path
public class ExcelUtil {
	 
	/**
	 * 通过模板导出excel文件
	 * @param dataList 数据集合
	 * @param dataEntityClass 数据实体类
	 * @param templateName 模板名称
	 * @param titleRowNum 固定标题行数
	 * @return
	 * @throws Exception
	 */
	public static <T> ByteArrayOutputStream exportExcel(JsonA dataList, JsonO j_obj, String templateName, int titleRowNum) throws Exception {
		// 读取excel模板文件
		InputStream in = ExcelUtil.class.getClassLoader().getResourceAsStream("templates/"+templateName);
		XSSFWorkbook workbook = new XSSFWorkbook(in);
        XSSFSheet sheet = workbook.getSheetAt(0);
        
        Map<String,String> datas = new HashMap<String,String>();  
        datas.put("title","呼吸机当前运行情况统计");  
        datas.put("date",DateTools.DateToStr(new Date()));  
        datas.put("dep","医学工程科");  
        replaceFinalData(datas,sheet); 
        // 设置默认行高
        //sheet.setDefaultRowHeightInPoints(25F);
        // 获取数据实体类的所有字段
        //Field[] declaredFields = dataEntityClass.getDeclaredFields();
        //int declaredFieldsSize = declaredFields.length;
        // ByteArray输出字节流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 遍历数据写入到excel
        int dataListSize = dataList.size();
        for (int i = 0; i < dataListSize; i++) { 
        	// 获取数据list里面的对象实例
        	JsonO json_o = dataList.getJsonO(i);
            // 创建行
            Row row = sheet.createRow(i + titleRowNum);
            
            Iterator<String> declaredFields= j_obj.getKeyIterator();
            int jj=0;
            while(declaredFields.hasNext()){

            	String field=(String)declaredFields.next();
            	// 获取字段的值
            	 Object value = json_o.getString(field);
            	 
            	 // 创建单元格
 	            Cell cell = row.createCell(jj);jj++;
 	            
 	            // 设置单元格样式
 	            XSSFCellStyle cellStyle = workbook.createCellStyle(); 
 	            
 	            // 设置文本对齐方式
 	            cellStyle.setWrapText(true); //设置自动换行  
 	            cellStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
 	            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
 	            
 	            // 设置边框
 	            cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
 	            cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
 	            cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
 	            cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
 	            
 	            // 设置字体
 	            XSSFFont font = workbook.createFont();
 	            font.setFontName("宋体");// 设置字体名称
 	            font.setFontHeightInPoints((short) 11);// 设置字号
 	            font.setColor(IndexedColors.BLACK.index);// 设置字体颜色
 	            
 	          //设置背景颜色
 	            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
 	            //solid 填充  foreground  前景色
 	            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
 	            
 	            
 	            cellStyle.setFont(font);
 	            
 	            cell.setCellStyle(cellStyle); 
 	            cell.setCellValue(value != null ? value.toString() : "");
 	            
 	            
            	}
      
        }
        workbook.write(baos);
 
		return baos;
    }
	
	 @Post
	public void exportMacRunState(String keys, String region_id_build, String region_id_floor, String deptid, String base_state_id,HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
	
		 Erp erp=new Erp();
		ByteArrayOutputStream baos = null;
		OutputStream out = null;
		try {
			
			//先构建字段名
			JsonA dataList=new JsonA();
			
//			JsonO  j_obj=new JsonO();
//			j_obj.putQuoted("mac", "MAC地址");
//			j_obj.putQuoted("device_type", "设备类型");
//			j_obj.putQuoted("ssid", "WIFI名称");
//			j_obj.putQuoted("run_state", "运行状态");
//			j_obj.putQuoted("device_id", "设备编号");
//			j_obj.putQuoted("deptnm", "部门");
//			j_obj.putQuoted("region_father_name", "大楼");
//			j_obj.putQuoted("name", "楼层");
//			j_obj.putQuoted("open", "是否开机");
//			j_obj.putQuoted("y_run", "昨日运行情况");			
//			j_obj.putQuoted("insert_date", "最后一次读取时间");
//			
			
			
			JsonO  j_obj=new JsonO("{'device_id':'设备编号','factory_id':'采集盒ID','mac':'MAC地址','open':'是否开机','y_run':'昨日运行情况','device_type':'设备类型','name':'楼层','deptnm':'部门','insert_date':'最后一次读取时间','ssid':'WIFI名称','run_state':'运行状态','region_father_name':'大楼'}");
			
			
			dataList.add(j_obj);
			
			
			JsonO jo=erp.real_info_nopage(keys, region_id_build, region_id_floor, deptid, base_state_id);
			
		
			
			JsonA  fff_arr=jo.getJsonA("rows"); 
			
			for(int i=0;i<fff_arr.size();i++){
				
				 JsonO   joo=fff_arr.getJsonO(i);
				 Iterator<String> declaredFields= j_obj.getKeyIterator();
				 JsonO  j_obj_new=new JsonO();
	             while(declaredFields.hasNext()){
	            	
	            		String field=(String)declaredFields.next();
	                	// 获取字段的值
	            		String value = joo.getString(field);
	                	 
	                	 j_obj_new.putQuoted(field, value);
	                	
	            }
	             dataList.add(j_obj_new);
				
			}			    
			
			//dataList=jo.getJsonA("rows");
			
			System.out.print(dataList);
			baos = ExcelUtil.exportExcel(dataList, j_obj, "test.xlsx", 2);
			// 设置响应消息头，告诉浏览器当前响应是一个下载文件
			response.setContentType( "application/x-msdownload");
			// 告诉浏览器，当前响应数据要求用户干预保存到文件中，以及文件名是什么 如果文件名有中文，必须URL编码 
			String FileName = URLEncoder.encode("呼吸机实时数据列表.xlsx", "UTF-8"); 
			response.setHeader( "Content-Disposition", "attachment;filename=" + FileName);
			out = response.getOutputStream();
			baos.writeTo(out);
		} catch (Exception e) {
			e.printStackTrace();
		    throw new Exception("导出失败：" + e.getMessage());
		} finally {
			if(baos != null){
				baos.close();
			}
			if(out != null){
				out.close();
			}
		}
	}
	 
	 
	 /** 
	     * 根据map替换相应的常量，通过Map中的值来替换#开头的值 
	     * @param datas 
	     */  
	    public static void replaceFinalData(Map<String,String> datas,Sheet  sheet) {  
	        if(datas==null) return;  
	        for(Row row:sheet) {  
	            for(Cell c:row) {  
	                if(c.getCellType()!=Cell.CELL_TYPE_STRING) continue;  
	                String str = c.getStringCellValue().trim();  
	                if(str.startsWith("#")) {  
	                    if(datas.containsKey(str.substring(1))) {  
	                        c.setCellValue(datas.get(str.substring(1)));  
	                    }  
	                }  
	            }  
	        }  
	    } 
	    
	    
}