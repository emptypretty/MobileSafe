package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;

public class ToastLocationActivity extends Activity {
	protected static final String tag = "ToastLocationActvity";
	private Button bt_top;
	private Button bt_bottom;
	private ImageView iv_drag;
	private WindowManager mWM;
	private int mScreenWidth;
	private int mScreenHeight;
	private long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		initUI();
	}

	private void initUI() {
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

		// 获取屏幕宽度
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();

		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);
		iv_drag = (ImageView) findViewById(R.id.iv_drag);

		// 创建一个imageView显示规则参数所在的对象
		RelativeLayout.LayoutParams params = (LayoutParams) iv_drag
				.getLayoutParams();

		// 读取上一次在sp中存储的(x,y)控件左上角位置,赋值给此控件,让其在此位置显示
		params.leftMargin = PrefUtils.getInt(this, ConstantValue.LOCATION_X, 0);
		params.topMargin = PrefUtils.getInt(this, ConstantValue.LOCATION_Y, 0);

		if (params.topMargin > mScreenHeight / 2) {
			// 底部的描述隐藏
			bt_bottom.setVisibility(View.INVISIBLE);
			// 顶部的描述显示
			bt_top.setVisibility(View.VISIBLE);
		} else {
			// 底部的描述显示
			bt_bottom.setVisibility(View.VISIBLE);
			// 顶部的描述隐藏
			bt_top.setVisibility(View.INVISIBLE);
		}

		// 要将此规则,告知图片控件,设置修改过后的位置给次imageView
		iv_drag.setLayoutParams(params);

		iv_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(tag, "响应点击事件.................");
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);

				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (500 >= (SystemClock.uptimeMillis() - mHits[0])) {
					// 让图片在屏幕中居中
					iv_drag.layout(mScreenWidth / 2 - iv_drag.getWidth() / 2,
							mScreenHeight / 2 - iv_drag.getHeight() / 2,
							mScreenWidth / 2 + iv_drag.getWidth() / 2,
							mScreenHeight / 2 + iv_drag.getHeight() / 2);
					PrefUtils.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					PrefUtils.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
				}
			}
		});

		// 对imageView拖拽过程的事件监听
		iv_drag.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
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

					// 修改可拖拽图片的位置(获取其初始位置,然后加上x轴,y轴移动差值)
					int left = iv_drag.getLeft() + disX;
					int top = iv_drag.getTop() + disY;
					int right = iv_drag.getRight() + disX;
					int bottom = iv_drag.getBottom() + disY;

					// 当前的图片不能移出屏幕,所以需要对左上右下的坐标做限制
					// 左侧位置小于0,坐标无效
					if (left < 0) {
						return true;
					}
					// 右侧坐标大于屏幕宽度,坐标无效
					if (right > mScreenWidth) {
						return true;
					}

					if (top < 0) {
						return true;
					}

					// 22个像素通知栏初略高度,dp和px在手机上有不同的转换关系,智慧北京,屏幕适配
					// 1dp = 0.75px
					// 1dp = 1px
					// 1dp = 1.5px
					// 1dp = 2px
					// 1dp = 3px
					if (bottom > mScreenHeight - 22) {
						return true;
					}

					// 在移动过程中,如果吐司图片高度超过一半屏幕高度,则让描述文字底部隐藏,顶部显示,反之同上
					if (top > mScreenHeight / 2) {
						// 底部的描述隐藏
						bt_bottom.setVisibility(View.INVISIBLE);
						// 顶部的描述显示
						bt_top.setVisibility(View.VISIBLE);
					} else {
						// 底部的描述显示
						bt_bottom.setVisibility(View.VISIBLE);
						// 顶部的描述隐藏
						bt_top.setVisibility(View.INVISIBLE);
					}

					// 告知图片控件,要安装上诉的规则去显示图片
					iv_drag.layout(left, top, right, bottom);

					// 移动过后的位置,是下一次移动的初始位置,所以重新获取其初始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 抬起(一次),记录移动完成后,手势抬起的坐标,作为下一次打开此界面的初始坐标
					// 只需要记录控件的左上角的(x,y)轴坐标就可以记录控件的所在屏幕位置
					PrefUtils.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					PrefUtils.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
					break;
				}
				// 如果仅仅响应按下移动抬起,在此处返回true,不返回true不能响应事件
				// 如果此控件既要响应拖拽,又要响应点击事件,必须将其onTouch方法的返回值结果,设置成false
				// 事件分发,事件响应的规则(dispatchTouchEvent,onTouch,onTouchEvent)
				return false;
			}
		});
	}
}
