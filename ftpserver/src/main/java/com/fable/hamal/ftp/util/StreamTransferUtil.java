package com.fable.hamal.ftp.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamTransferUtil {
	/**
	 * 将OutputStream转换为InputStream
	 * @param out ByteArrayOutputStream
	 * @return
	 */
	public static ByteArrayInputStream parse(OutputStream out) {
		ByteArrayInputStream swapStream =null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos = (ByteArrayOutputStream) out;
			swapStream = new ByteArrayInputStream(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return swapStream;
	}
	
	public static FileInputStream parseBufferedStream(BufferedOutputStream out,String completeName) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(new File(completeName));
			byte[] buffer = new byte[8192];
			int i = fileInputStream.read(buffer);
			while (i!=-1) {
				out.write(buffer,0,i);
				out.flush();
				i = fileInputStream.read(buffer);
			}
			out.flush();
			fileInputStream.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileInputStream;
	}
	public static void writeFile(InputStream inputStream,OutputStream outputStream) {
		int c;
		try {
			while((c = inputStream.read()) !=-1) {
				outputStream.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream !=null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (outputStream !=null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
