package com.itheima.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	// 名称，包名，图标，（）内存，sd卡，系统，用户
	public String name;
	public String packageName;
	public Drawable icon;
	public boolean isSdCard;
	public boolean isSystem;

	public AppInfo() {
		super();
	}

	public AppInfo(String packageName, String name, Drawable drawable,
			boolean isSystem, boolean isSdApp) {
		super();
		this.packageName = packageName;
		this.name = name;
		this.icon = drawable;
		this.isSystem = isSystem;
		this.isSdCard = isSdApp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public boolean isSdCard() {
		return isSdCard;
	}

	public void setSdCard(boolean isSdCard) {
		this.isSdCard = isSdCard;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	@Override
	public String toString() {
		return "AppInfo [packageName=" + packageName + ", name=" + name
				+ ", drawable=" + icon + ", isSystem=" + isSystem
				+ ", isSdApp=" + isSdCard + "]";
	}

}
