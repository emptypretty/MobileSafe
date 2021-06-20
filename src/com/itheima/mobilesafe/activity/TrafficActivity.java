package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;

import com.itheima.mobilesafe.R;

public class TrafficActivity extends Activity {
	private static final String tag = "TrafficActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);

		// 获取上传流量(3G,4G自带流量,wifi)
		// 获取下载流量(3G,4G自带流量,wifi)

		// 封装流量类
		long mobileRxBytes = TrafficStats.getMobileRxBytes();// 手机使用的接受流量,下载
		// 获取手机+wifi总流量
		long totalRxBytes = TrafficStats.getTotalRxBytes();

		// 上传流量(手机+wifi)
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		// 手机上传流量
		long mobileTxBytes = TrafficStats.getMobileTxBytes();

		Log.i(tag, "手机下载流量 mobileRxBytes = " + mobileRxBytes);
		Log.i(tag, "下载流量 totalRxBytes = " + totalRxBytes);

		Log.i(tag, "手机上传流量 mobileTxBytes = " + mobileTxBytes);
		Log.i(tag, "上传流量 totalTxBytes = " + totalTxBytes);
	}
}
