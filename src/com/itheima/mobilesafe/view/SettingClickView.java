package com.itheima.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class SettingClickView extends RelativeLayout {

	private TextView tv_title;
	private TextView tv_des;

	public SettingClickView(Context context) {
		this(context, null);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		View.inflate(context, R.layout.setting_click_view, this);

		// 自定义组合控件中的标题描述
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);

	}

	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setDes(String des) {
		tv_des.setText(des);
	}

}
