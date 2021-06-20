package com.itheima.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	//名称
	public String name;
	//图标
	public Drawable drawable;
	//内存大小
	public long memSize;
	//类型
	public boolean isSystem;
	//勾选
	public boolean isCheck;
	//包名
	public String packageName;
	
	public ProcessInfo() {
		super();
	}

	public ProcessInfo(String name, Drawable drawable, long memSize,
			boolean isSystem, boolean isCheck, String packageName) {
		super();
		this.name = name;
		this.drawable = drawable;
		this.memSize = memSize;
		this.isSystem = isSystem;
		this.isCheck = isCheck;
		this.packageName = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public long getMemSize() {
		return memSize;
	}

	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "ProcessInfo [name=" + name + ", drawable=" + drawable
				+ ", memSize=" + memSize + ", isSystem=" + isSystem
				+ ", isCheck=" + isCheck + ", packageName=" + packageName + "]";
	}
}
