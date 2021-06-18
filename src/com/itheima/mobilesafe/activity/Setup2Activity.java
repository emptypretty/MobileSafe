package com.itheima.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.ToastUtil;
import com.itheima.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_sim_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		initUI();

	}

	/**
	 * 
	 */
	private void initUI() {
		// TODO Auto-generated method stub

		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);

		// 1,回显（读取已有的绑定状态，用作显示，sp中是否存储了sim卡的序列号）
		String sim_number = PrefUtils.getString(getApplicationContext(),
				ConstantValue.SIM_SERIAL_NUMBER, "");
		// 2，判断是否序列号为""
		if (TextUtils.isEmpty(sim_number)) {
			siv_sim_bound.setCheck(false);
		} else {
			siv_sim_bound.setCheck(true);
		}

		siv_sim_bound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 3，获取原有的状态
				boolean isCheck = siv_sim_bound.isCheck();

				// 4，将原有状态取反
				// 5,状态设置给当前条目
				siv_sim_bound.setCheck(!isCheck);

				if (!isCheck) {
					// 6，存储（序列卡号）
					// 6.1获取sim卡序列号TelephoneManager
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String simSerialNumber = manager.getSimSerialNumber();
					PrefUtils.putString(getApplicationContext(),
							ConstantValue.SIM_SERIAL_NUMBER, simSerialNumber);
				} else {
					PrefUtils.remove(getApplicationContext(),
							ConstantValue.SIM_SERIAL_NUMBER);
				}

			}
		});
	}

	@Override
	protected void showNextPage() {
		// TODO Auto-generated method stub
		String serialNumber = PrefUtils.getString(this,
				ConstantValue.SIM_SERIAL_NUMBER, "");

		if (!TextUtils.isEmpty(serialNumber)) {
			Intent intent = new Intent(getApplicationContext(),
					Setup3Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.show(this, "请绑定sim序列号");
		}
	}

	@Override
	protected void showPrePage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getApplicationContext(),
				Setup1Activity.class);
		startActivity(intent);

		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
