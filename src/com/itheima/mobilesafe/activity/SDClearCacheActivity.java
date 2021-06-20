package com.itheima.mobilesafe.activity;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SDClearCacheActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(this);
		textView.setText("SDClearCacheActivity");
		setContentView(textView);
		
		//清理手机中存放在sd卡中的缓存数据???????
		//手机卫士---->sd卡中  mobilesafe/cache/
		//金山------->sd卡中  jinshan/cache
		
		//找到常见应用的sd卡缓存文件夹路径的数据库
		
		//1,判断数据库中所有的文件夹路径是否存在
		File file = new File("路径");
		//2,判断文件是否存在
		if(file.exists()){
			//3,认为他是一个缓存文件夹
			//4,递归删除此文件中的文件夹,以及文件
		}
		
	}
}
