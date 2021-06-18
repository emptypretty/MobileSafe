package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.service.AddressService;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.ServiceUtil;
import com.itheima.mobilesafe.view.SettingClickView;
import com.itheima.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

	private String[] mToastStyleDes;
	private int mToastStyle;
	private SettingClickView scv_toast_style;
	private SettingClickView scv_location;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initUpdate();

		initAddress();

		initToastStyle();

		initLocation();
	}

	/**
	 * 双击居中View所在屏幕位置的处理方法
	 */
	private void initLocation() {
		// TODO Auto-generated method stub
		scv_location = (SettingClickView) findViewById(R.id.scv_location);
		scv_location.setTitle("归属地提示框的位置");
		scv_location.setDes("设置归属地提示框的位置");

		scv_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),
						ToastLocationActivity.class));
			}
		});

	}

	private void initToastStyle() {
		// TODO Auto-generated method stub
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		scv_toast_style.setTitle("设置归属地显示风格");
		// 创建描述文字所在的string类型数组

		mToastStyleDes = new String[] { "透明", "橙色", "蓝色", "灰色", "绿色" };

		// 2.通过sp获取吐司显示样式的索引值（int），用于获取描述文字
		mToastStyle = PrefUtils
				.getInt(this, ConstantValue.TOAST_STYLE_INDEX, 0);

		// 3.通过索引，获取字符串数组中的文字，显示给描述内容控件
		scv_toast_style.setDes(mToastStyleDes[mToastStyle]);

		// 4.监听点击事件，弹出对话框

		scv_toast_style.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showToastStyleDialog();

			}
		});
	}

	/**
	 * 创建选中显示样式的对话框
	 */
	protected void showToastStyleDialog() {
		// TODO Auto-generated method stub
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");

		mToastStyle = PrefUtils
				.getInt(this, ConstantValue.TOAST_STYLE_INDEX, 0);
		// 选择单个条目事件监听
		// （1.string 类型的数组描述颜色文字数组，
		// 2.弹出对话框的时候的选中条目索引值
		// 3.点击某一个条目后触发的点击事件（1，记录选中的索引值2.关闭对话框3.显示选中的颜色））
		builder.setSingleChoiceItems(mToastStyleDes, mToastStyle,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {// which选中的索引值
						// 1，记录选中的索引值2.关闭对话框3.显示选中的颜色
						PrefUtils.putInt(getApplicationContext(),
								ConstantValue.TOAST_STYLE_INDEX, which);
						dialog.dismiss();
						scv_toast_style.setDes(mToastStyleDes[which]);

					}
				});

		// 取消条目的事件

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		builder.show();

	}

	/**
	 * 是否显示电话号码归属地的方法
	 */
	private void initAddress() {
		// TODO Auto-generated method stub

		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
		// 对服务是否开启的状态显示
		boolean isRunning = ServiceUtil.isRunning(this,
				"com.itheima.mobilesafe.service.AddressService");
		siv_address.setCheck(isRunning);

		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isCheck = siv_address.isCheck();
				siv_address.setCheck(!isCheck);

				if (!isCheck) {

					// 开启服务，管理吐司
					startService(new Intent(getApplicationContext(),
							AddressService.class));
				} else {
					// 关闭服务，不需要显示吐司
					stopService(new Intent(getApplicationContext(),
							AddressService.class));
				}

			}
		});

	}

	/**
	 * 版本更新开关
	 */
	private void initUpdate() {
		// TODO Auto-generated method stub
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// 获取已有的开关状态，用作显示
		boolean open_update = PrefUtils.getBoolean(this,
				ConstantValue.UPDATE_VERSION, false);
		siv_update.setCheck(open_update);

		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 若干之前是选中的，点击之后，变成未选中
				// 若干之前是未选中的，点击之后，变成选中

				// 获取之前的选中状态
				boolean check = siv_update.isCheck();
				siv_update.setCheck(!check);

				PrefUtils.putBoolean(getApplicationContext(),
						ConstantValue.UPDATE_VERSION, !check);

			}
		});
	}
}
