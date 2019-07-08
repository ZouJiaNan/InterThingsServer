package com.casit.bootsystem.util;


import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class MultipartRequest {
	private static Logger log = Logger.getLogger(MultipartRequest.class);
	private String charset = "UTF-8";
	private Hashtable map = new Hashtable();
	private HttpServletRequest request;
	private List fileItems = new ArrayList();
	private boolean isMultipart;
	public MultipartRequest(HttpServletRequest request) {
		this(request, -1, -1, null);
	}
	// 通过构造函数限定附件使用的内存大小，最大的附件大小和临时目录
	public MultipartRequest(HttpServletRequest request, int maxMemorySize,
			int maxFileSize, String tempDirectory) {
		this.charset = request.getCharacterEncoding();
		log.info("charset:" + this.charset);
		this.request = request;
		// 判断该request对象是否包含多个部分，是否可以包含附件
		isMultipart = ServletFileUpload.isMultipartContent(request);
		ServletFileUpload upload;
		DiskFileItemFactory factory;
		if (isMultipart) {
			// 使用FileUpload组件中的类
			factory = new DiskFileItemFactory();
			if (maxMemorySize > 10 * 1024)
				factory.setSizeThreshold(maxMemorySize);
			if (tempDirectory != null)
				factory.setRepository(new File(tempDirectory));
			upload = new ServletFileUpload(factory);
			if (maxFileSize > 0)
				upload.setSizeMax(maxFileSize);
			try {
				fileItems = upload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < fileItems.size(); i++) {
				FileItem item = (FileItem) fileItems.get(i);
				map.put(item.getFieldName(), item);
			}
		} else {
			Enumeration enu = request.getParameterNames();
			while (enu.hasMoreElements()) {
				Object tmp = enu.nextElement();
				if (tmp != null)
					map.put(tmp.toString(), request
							.getParameter(tmp.toString()));
			}
		}
	}
	public MultipartRequest(HttpServletRequest request, int maxMemorySize,
			int maxFileSize) {
		this(request, maxMemorySize, maxFileSize, null);
	}
	// 判断Request中是否包含name域
	public boolean contains(String name) {
		String value = getString(name);
		return value != null;
	}
	public boolean equals(String name, String compareValue) {
		String value = getString(name);
		return (value != null) && value.equals(compareValue);
	}
	public boolean equalsIgnoreCase(String name, String compareValue) {
		String value = getString(name);
		return (value != null) && value.equalsIgnoreCase(compareValue);
	}
	// 从存储日期的域中，获得一个日期对象
	public Date getDate(String name, String dateFormat) {
		String value = null;
		if (!isMultipart()) {
			value = request.getParameter(name);
		} else {
			value = getFieldValueFromMultiypartRequest(name);
		}
		Date result = DateUtil.parse(dateFormat, value);
		return result;
	}
	public double getDouble(String name) {
		return getDouble(name, -1);
	}
	public double getDouble(String name, double defaultValue) {
		String value = null;
		if (!isMultipart()) {
			value = request.getParameter(name);
		} else {
			value = getFieldValueFromMultiypartRequest(name);
		}
		double result = parseDouble(value);
		result = (result == -1) ? defaultValue : result;
		return result;
	}
	public double[] getDoubleArray(String name) {
		String[] value = getStringArray(name);
		double[] result = new double[value.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = parseDouble(value[i]);
		}
		return result;
	}
	// 从request对象的文件域获得文件流
	public InputStream getFielInputStream(String fieldName) throws Exception {
		FileItem item = getFile(fieldName);
		return ((item != null) & !item.isFormField()) ? item.getInputStream()
				: null;
	}
	// 从request对象中，获得一个表示附件的FileItem对象
	public FileItem getFile(String fieldName) {
		return (FileItem) map.get(fieldName);
	}
	// 返回文件域中存储的文件名
	public String getFileName(String fieldName) {
		FileItem item = getFile(fieldName);
		String fname = (item != null) ? item.getName() : null;
		if ((fname != null) && (fname.length() > 0)) {
			fname = fname.substring(fname.lastIndexOf(File.separatorChar) + 1);
		}
		return fname;
	}
	public String[] getFileNames() {
		FileItem[] items = getFiles();
		String[] result = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			result[i] = items[i].getName();
			if ((result[i] != null) && (result[i].length() > 0)) {
				result[i] = result[i].substring(result[i]
						.lastIndexOf(File.separatorChar) + 1);
			}
		}
		return result;
	}
	public Set getParameterNamesSet() {
		return map.keySet();
	}
	public Enumeration getParameterNames() {
		return map.keys();
	}
	public long getFileSize(String fieldName) {
		FileItem item = getFile(fieldName);
		return (item != null) ? item.getSize() : 0;
	}
	// 返回文件域的数组
	public FileItem[] getFiles() {
		ArrayList arr = new ArrayList();
		for (int i = 0; i < fileItems.size(); i++) {
			FileItem item = (FileItem) fileItems.get(i);
			if (!item.isFormField()) {
				arr.add(item);
			}
		}
		FileItem[] result = new FileItem[arr.size()];
		return (FileItem[]) arr.toArray(result);
	}
	public float getFloat(String name) {
		return getFloat(name, -1);
	}
	public float getFloat(String name, float defaultValue) {
		String value = null;
		if (!isMultipart()) {
			value = request.getParameter(name);
		} else {
			value = getFieldValueFromMultiypartRequest(name);
		}
		float result = parseFloat(value);
		result = (result == -1) ? defaultValue : result;
		return result;
	}
	public float[] getFloatArray(String name) {
		String[] value = getStringArray(name);
		float[] result = new float[value.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = parseFloat(value[i]);
		}
		return result;
	}
	public int getInt(String name) {
		return getInt(name, -1);
	}
	public int getInt(String name, int defaultValue) {
		String value = null;
		if (!isMultipart()) {
			value = request.getParameter(name);
		} else {
			value = getFieldValueFromMultiypartRequest(name);
		}
		int result = parseInt(value);
		result = result == -1 ? defaultValue : result;
		return result;
	}
	public int[] getIntArray(String name) {
		String[] value = getStringArray(name);
		int[] result = new int[value.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = parseInt(value[i]);
		}
		return result;
	}
	public long getLong(String name) {
		return getLong(name, -1);
	}
	public long getLong(String name, long defaultValue) {
		String value = null;
		if (!isMultipart()) {
			value = request.getParameter(name);
		} else {
			value = getFieldValueFromMultiypartRequest(name);
		}
		long result = parseLong(value);
		result = (result == -1) ? defaultValue : result;
		return result;
	}
	public long[] getLongArray(String name) {
		String[] value = getStringArray(name);
		long[] result = new long[value.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = parseLong(value[i]);
		}
		return result;
	}
	public boolean isMultipart() {
		return isMultipart;
	}
	public String getString(String name) {
		return getString(name, null);
	}
	public String getParameter(String name) {
		return getString(name, null);
	}
	public String getString(String name, String defaultValue) {
		String value = null;
		if (!isMultipart()) {
			value = request.getParameter(name);
		} else {
			value = getFieldValueFromMultiypartRequest(name);
		}
		value = (value == null) ? defaultValue : value;
		return value;
	}
	public String[] getStringArray(String name) {
		String[] result = null;
		if (!isMultipart()) {
			result = request.getParameterValues(name);
		} else {
			result = getFieldValuesFromMultiypartRequest(name);
		}
		if (result == null) {
			result = new String[0];
		}
		return result;
	}
	// 把文件域fieldName表示的文件写到file对象所指定的文件中
	public void writeFileToDisk(String fieldName, File file) throws Exception {
		FileItem item = getFile(fieldName);
		if (item != null) {
			item.write(file);
		}
	}
	// 把文件域fieldName表示的文件写到filePath所指定的文件中
	public void writeFileToDisk(String fieldName, String filepath)
			throws Exception {
		File file = new File(filepath);
		if (file.exists()) {
			throw new Exception("file has exist");
		}
		writeFileToDisk(fieldName, file);
	}
	// 返回域的值
	private String getFieldValueFromMultiypartRequest(String fieldName) {
		FileItem item = (FileItem) map.get(fieldName);
		String temp = null;
		if (item == null)
			return null;
		try {
			if (item.isFormField())
				temp = item.getString(this.charset);
			else {
				temp = item.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (item != null) ? temp : null;
	}
	private String[] getFieldValuesFromMultiypartRequest(String fieldName) {
		ArrayList arr = new ArrayList();
		for (int i = 0; i < fileItems.size(); i++) {
			FileItem item = (FileItem) fileItems.get(i);
			if (item.getFieldName().equals(fieldName)) {
				try {
					arr.add(item.getString(this.charset));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		String[] result = new String[arr.size()];
		return (String[]) arr.toArray(result);
	}
	private double parseDouble(String name) {
		double result = -1;
		if (name != null) {
			try {
				result = Double.parseDouble(name);
			} catch (Exception e) {
			}
		}
		;
		return result;
	}
	private float parseFloat(String name) {
		float result = -1;
		if (name != null) {
			try {
				result = Float.parseFloat(name);
			} catch (Exception e) {
			}
		}
		;
		return result;
	}
	private int parseInt(String name) {
		int result = -1;
		if (name != null) {
			try {
				result = Integer.parseInt(name);
			} catch (Exception e) {
			}
		}
		;
		return result;
	}
	private long parseLong(String name) {
		long result = -1;
		if (name != null) {
			try {
				result = Long.parseLong(name);
			} catch (Exception e) {
			}
		}
		;
		return result;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
}
