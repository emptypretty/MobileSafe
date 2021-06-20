package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.service.LockScreenService;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.ServiceUtil;

public class ProcessSettingActivity extends Activity {
	private CheckBox cb_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);

		initSystemProcessVisable();
		initLockScreenClear();
	}

	private void initLockScreenClear() {
		// 在开启锁屏清理时,开启一个服务,在服务中时刻去监听锁屏广播,如果接收到此广播,则清理进程
		// 判断服务是否正在运行,运行状态,就是单选框的是否选中的状态
		cb_clear = (CheckBox) findViewById(R.id.cb_clear);
		boolean isRunning = ServiceUtil.isRunning(this,
				"com.itheima.mobilesafe.service.LockScreenService");
		cb_clear.setChecked(isRunning);
		if (isRunning) {
			cb_clear.setText("锁屏清理已开启");
		} else {
			cb_clear.setText("锁屏清理已关闭");
		}

		// 监听cb_clear状态发生改变方法
		cb_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// 开启服务
					startService(new Intent(getApplicationContext(),
							LockScreenService.class));
					cb_clear.setText("锁屏清理已开启");
				} else {
					// 关闭服务
					stopService(new Intent(getApplicationContext(),
							LockScreenService.class));
					cb_clear.setText("锁屏清理已关闭");
				}
			}
		});
	}

	private void initSystemProcessVisable() {
		// 回显状态过程
		final CheckBox cb_system_visable = (CheckBox) findViewById(R.id.cb_system_visable);
		boolean isCheck = PrefUtils.getBoolean(this,
				ConstantValue.IS_SYSTEM_VISABLE, false);
		cb_system_visable.setChecked(isCheck);

		if (isCheck) {
			cb_system_visable.setText("显示系统进程");
		} else {
			cb_system_visable.setText("隐藏系统进程");
		}
		// 设置状态过程
		cb_system_visable
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cb_system_visable.setText("显示系统进程");
						} else {
							cb_system_visable.setText("隐藏系统进程");
						}
						PrefUtils.putBoolean(ProcessSettingActivity.this,
								ConstantValue.IS_SYSTEM_VISABLE, isChecked);
					}
				});
	}
}
