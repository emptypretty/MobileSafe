package com.itheima.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class ClearCacheActivity extends Activity {
	protected static final int UPDATE_PROGRESS = 100;
	protected static final int SCAN_FINISH = 101;
	protected static final int UPDATE_CACHE = 102;
	private static final String tag = "ClearCacheActivity";

	private Button bt_clear_cache;
	private TextView tv_name;
	private LinearLayout ll_parent;
	private ProgressBar pb_bar;
	private int index = 0;

	private PackageManager mPM;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_PROGRESS:
				tv_name.setText("扫描应用:" + (String) msg.obj);
				break;
			case SCAN_FINISH:
				tv_name.setText("扫描完成");
				break;
			case UPDATE_CACHE:
				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				// 一旦检查到带有缓存的应用,就需要将其对应条目添加到线性布局中
				View view = View.inflate(getApplicationContext(),
						R.layout.cache_item_view, null);

				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_cache_size = (TextView) view
						.findViewById(R.id.tv_cache_size);
				ImageView iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);

				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_name.setText(cacheInfo.name);
				tv_cache_size.setText("缓存大小:"
						+ Formatter.formatFileSize(getApplicationContext(),
								cacheInfo.cacheSize));

				iv_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 删除一个应用缓存的方法,PackageManager,并且此方法隐藏方法
						// public abstract void
						// deleteApplicationCacheFiles(String packageName,
						// IPackageDataObserver observer);

						// 系统权限
						/*
						 * try { Class<?> clazz =
						 * Class.forName("android.content.pm.PackageManager");
						 * Method method =
						 * clazz.getMethod("deleteApplicationCacheFiles",
						 * String.class, IPackageDataObserver.class);
						 * 
						 * method.invoke(mPM, cacheInfo.packageName, new
						 * IPackageDataObserver.Stub() {
						 * 
						 * @Override public void onRemoveCompleted(String
						 * packageName, boolean succeeded) throws
						 * RemoteException { Log.i(tag,
						 * "移除指定应用"+packageName+"缓存后调用过的方法"); } }); } catch
						 * (Exception e) { e.printStackTrace(); }
						 */

						// 换一种思维,调用系统的清除缓存的界面,安装要求传递参数,让用户主动点击按钮去清除缓存
						Intent intent = new Intent(
								"android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"
								+ cacheInfo.packageName));
						startActivity(intent);
					}
				});
				// 添加到线性布局中,后扫描的加到第一位,索引0
				ll_parent.addView(view, 0);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clear_cache);

		Log.i(tag, "主线程id............." + Thread.currentThread().getId());

		initUI();
		// 准备数据
		initData();
	}

	private void initData() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// 1,获取包管理者对象

				mPM = getPackageManager();

				// 2,获取手机中没有卸载掉应用
				List<PackageInfo> installedPackages = mPM
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				pb_bar.setMax(installedPackages.size());
				// 3,遍历所有的应用
				for (PackageInfo packageInfo : installedPackages) {
					String packageName = packageInfo.packageName;
					String name = packageInfo.applicationInfo.loadLabel(mPM)
							.toString();

					// 扫描过程中,获取到了有缓存的应用处理逻辑
					try {
						Class<?> clazz = Class
								.forName("android.content.pm.PackageManager");
						Method method = clazz.getMethod("getPackageSizeInfo",
								String.class, IPackageStatsObserver.class);
						method.invoke(mPM, packageName, mStatsObserver);
					} catch (Exception e) {
						e.printStackTrace();
					}

					index++;
					pb_bar.setProgress(index);

					try {
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}

					Message msg = Message.obtain();
					msg.what = UPDATE_PROGRESS;
					msg.obj = name;
					mHandler.sendMessage(msg);
				}

				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();

	}

	private void initUI() {
		bt_clear_cache = (Button) findViewById(R.id.bt_clear_cache);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_parent = (LinearLayout) findViewById(R.id.ll_parent);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);

		bt_clear_cache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 清理所有应用的缓存,
				// 手机已经由缓存占有了一定空间,但是此时管手机要Long.MAX_VALUE空间,手机是肯定没有这么大的空间去提供,
				// 则回去释放已经占有的空间
				// 隐藏方法
				// public abstract void freeStorageAndNotify(long
				// freeStorageSize, IPackageDataObserver observer);

				// 权限
				try {
					Class<?> clazz = Class
							.forName("android.content.pm.PackageManager");
					Method method = clazz.getMethod("freeStorageAndNotify",
							long.class, IPackageDataObserver.class);
					method.invoke(mPM, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									Log.i(tag, "缓存以及清理完成............."
											+ Thread.currentThread().getId());

									// 移除所有当前线性布局内部的view,通过消息机制在handlerMessage方法中去移除掉ll_parent布局,中所有的控件
									// ll_parent.removeAllViews();
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
		// 获取缓存的代码,在此方法中做调用,回调方法
		public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
			// cacheSize是SizeInfo中的字段,此字段需要获取缓存大小,单位为byte
			// 此方法运行在子线程中
			long cacheSize = stats.cacheSize;
			if (cacheSize > 0) {
				CacheInfo cacheInfo = new CacheInfo();
				cacheInfo.cacheSize = cacheSize;
				// 作为底部有缓存的的列表展示
				// 获取现在检查应用的包名
				cacheInfo.packageName = stats.packageName;
				try {
					ApplicationInfo applicationInfo = mPM.getApplicationInfo(
							stats.packageName, 0);
					// 图标
					cacheInfo.icon = applicationInfo.loadIcon(mPM);
					// 名称
					cacheInfo.name = applicationInfo.loadLabel(mPM).toString();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				Message msg = Message.obtain();
				msg.what = UPDATE_CACHE;
				msg.obj = cacheInfo;
				mHandler.sendMessage(msg);
			}
		}
	};

	class CacheInfo {
		public String name;
		public String packageName;
		public Drawable icon;
		long cacheSize;
	}
}
