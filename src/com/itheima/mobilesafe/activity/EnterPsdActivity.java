package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ToastUtil;

public class EnterPsdActivity extends Activity {
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_psd);

		TextView tv_name = (TextView) findViewById(R.id.tv_name);
		ImageView iv_icon = (ImageView) findViewById(R.id.iv_icon);
		final EditText et_psd = (EditText) findViewById(R.id.et_psd);
		Button bt_submit = (Button) findViewById(R.id.bt_submit);

		packageName = getIntent().getStringExtra("packageName");
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(
					packageName, 0);
			Drawable icon = applicationInfo.loadIcon(pm);
			String name = applicationInfo.loadLabel(pm).toString();

			tv_name.setText(name);
			iv_icon.setBackgroundDrawable(icon);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// 在点击按钮后去判断输入密码是否成功
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String psd = et_psd.getText().toString();
				if (!TextUtils.isEmpty(psd)) {
					if (psd.equals("123")) {
						// 验证成功,使用当前(计算器,日历)应用
						finish();
						// 发送一条广播(包名数据),告知看门狗不要再去校验此应用(包名)
						Intent intent = new Intent(
								"com.itheima.mobilesafe.SKIP_WATCH");
						intent.putExtra("packageName", packageName);
						sendBroadcast(intent);

					} else {
						ToastUtil.show(getApplicationContext(), "输入密码错误");
					}
				} else {
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
	}

	// 点击回退按钮的时候,需要去跳转到手机桌面应用
	@Override
	public void onBackPressed() {
		// 回退按钮被点中时候,调用的方法,隐式意图开启此应用
		Intent intent = new Intent(Intent.ACTION_MAIN);
		// 桌面应用的一个category
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);

		super.onBackPressed();
	}
}
