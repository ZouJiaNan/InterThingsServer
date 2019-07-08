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

import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
@Path
public class CopyOfExcelUtil {
	 
	/**
	 * 通过模板导出excel文件
	 * @param dataList 数据集合
	 * @param dataEntityClass 数据实体类
	 * @param templateName 模板名称
	 * @param titleRowNum 固定标题行数
	 * @return
	 * @throws Exception
	 */
	public static <T> ByteArrayOutputStream exportExcel(List<T> dataList, Class<T> dataEntityClass, String templateName, int titleRowNum) throws Exception {
		// 读取excel模板文件
		InputStream in = CopyOfExcelUtil.class.getClassLoader().getResourceAsStream("templates/"+templateName);
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
        Field[] declaredFields = dataEntityClass.getDeclaredFields();
        int declaredFieldsSize = declaredFields.length;
        // ByteArray输出字节流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 遍历数据写入到excel
        int dataListSize = dataList.size();
        for (int i = 0; i < dataListSize; i++) { 
        	// 获取数据list里面的对象实例
            T instance = dataList.get(i);
            // 创建行
            Row row = sheet.createRow(i + titleRowNum);
            for(int j = 0; j < declaredFieldsSize; j++){
	        	Field field = declaredFields[j];
	        	field.setAccessible(true);
	        	// 获取字段的值
	            Object value = field.get(instance);
	            
	            // 创建单元格
	            Cell cell = row.createCell(j);
	            
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
       // workbook.close();
		return baos;
    }
	
	 @Post
	public void exportMacRunState(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		 
		ByteArrayOutputStream baos = null;
		OutputStream out = null;
		try {
			// excel数据  实际运用从数据库中查询
			List<ExcelDataTest> dataList = new ArrayList<ExcelDataTest>();
			
			
			
			ExcelDataTest data = new ExcelDataTest();
			data.setColumn1(123456);
			data.setColumn2("测试22测试");
			data.setColumn3("测试测试测试ee测试测试测试");
			dataList.add(data);
			dataList.add(data);
			dataList.add(data);
			
			
			
			
			System.out.print(dataList);
			baos = CopyOfExcelUtil.exportExcel(dataList, ExcelDataTest.class, "test.xlsx", 2);
			// 设置响应消息头，告诉浏览器当前响应是一个下载文件
			response.setContentType( "application/x-msdownload");
			// 告诉浏览器，当前响应数据要求用户干预保存到文件中，以及文件名是什么 如果文件名有中文，必须URL编码 
			String FileName = URLEncoder.encode("测试.xlsx", "UTF-8"); 
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