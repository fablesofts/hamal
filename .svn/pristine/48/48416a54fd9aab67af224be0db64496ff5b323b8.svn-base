package com.fable.hamal.ftp.util;

public class PathReader {
	public static void main(String[] args) {
		String path1 = PathReader.class.getClass().getResource("/").getPath();
		System.out.println(path1.substring(1,3));
		
	}
	/**
	 * 返回项目的根目录
	 * @return
	 */	
	public static String getRootPath() {
		String path1 = PathReader.class.getClass().getResource("/").getPath();
		return path1.substring(1,3);
	}
	/**
	 * 判断当前系统是否是windows
	 * @return true:windows  false :linux
	 */
	public static boolean isWindows() {
		String os = System.getProperties().getProperty("os.name");
		String windows = "Windows";
		boolean isWindows = os.indexOf(windows) == -1 ? false:true;
		return isWindows;
	}
}
