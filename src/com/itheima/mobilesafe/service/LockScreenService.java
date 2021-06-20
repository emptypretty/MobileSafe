package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.itheima.mobilesafe.engine.ProcessInfoProvider;

public class LockScreenService extends Service {
	private InnerReceiver mInnerReceiver;

	@Override
	public void onCreate() {
		// 注册锁屏的广播接受者
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		super.onCreate();
	}

	class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 清理进程(ProcessInfoProvider)
			ProcessInfoProvider.killProcess(context);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (mInnerReceiver != null) {
			unregisterReceiver(mInnerReceiver);
		}
		super.onDestroy();
	}
}
