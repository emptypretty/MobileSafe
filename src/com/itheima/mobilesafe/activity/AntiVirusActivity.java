package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.VirusDao;
import com.itheima.mobilesafe.utils.Md5Util;

public class AntiVirusActivity extends Activity {
	protected static final int SCANNING = 100;
	protected static final int SCAN_FINISH = 101;
	protected static final String tag = "AntiVirusActivity";

	private ImageView iv_scan;
	private TextView tv_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_parent;
	private int mIndex = 0;

	// 病毒Scan对象集合
	private List<Scan> mScanVirusList;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:
				Scan scan = (Scan) msg.obj;
				tv_name.setText(scan.name);
				TextView textView = new TextView(getApplicationContext());
				if (scan.isVirus) {
					// 是病毒
					textView.setText("发现病毒:" + scan.name);
					textView.setTextColor(Color.RED);
				} else {
					// 不是病毒
					textView.setText("扫描安全:" + scan.name);
					textView.setTextColor(Color.BLACK);
				}
				// 需要将其添加在上一个扫描应用的上方,便于用户观察
				ll_parent.addView(textView, 0);
				break;
			case SCAN_FINISH:
				// 结束动画
				tv_name.setText("扫描完成");
				// 正在执行的动画做一个清理,终止
				iv_scan.clearAnimation();
				// 扫描完成后,卸载应用的操作
				showUninstallActvity(mScanVirusList);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);

		initUI();
		// 初始化动画的方法
		initAnimation();
		// 准备数据
		initData();
	}

	protected void showUninstallActvity(List<Scan> virusList) {
		for (Scan scan : virusList) {
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + scan.packageName));
			startActivity(intent);
		}
	}

	private void initAnimation() {
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(500);
		// 一直去做执行,无限循环
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		iv_scan.startAnimation(rotateAnimation);
	}

	private void initData() {
		new Thread() {
			public void run() {
				// 获取病毒md5码所在的集合
				List<String> virusList = VirusDao.getVirusList();
				// 创建记录病毒的集合

				mScanVirusList = new ArrayList<Scan>();

				// 创建记录病毒的集合
				List<Scan> scanList = new ArrayList<Scan>();

				// 获取当前手机安装上的应用,获取每一个应用的签名的md5码
				// 1,获取包管理者对象
				PackageManager pm = getPackageManager();
				// 2,获取有签名的应用+没有卸载干净应用相关信息()
				List<PackageInfo> installedPackages = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);
				pb_bar.setMax(installedPackages.size());

				// 3,循环遍历带有签名应用的集合
				for (PackageInfo packageInfo : installedPackages) {
					Scan scan = new Scan();
					// 4,获取签名文件的数组
					Signature[] signatures = packageInfo.signatures;
					// 5,数组中的第0为,就是应用的签名文件
					String charsString = signatures[0].toCharsString();
					// 6,对此charsString做md5加密过程(encoder 32位数)
					String encoder = Md5Util.encoder(charsString);

					// 7,设置应用的包名
					scan.packageName = packageInfo.packageName;

					// 8,判断当前的应用是否为病毒
					if (virusList.contains(encoder)) {
						scan.isVirus = true;
						mScanVirusList.add(scan);
					} else {
						scan.isVirus = false;
					}

					// 9,获取应用名称
					scan.name = packageInfo.applicationInfo.loadLabel(pm)
							.toString();
					// 10,应用的集合去添加数据(不管带不带毒)
					scanList.add(scan);

					Log.i(tag, "encoder = " + encoder);
					Log.i(tag, "scan.name = " + scan.name);
					Log.i(tag,
							"我是一条华丽丽的分割线 ======================================== ");

					mIndex++;
					// 11,扫描完了一个应用以后,需要告知进度条递增
					pb_bar.setProgress(mIndex);

					// 12,睡眠
					try {
						Thread.sleep(50 + new Random().nextInt(50));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 13,告知主线程需要去更新UI
					Message msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = scan;
					mHandler.sendMessage(msg);
				}

				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	class Scan {
		boolean isVirus;
		String packageName;
		String name;
	}

	private void initUI() {
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_parent = (LinearLayout) findViewById(R.id.ll_parent);
	}
}
