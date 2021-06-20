package com.itheima.mobilesafe.activity;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.SmsBackUp;
import com.itheima.mobilesafe.engine.SmsBackUp.CallBack;

public class AToolActivity extends Activity {

	private TextView tv_address_query;
	private TextView tv_sms_backup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		// 电话归属地查询方法
		initPhoneAddress();

		// 备份短信
		initSmsBackUp();

		// 开启常用号码的方法
		initCommonNumber();

		// 程序锁
		initAppLock();
	}

	private void initAppLock() {
		TextView tv_applock = (TextView) findViewById(R.id.tv_applock);
		tv_applock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						AppLockActivity.class));
			}
		});
	}

	private void initCommonNumber() {
		TextView tv_common_number_query = (TextView) findViewById(R.id.tv_common_number_query);
		tv_common_number_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent(getApplicationContext(),
						CommonNumberActivity.class));

			}
		});
	}

	private void initSmsBackUp() {

		// TODO Auto-generated method stub
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSmsBackUpDialog();
			}
		});

	}

	protected void showSmsBackUpDialog() {
		// 1.创建一个带进度条的对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");

		// 指定进度条的样式为水平

		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();

		// 4.获取系统短信 调用备份方法即可

		new Thread() {
			@Override
			public void run() {
				// 获取sd卡路径
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + File.separator + "sms74.xml";
				SmsBackUp.backup(getApplicationContext(), path, new CallBack() {

					@Override
					public void setProgress(int index) {
						// TODO Auto-generated method stub
						progressDialog.setProgress(index);
					}

					@Override
					public void setMax(int max) {
						// TODO Auto-generated method stub
						progressDialog.setMax(max);
					}
				});
				progressDialog.dismiss();
			};
		}.start();

	}

	private void initPhoneAddress() {
		// TODO Auto-generated method stub
		tv_address_query = (TextView) findViewById(R.id.tv_address_query);
		tv_address_query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),
						QueryAddressActivity.class));
			}
		});
	}
}
