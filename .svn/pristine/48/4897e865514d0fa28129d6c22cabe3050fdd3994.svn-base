package com.fable.hamal.ftp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
* 
* ftp上文件的封装
* @author houly
*
*/


public class FtpFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -438376767446894227L;
	/**文件对应的工作空间，自上而下依次*/
	private List<String> list = new ArrayList<String>();
	/**文件名称*/
	private String fileName ;

	
	
	
	public FtpFile() {
		super();
	}

	public FtpFile(List<String> list, String fileName) {
		super();
		this.list = list;
		this.fileName = fileName;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FtpFile other = (FtpFile) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FtpFile [fileName=" + fileName + ", list=" + list + "]";
	}
	
	
}
