package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.Md5Util;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.ToastUtil;

public class HomeActivity extends Activity {
	public static final String HAS_SHORT_CUT = "has_short_cut";
	private GridView gv_home;
	private String[] mTitleStr;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initUI();
		initData();

	}

	private void initUI() {
		// TODO Auto-generated method stub
		gv_home = (GridView) findViewById(R.id.gv_home);
	}

	private void initData() {
		// TODO Auto-generated method stub
		mTitleStr = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mDrawableIds = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings

		};

		gv_home.setAdapter(new MyAdapter());
		// 注册九宫格单个条目点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				switch (position) {
				case 0:
					// 开启对话框
					showDialog();

					break;
				case 7:
					startActivity(new Intent(getApplicationContext(),
							AToolActivity.class));
					break;
				case 8:

					startActivity(new Intent(getApplicationContext(),
							SettingActivity.class));
					break;

				default:
					break;
				}

			}

		});
	}

	/**
	 * 
	 */
	protected void showDialog() {
		// 判断本地是否有存储密码（sp，字符串）
		String psd = PrefUtils.getString(this, ConstantValue.SET_PSD, "");
		if (TextUtils.isEmpty(psd)) {
			showSetPsdDialog();
		} else {
			showConbfirmPsdDialog();
		}
		// 初始设置密码对话框
		// 确认密码对话框

	}

	/**
	 * 显示确认密码的对话框
	 */
	private void showConbfirmPsdDialog() {
		// TODO Auto-generated method stub

		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		final View view = View.inflate(getApplicationContext(),
				R.layout.dialog_confirm_psd, null);
		dialog.setView(view);
		dialog.show();

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et_set_psd = (EditText) view
						.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);

				String confirmPsd = et_confirm_psd.getText().toString();

				if (!TextUtils.isEmpty(confirmPsd)) {
					// 将存储在sp中32位的密码，获取出来，然后将输入的密码同样进行MD5，然后与sp中存储密码对比
					String psd = PrefUtils.getString(getApplicationContext(),
							ConstantValue.SET_PSD, "");
					// 进入应用手机防盗模块
					if (psd.equals(Md5Util.encoder(confirmPsd))) {
						// 进入应用手机防盗模块，开启一个新的activity
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面以后需要去隐藏
						dialog.dismiss();

					} else {
						ToastUtil.show(getApplicationContext(), "密码输入有误");
					}
				} else {
					// 提示用户密码输入有为空的情况
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	/**
	 * 显示设置密码的对话框
	 */
	private void showSetPsdDialog() {
		// TODO Auto-generated method stub
		// 因为需要自己去定义对话框的展示样式，所以需要调用dialog，setView（view）
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		final View view = View.inflate(getApplicationContext(),
				R.layout.dialog_set_psd, null);
		dialog.setView(view);
		dialog.show();

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et_set_psd = (EditText) view
						.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);

				String psd = et_set_psd.getText().toString();
				String confirmPsd = et_confirm_psd.getText().toString();

				if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
					// 进入应用手机防盗模块
					if (psd.equals(confirmPsd)) {
						// 进入应用手机防盗模块，开启一个新的activity
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面以后需要去隐藏
						dialog.dismiss();

						PrefUtils.putString(getApplicationContext(),
								ConstantValue.SET_PSD, Md5Util.encoder(psd));

					} else {
						ToastUtil.show(getApplicationContext(), "密码输入有误");
					}
				} else {
					// 提示用户密码输入有为空的情况
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// 条目的总数 文字数组 == 图片数组
			return mTitleStr.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mDrawableIds[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View view = View.inflate(getApplicationContext(),
					R.layout.gridview_item, null);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			tv_title.setText(mTitleStr[arg0]);
			iv_icon.setBackgroundResource(mDrawableIds[arg0]);
			return view;
		}

	}

}
