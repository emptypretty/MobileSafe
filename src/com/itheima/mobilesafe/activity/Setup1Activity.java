package com.itheima.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.itheima.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	// 手势滑动和平移是对应的关系
	@Override
	protected void showNextPage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getApplicationContext(),
				Setup2Activity.class);
		startActivity(intent);

		finish();

		// 开启平移动画

		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}

	@Override
	protected void showPrePage() {
		// 空实现

	}

}
