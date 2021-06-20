package com.itheima.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.itheima.mobilesafe.activity.EnterPsdActivity;
import com.itheima.mobilesafe.db.dao.AppLockDao;

public class WatchDogService extends Service {
	protected static final String tag = "WatchDogService";
	private AppLockDao mDao;
	private boolean isWatch;
	private InnerReceiver mInnerReceiver;
	private String skipPackageName;
	private List<String> packageList;
	private MyContentObserver mContentObserver;

	@Override
	public void onCreate() {
		// 创建一个死循环,此循环要去时刻监听,手机开启的应用,如果手机开启的应用在已加锁的列表中,将其拦截下来(弹出输入密码的界面)
		mDao = AppLockDao.getInstance(getApplicationContext());
		// 通过变量让监听开启应用的过程和服务生命周期绑定
		isWatch = true;
		watch();

		// 动态注册广播,去接受输入密码的界面,传递过来要去跳过检测的包名
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.itheima.mobilesafe.SKIP_WATCH");

		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);

		// 观察数据改变

		mContentObserver = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(
				Uri.parse("content://com.itheima.mobilesafe/applock/change"),
				true, mContentObserver);

		super.onCreate();
	}

	class MyContentObserver extends ContentObserver {

		public MyContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			// 获取改变后的数据库中的数据,并且将其封装到集合中
			packageList = mDao.findAll();
			super.onChange(selfChange);
		}
	}

	class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 通过意图获取数据
			skipPackageName = intent.getStringExtra("packageName");
		}
	}

	private void watch() {
		// 此包名如果存在于已加锁的包名数据库中,则说明此应用在开启过程中要去拦截
		packageList = mDao.findAll();
		new Thread() {
			public void run() {
				while (isWatch) {
					// 获取现在正在开启的应用,包名和程序锁中应用的包名做匹配,如果一致,则需要拦截
					// 1,获取activity管理者对象
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					// 2,获取当前正在前台运行的任务栈(任务栈---->第一个activity就是当前开启的应用的activity---->应用包名)
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					// 3,获取唯一的一个任务栈对象,获取其栈顶的activity,返回相应的一个组件
					ComponentName componentName = runningTasks.get(0).topActivity;
					// 4,从组件中获取上一步activity所在应用的包名
					String packageName = componentName.getPackageName();

					if (packageList.contains(packageName)) {
						if (!packageName.equals(skipPackageName)) {
							// 拦截,开启一个新的activity
							Intent intent = new Intent(getApplicationContext(),
									EnterPsdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packageName", packageName);
							startActivity(intent);
						}
					}
					try {
						// 如果不在此处睡眠,会导致手机性能消耗较大
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		// 不要再循环监听
		isWatch = false;
		// 注销广播接受者
		if (mInnerReceiver != null) {
			unregisterReceiver(mInnerReceiver);
		}
		// 注销内容观察者
		if (mContentObserver != null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		super.onDestroy();
	};
}
