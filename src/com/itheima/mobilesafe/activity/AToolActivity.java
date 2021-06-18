package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class AToolActivity extends Activity {

	private TextView tv_address_query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		// 电话归属地查询方法
		initPhoneAddress();
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
