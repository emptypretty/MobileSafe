package com.itheima.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.ToastUtil;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		initUI();
	}

	private void initUI() {
		// TODO Auto-generated method stub

		cb_box = (CheckBox) findViewById(R.id.cb_box_open_protect);

		boolean open_security = PrefUtils.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_PROTECT, false);
		cb_box.setChecked(open_security);

		// 2,根据状态，修改checkbox后续的文字显示

		if (open_security) {
			cb_box.setText("防盗保护已开启");
		} else {
			cb_box.setText("防盗保护已关闭");
		}

		// 在点击此单选框是,选中状态的切换(选中--->未选中,未选中---->选中),是否选中的事件
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_box.setText("防盗保护已开启");
				} else {
					cb_box.setText("防盗保护已关闭");
				}
				PrefUtils.putBoolean(Setup4Activity.this,
						ConstantValue.OPEN_PROTECT, isChecked);
			}
		});

	}

	@Override
	protected void showNextPage() {

		boolean is_open_security = PrefUtils.getBoolean(this,
				ConstantValue.OPEN_PROTECT, false);
		if (is_open_security) {
			Intent intent = new Intent(getApplicationContext(),
					SetupOverActivity.class);
			startActivity(intent);
			finish();
			PrefUtils.putBoolean(this, ConstantValue.SETUP_OVER, true);

			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.show(this, "必须开启防盗保护");
		}

	}

	@Override
	protected void showPrePage() {

		Intent intent = new Intent(getApplicationContext(),
				Setup3Activity.class);
		startActivity(intent);

		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
