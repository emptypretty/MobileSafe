package com.itheima.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class SettingItemView extends RelativeLayout {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.itheima.mobilesafe";
	private static final String tag = "SettingItemView";
	private TextView tv_title;
	private TextView tv_des;
	private CheckBox cb_box;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// xml--->view 将设置界面的一个条目转换成view对象

		View.inflate(context, R.layout.setting_item_view, this);
		/*
		 * View view = View.inflate(context, R.layout.setting_item_view, null);
		 * this.addView(view);
		 */
		// 自定义组合控件中的标题描述
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);

		// 获取自定义以及原生属性的操作，写在此处，AttributeSet attrs对象中获取
		initAttrs(attrs);

		tv_title.setText(mDestitle);

	}

	/**
	 * 返回属性集合中自定义属性值
	 * 
	 * @param attrs
	 *            构造方法中维护好的属性集合
	 */
	private void initAttrs(AttributeSet attrs) {
		// TODO Auto-generated method stub
		// 获取属性的总个数
		Log.i(tag, "----------" + attrs.getAttributeCount());
		// 获取属性名称以及属性值

		/*
		 * for (int i = 0; i < attrs.getAttributeCount(); i++) { Log.i(tag,
		 * "name = " + attrs.getAttributeName(i)); Log.i(tag, "value = " +
		 * attrs.getAttributeValue(i)); Log.i(tag, "fengexian==========="); }
		 */
		// 通过名空间+属性名称获取属性值
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
		Log.i(tag, mDestitle);
		Log.i(tag, mDesoff);
		Log.i(tag, mDeson);

	}

	/**
	 * 判断是否开启的方法
	 * 
	 * @return 返回当前SettingItemView是否选中状态 true开启（checkBox返回true）
	 *         false关闭（CheckBox返回true）
	 */
	public boolean isCheck() {
		return cb_box.isChecked();
	}

	/**
	 * @param isCheck
	 *            是否作为开启的变量，由点击过程中去做传递
	 */
	public void setCheck(boolean isCheck) {
		// 当前条目早选择的过程中，cb_box选中状态也跟随（ischeck）变化
		cb_box.setChecked(isCheck);
		if (isCheck) {
			tv_des.setText(mDeson);
		} else {
			tv_des.setText(mDesoff);
		}

	}
}
