package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itheima.mobilesafe.engine.ProcessInfoProvider;

public class KillProcessReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// 杀死进程
		ProcessInfoProvider.killProcess(context);
	}
}
