package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.AddressDao;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;

public class AddressService extends Service {

	public static final String tag = "AddressService";
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;

	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mViewToast;
	private WindowManager mWindowManager;
	private String mAddress;
	private TextView tv_toast;
	private int[] mDrawableIds;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	private int mScreenWidth;
	private int mScreenHeight;
	private InnerOutCallReceiver mInnerOutCallReceiver;

	@Override
	public void onCreate() {
		// 第一次开启服务以后，就需要去管理吐司的显示
		// 电话状态的监听（服务开启的时候，需要去做监听，关闭的时候电话状态就不需要监听）
		// 1.电话管理者对象
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		// 获取窗体对象
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
		mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

		// 监听播出电话的广播过滤条件(权限)
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

		// 创建广播接受者
		mInnerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(mInnerOutCallReceiver, intentFilter);

		super.onCreate();
	}

	class InnerOutCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收到此广播后，需要侠士自定义的吐司，显示播出归属地
			String phone = getResultData();
			showToast(phone);
		}
	}

	class MyPhoneStateListener extends PhoneStateListener {
		// 手动重写，电话状态发生改变会触发的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				// 空闲状态，没有任何活动
				Log.i(tag, "挂断电话，空闲了............");
				// 挂断电话的时候窗体需要移除吐司
				if (mWindowManager != null && mViewToast != null) {
					mWindowManager.removeView(mViewToast);
				}

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// 摘机状态，至少有个电话活动，该活动或是拨打（dialing）或是通话
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// 响铃（展示吐司）
				Log.i(tag, "响铃了............");

				showToast(incomingNumber);
				break;
			default:
				break;
			}

			super.onCallStateChanged(state, incomingNumber);

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void showToast(String incomingNumber) {
		// TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(), incomingNumber, 0).show();

		final WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;

		// 在响铃的时候显示吐司 ，和电话类型一致
		params.setTitle("Toast");
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// 指定吐司的所在位置(将吐司指定在左上角)
		params.gravity = Gravity.LEFT + Gravity.TOP;
		// 吐司显示效果（吐司布局文件），xml-->view(吐司)，将吐司挂在到windowManager窗体上
		mViewToast = View.inflate(this, R.layout.toast_view, null);

		tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

		mViewToast.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				// 触摸过程中调用的方法,event.getAction()当前触发的动作,去判断触发了什么事件
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 按下(一次)
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 移动(连续过程,触发多次)
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					// 计算每一个移动过程中,x轴和y轴坐标上的一个差值,此差值需要作用在可拖拽的控件上
					int disX = moveX - startX;
					int disY = moveY - startY;

					params.x = params.x + disX;
					params.y = params.y + disY;

					// 容错处理

					if (params.x < 0) {
						params.x = 0;
					}

					if (params.y < 0) {
						params.y = 0;
					}

					if (params.x > mScreenWidth - mViewToast.getWidth()) {
						params.x = mScreenWidth - mViewToast.getWidth();
					}

					if (params.y > mScreenHeight - mViewToast.getHeight() - 22) {
						params.y = mScreenHeight - mViewToast.getHeight() - 22;
					}

					// 告知窗体吐司需要按照手势的移动，去做位置的更新
					mWindowManager.updateViewLayout(mViewToast, params);

					// 移动过后的位置,是下一次移动的初始位置,所以重新获取其初始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					PrefUtils.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, params.x);
					PrefUtils.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, params.y);
					break;
				}
				// true 响应拖拽的事件
				return true;
			}
		});

		// 读取sp中存储吐司位置的左上角x,y坐标值
		params.x = PrefUtils.getInt(getApplicationContext(),
				ConstantValue.LOCATION_X, 0);
		params.y = PrefUtils.getInt(getApplicationContext(),
				ConstantValue.LOCATION_Y, 0);

		// 从sp中获取色值文字的索引，匹配图片，用作展示
		mDrawableIds = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		int toastStyle = PrefUtils.getInt(getApplicationContext(),
				ConstantValue.TOAST_STYLE_INDEX, 0);

		tv_toast.setBackgroundResource(mDrawableIds[toastStyle]);

		// 在窗体上挂在一个View（权限）
		mWindowManager.addView(mViewToast, params);

		// 获取到了来电号码以后，需要做来电号码查询

		query(incomingNumber);

	}

	private void query(final String incomingNumber) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				mAddress = AddressDao.getAddress(incomingNumber);
				mHandler.sendEmptyMessage(0);

			};
		}.start();

	}

	@Override
	public void onDestroy() {
		// 取消对电话状态的监听(开启服务的时候监听电话的对象)
		if (mTM != null && mPhoneStateListener != null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}

		if (mInnerOutCallReceiver != null) {
			unregisterReceiver(mInnerOutCallReceiver);

		}
		super.onDestroy();
	}
}
