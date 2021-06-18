package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;

public class BootReceiver extends BroadcastReceiver {

	private static final String tag = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(tag, "重启手机成功了并且监听到了相应的广播");

		// 1.获取开机后手机的sim卡的序列号
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber() + "xxx";

		// 2.sp中存储的序列卡号
		String sim_number = PrefUtils.getString(context,
				ConstantValue.SIM_SERIAL_NUMBER, "");

		// 3，比对不一致

		if (!simSerialNumber.equals(sim_number)) {
			// 4.发送短信给选中的联系人号码
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("5554", null, "sim change!!!", null,
					null);
		}

	}
}
