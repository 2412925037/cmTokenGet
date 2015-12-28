package com.use.nice.manager;

 /**
 * 用于获取asset下文件的信息的model类
 * @author zhengnan 
 * @date 2015年5月4日
 */
public   class AssertFileInfo{
	
	public AssertFileInfo(String name,boolean isObs) {
		this(name, isObs, true);
	}
	 public AssertFileInfo(String name,boolean isObfuscate,boolean isExist){
		 this.fileName = name;
		 this.isObfuscate = isObfuscate;
		 this.isExist = isExist;
	 }
	private  String fileName =null;//文件名
	private boolean isObfuscate =false;//是否已被hash混淆

	 public boolean isExist() {
		 return isExist;
	 }

	 public void setIsExist(boolean isExist) {
		 this.isExist = isExist;
	 }

	 private boolean isExist = true;
	public String getFileName() {
		if(!isExist) throw new RuntimeException();
		return fileName;
	}
	public void setFileName(String fileName) {
	    this.fileName = fileName;
	}
	public boolean isObfuscate() {
		if(!isExist) throw new RuntimeException();
	    return isObfuscate;
	}
    }