package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// 监听手势的移动
						if (e1.getX() - e2.getX() > 0) {
							// 调用子类的下一页方法
							// 在第一个界面上的时候，就跳转到第二个界面
							// 在第二个界面上的时候，就跳转到第一个界面
							// .........
							showNextPage();
						}

						if (e1.getX() - e2.getX() < 0) {
							// 调用子类的上一页方法

							// 在第一个界面上的时候，无响应
							// 在第二个界面上的时候，就跳转到第三个界面
							// .........

							showPrePage();
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	// 下一页的抽象方法，由子类决定具体跳转到那个界面
	protected abstract void showNextPage();

	// 上一页的抽象方法，由子类决定具体跳转到那个界面
	protected abstract void showPrePage();

	// 点击下一页按钮的时候，根据子类的showNextPage方法做相应跳转
	public void nextPage(View view) {
		showNextPage();
	}

	// 点击上一页按钮的时候，根据子类的showNextPage方法做相应跳转
	public void prePage(View view) {
		showPrePage();
	}

	// 1，监听屏幕上响应的事件类型（按下（1次），移动（多次），抬起（1次））
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 3，通过手势处理类，接收多种类型的事件，用作处理的方法
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);

	}
}
