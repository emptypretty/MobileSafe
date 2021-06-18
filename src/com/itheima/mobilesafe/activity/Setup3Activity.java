package com.itheima.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		initUI();
	}

	private void initUI() {
		// TODO Auto-generated method stub

		// 显示电话号码的输入框
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		// 获取联系人电话号码回显过程
		String phone = PrefUtils.getString(this, ConstantValue.CONTACT_PHONE,
				"");
		et_phone_number.setText(phone);
		// 点击选择联系人的对话框
		Button bt_select_number = (Button) findViewById(R.id.bt_select_number);

		bt_select_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (data != null) {
			String phone = data.getStringExtra("phone");
			phone = phone.replace(" ", "").replace("-", "").trim();
			et_phone_number.setText(phone);

			// 将回传的数据做本地sp存储
			PrefUtils.putString(this, ConstantValue.CONTACT_PHONE, phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void showNextPage() {

		String phone = et_phone_number.getText().toString();

		// 在sp存储了相关联系人以后才可以跳转到下一页操作

		if (!TextUtils.isEmpty(phone)) {
			Intent intent = new Intent(getApplicationContext(),
					Setup4Activity.class);
			startActivity(intent);

			finish();

			// 如果现在是输入电话号码，则需要去保存
			PrefUtils.putString(this, ConstantValue.CONTACT_PHONE, phone);

			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);

		} else {
			ToastUtil.show(this, "请输入电话号码");
		}
	}

	@Override
	protected void showPrePage() {

		Intent intent = new Intent(getApplicationContext(),
				Setup2Activity.class);
		startActivity(intent);

		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
