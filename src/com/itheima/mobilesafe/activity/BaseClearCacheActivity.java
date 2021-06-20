package com.itheima.mobilesafe.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost.TabSpec;

import com.itheima.mobilesafe.R;

public class BaseClearCacheActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_clear_cache);

		// 构建内容页对应的两个activity(缓存清理,sd卡清理activity)

		// newTabSpec维护一个tag
		// 指定选项卡的布局结构
		// 选项卡1
		TabSpec tab1 = getTabHost().newTabSpec("缓存清理").setIndicator("缓存清理");
		// 指定选项卡选中后跳转到的意图,开启activity操作
		tab1.setContent(new Intent(this, ClearCacheActivity.class));

		// newTabSpec维护一个tag
		// 指定选项卡的布局结构
		// 选项卡2
		TabSpec tab2 = getTabHost().newTabSpec("sd卡清理").setIndicator("SD卡清理");
		tab2.setContent(new Intent(this, SDClearCacheActivity.class));

		// 将上诉两个选项卡做添加操作
		getTabHost().addTab(tab1);
		getTabHost().addTab(tab2);
	}
}
