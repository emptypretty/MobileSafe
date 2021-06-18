package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.service.LocationService;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;

public class SmsReceiver extends BroadcastReceiver {
	private static final String tag = null;
	private SmsMessage sms;
	private String messageBody;
	private String originatingAddress;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 1.判断是否开启了防盗保护
		boolean open_protect = PrefUtils.getBoolean(context,
				ConstantValue.OPEN_PROTECT, false);
		if (open_protect) {
			// 2.获取短信内容

			Object[] objects = (Object[]) intent.getExtras().get("pdus");

			// 3.循环遍历短信的过程
			for (Object object : objects) {
				// 4获取短信对象
				sms = SmsMessage.createFromPdu((byte[]) object);
				// 5.获取短信对象的基本信息
				originatingAddress = sms.getOriginatingAddress();
				messageBody = sms.getMessageBody();

				// 6.判断是否包含播放音乐的关键字
				if (messageBody.contains("#*alarm*#")) {
					// 7,播放音乐（准备音乐，MediaPlayer）

					MediaPlayer mediaPlayer = MediaPlayer.create(context,
							R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();

				}

				if (messageBody.contains("#*location*#")) {
					// 8.开启获取位置服务
					context.startService(new Intent(context,
							LocationService.class));
				}

				if (messageBody.contains("#*lockscreen*#")) {
					Log.i(tag, "屏幕锁屏");
				}

				if (messageBody.contains("#*wipedata*#")) {
					Log.i(tag, "清除数据");
				}
			}
		}

	}
}
