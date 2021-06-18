package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;

public class SetupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		boolean setup_over = PrefUtils.getBoolean(this,
				ConstantValue.SETUP_OVER, false);

		if (setup_over) {
			// 密码输入成功后，并且四个导航界面设置完成，----->停留在设置完成功能列表界面
			setContentView(R.layout.activity_setup_over);
		} else {
			// 密码输入成功后，四个导航界面没有设置完成，----->停留到导航界面第一个
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}

	}
}
