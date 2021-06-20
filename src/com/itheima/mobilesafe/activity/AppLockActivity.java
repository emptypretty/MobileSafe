package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.db.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;

public class AppLockActivity extends Activity {
	private LinearLayout ll_unlock, ll_lock;
	private TextView tv_unlock_des, tv_lock_des;
	private ListView lv_unlock, lv_lock;
	private Button bt_unlock, bt_lock;
	// 所有应用所在的集合
	private List<AppInfo> mAppInfoList;
	private AppLockDao mDao;
	// 数据库中查询出来的已加锁应用包名所在的集合
	private List<String> mLockPackgeList;

	// 已加锁应用集合
	private List<AppInfo> mLockAppInfoList;
	// 未加锁应用集合
	private List<AppInfo> mUnLockAppInfoList;

	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 1,设置已加锁应用的数据适配器
			mLockAdapter = new MyAdapter(true);
			lv_lock.setAdapter(mLockAdapter);

			// 2,设置未加锁应用的数据适配器
			mUnLockAdapter = new MyAdapter(false);
			lv_unlock.setAdapter(mUnLockAdapter);
		};
	};
	private TranslateAnimation mTranslateAnimation;

	// 已加锁和未加锁的应用,都用此数据适配器,当时传递的集合不一样,数据适配器在展示的时候,就会有差异
	class MyAdapter extends BaseAdapter {
		private boolean isLock;

		public MyAdapter(boolean isLock) {
			this.isLock = isLock;
		}

		@Override
		public int getCount() {
			tv_unlock_des.setText("未加锁应用:" + mUnLockAppInfoList.size());
			tv_lock_des.setText("已加锁应用:" + mLockAppInfoList.size());
			if (isLock) {
				return mLockAppInfoList.size();
			} else {
				return mUnLockAppInfoList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {
				return mLockAppInfoList.get(position);
			} else {
				return mUnLockAppInfoList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 1,复用convertView
			ViewHoler viewHoler = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_item_islock, null);
				// 2,复用holder,防止多次findViewById
				viewHoler = new ViewHoler();
				viewHoler.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				viewHoler.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				viewHoler.iv_islock = (ImageView) convertView
						.findViewById(R.id.iv_islock);

				convertView.setTag(viewHoler);
			} else {
				viewHoler = (ViewHoler) convertView.getTag();
			}
			final View animationView = convertView;
			final AppInfo appInfo = getItem(position);
			// 3,获取holder中的控件
			// 4,給上诉控件赋值
			viewHoler.iv_icon.setBackgroundDrawable(appInfo.icon);
			viewHoler.tv_name.setText(appInfo.name);
			if (isLock) {
				viewHoler.iv_islock.setBackgroundResource(R.drawable.lock);
			} else {
				viewHoler.iv_islock.setBackgroundResource(R.drawable.unlock);
			}
			viewHoler.iv_islock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 异步执行,理解为开启一个子线程
					animationView.startAnimation(mTranslateAnimation);
					// 监听动画执行完成的操作
					mTranslateAnimation
							.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
									// 动画开启的时候调用方法
								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {
									// 动画重复的时候调用方法
								}

								@Override
								public void onAnimationEnd(Animation animation) {
									// 动画结束的时候调用的方法
									if (isLock) {
										// 在已加锁的数据适配器中,点击的锁的图片
										// 1,将点中的条目包名,从数据库删除
										mDao.delete(appInfo.packageName);
										// 2,将已加锁的集合对应的当前对象,移除掉
										mLockAppInfoList.remove(appInfo);
										// 3,将未加锁的集合添加当前条目指向对象
										mUnLockAppInfoList.add(appInfo);
									} else {
										// 在未加锁的数据适配器中,点击的锁的图片
										// 1,将点中的条目包名,添加至数据库
										mDao.insert(appInfo.packageName);
										// 2,将已加锁的集合添加当前条目指向对象
										mLockAppInfoList.add(appInfo);
										// 3,将未加锁的集合删除当前条目指向的对象
										mUnLockAppInfoList.remove(appInfo);
									}
									// 4,刷新已加锁和未加锁的两个数据适配器
									mLockAdapter.notifyDataSetChanged();
									mUnLockAdapter.notifyDataSetChanged();
								}
							});
				}
			});
			// 5,返回convertView
			return convertView;
		}
	}

	static class ViewHoler {
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_islock;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		initUI();
		initData();
		initAnimation();
	}

	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnimation.setDuration(500);
	}

	private void initData() {
		new Thread() {
			public void run() {
				// 1,获取所有软件信息所在集合
				mAppInfoList = AppInfoProvider
						.getAppInfoList(AppLockActivity.this);
				// 2,将已加锁和未加锁的应用区分
				mDao = AppLockDao.getInstance(AppLockActivity.this);
				mLockPackgeList = mDao.findAll();

				mLockAppInfoList = new ArrayList<AppInfo>();
				mUnLockAppInfoList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : mAppInfoList) {
					if (mLockPackgeList.contains(appInfo.packageName)) {
						// 将此对象,归类在已加锁应用集合中
						mLockAppInfoList.add(appInfo);
					} else {
						// 归类在未加锁应用集合中
						mUnLockAppInfoList.add(appInfo);
					}
				}
				// 3,消息,告知数据适配器使用以上的集合数据了
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

		tv_unlock_des = (TextView) findViewById(R.id.tv_unlock_des);
		tv_lock_des = (TextView) findViewById(R.id.tv_lock_des);

		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_lock = (ListView) findViewById(R.id.lv_lock);

		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);

		// 在点击按钮的时候,去切换按钮的背景图片
		bt_unlock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 未加锁的按钮,选中状态
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				// 已加锁,未选中状态
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
				// 未加锁的线性布局展示
				ll_unlock.setVisibility(View.VISIBLE);
				// 已加锁的线性布局隐藏
				ll_lock.setVisibility(View.GONE);
			}
		});

		// 在点击按钮的时候,去切换按钮的背景图片
		bt_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 未加锁的按钮,未选中状态
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				// 已加锁,已选中状态
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				// 未加锁的线性布局隐藏
				ll_unlock.setVisibility(View.GONE);
				// 已加锁的线性布局展示
				ll_lock.setVisibility(View.VISIBLE);
			}
		});
	}
}
