package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.AddressDao;

public class QueryAddressActivity extends Activity {

	private TextView et_phone;
	private Button bt_query;
	private TextView tv_query_result;
	private String mAddress;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 4.控件使用查询结果
			tv_query_result.setText(mAddress);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);

		/*
		 * // 测试代码
		 * 
		 * AddressDao.getAddress("13000206556");
		 */

		initUI();
	}

	private void initUI() {
		// TODO Auto-generated method stub
		et_phone = (EditText) findViewById(R.id.et_phone);
		bt_query = (Button) findViewById(R.id.bt_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);

		// 1.点查询功能，注册按钮的点击事件
		bt_query.setOnClickListener(new OnClickListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phone = et_phone.getText().toString();

				if (!TextUtils.isEmpty(phone)) {
					// 2.查询是耗时操作，开启子线程
					query(phone);
				} else {
					// 抖动

					Animation shake = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);

					// 自定义插补器
					/*
					 * shake.setInterpolator(new Interpolator() {
					 * 
					 * // y = 2x + 1
					 * 
					 * @Override public float getInterpolation(float input) { //
					 * TODO Auto-generated method stub return 0; } });
					 */
					et_phone.startAnimation(shake);

					// 手机振动效果（vibrator 振动）
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					// 振动毫秒值
					vibrator.vibrate(2000);
					// 规律振动（振动规则（不震动时间，震动时间，不震动时间，震动时间.....），重复次数）
					vibrator.vibrate(new long[] { 2000, 5000, 2000, 5000 }, -1);
				}

			}
		});

		// 5.实时查询（监听输入框文本的变化）

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String phone = et_phone.getText().toString();
				query(phone);

			}
		});
	}

	/**
	 * 耗时操作 获取电话号码归属地
	 * 
	 * @param phone
	 *            查询电话号码
	 */
	protected void query(final String phone) {
		// TODO Auto-generated method stub

		new Thread() {
			@Override
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				// 3.消息机制，告知主线程查询结果，可以去使用查询结果
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

}
