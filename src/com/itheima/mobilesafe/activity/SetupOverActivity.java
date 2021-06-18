package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;

public class SetupOverActivity extends Activity {

	private TextView tv_phone;
	private TextView tv_reset_setup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		boolean setup_over = PrefUtils.getBoolean(this,
				ConstantValue.SETUP_OVER, false);

		if (setup_over) {
			// 密码输入成功后，并且四个导航界面设置完成，----->停留在设置完成功能列表界面
			setContentView(R.layout.activity_setup_over);

			initUI();

		} else {
			// 密码输入成功后，四个导航界面没有设置完成，----->停留到导航界面第一个
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initUI() {
		// TODO Auto-generated method stub

		tv_phone = (TextView) findViewById(R.id.tv_phone);
		String phone = PrefUtils.getString(getApplicationContext(),
				ConstantValue.CONTACT_PHONE, "");
		tv_phone.setText(phone);

		// 让TextView具备可点击的操作，设置一个点击事件(TextView
		// ImageView默认没有点击事件)(Button默认具备点击事件)

		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						Setup1Activity.class));
				finish();
			}
		});

	}
}
