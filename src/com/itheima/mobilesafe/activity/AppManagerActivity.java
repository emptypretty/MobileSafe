package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.ToastUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

	private List<AppInfo> mAppInfoList;
	private ListView lv_app_list;
	private MyAdapter mAdapter;

	// 用户应用集合
	private ArrayList<AppInfo> mCustomerList;
	// 系统应用集合
	private ArrayList<AppInfo> mSystemList;
	// 选中条目的对象
	private AppInfo mAppInfo;

	private TextView tv_app_title;
	private PopupWindow mPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (mAdapter == null) {
				mAdapter = new MyAdapter();
				lv_app_list.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}

		};
	};

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCustomerList.size() + mSystemList.size() + 2;
		}

		// 告知listview现在有两种条目类型
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		// 告知每一个索引当前条目类型
		/*
		 * 返回 0 灰色条目 返回 1 图片+文字条目
		 */
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				// 返回灰色条目的状态码
				return 0;
			} else {
				// 图片+文字条目的状态码
				return 1;
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				// 灰色条目只显示文字,不能去使用appInfo集合中数据
				return null;
			} else {
				// 0,1,2,3,4,5...........
				if (position < mCustomerList.size() + 1) {
					// 使用用户应用的集合
					return mCustomerList.get(position - 1);
				} else {
					// 系统应用的集合(索引-用户应用的条目总数-灰色条目占用的两个条目)
					return mSystemList.get(position - mCustomerList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 处理灰色条目
			// 处理图片+文字条目
			// convertView是如何复用的
			// 维护两种类型的convertView1 灰色条目复用convertView1
			// 图片+文字convertView2 图片+文字复用convertView2

			int mode = getItemViewType(position);

			if (mode == 0) {
				// 构建灰色条目
				ViewTitleHolder holder = null;
				// 1,判断是否有可以复用的convertView
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_app_des, null);
					// 2,复用holder,减少findViewById次数
					holder = new ViewTitleHolder();
					// 3,查找item中控件操作
					holder.tv_app_des = (TextView) convertView
							.findViewById(R.id.tv_app_des);
					// 4,给convertView设置tag,让系统存储holder对象
					convertView.setTag(holder);
				} else {
					// 5,复用convertView同时,需要去复用holder
					holder = (ViewTitleHolder) convertView.getTag();
				}
				// 6,给控件赋值
				if (position == 0) {
					holder.tv_app_des.setText("用户应用(" + mCustomerList.size()
							+ ")");
				} else {
					holder.tv_app_des.setText("系统应用(" + mSystemList.size()
							+ ")");
				}
				// 7,返回一个条目的view对象
				return convertView;
			} else {
				ViewHolder holder = null;
				// 1,判断是否有可以复用的convertView
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_app_item, null);
					// 2,复用holder,减少findViewById次数
					holder = new ViewHolder();
					// 3,查找item中控件操作
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tv_path = (TextView) convertView
							.findViewById(R.id.tv_path);
					// 4,给convertView设置tag,让系统存储holder对象
					convertView.setTag(holder);
				} else {
					// 5,复用convertView同时,需要去复用holder
					holder = (ViewHolder) convertView.getTag();
				}
				// 6,给控件赋值
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				if (getItem(position).isSdCard) {
					holder.tv_path.setText("sd卡应用");
				} else {
					holder.tv_path.setText("手机应用");
				}
				// 7,返回一个条目的view对象
				return convertView;
			}
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;
	}

	static class ViewTitleHolder {
		TextView tv_app_des;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_app_manager);

		initTitle();

		initList();

	}

	private void initList() {
		// TODO Auto-generated method stub

		lv_app_list = (ListView) findViewById(R.id.lv_app_list);

		getAppInfoData();
		lv_app_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 1,就是listview对象
				// 2,firstVisibleItem第一个可见的条目索引
				// 3,当前可见条目的总数
				// 4,totalItemCount所有条目总数
				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem >= mCustomerList.size() + 1) {
						// 让顶部描述标题,去描述系统应用
						tv_app_title.setText("系统应用(" + mSystemList.size() + ")");
					} else {
						// 让顶部描述标题,去描述用户应用
						tv_app_title.setText("用户应用(" + mCustomerList.size()
								+ ")");
					}
				}
			}
		});

		lv_app_list.setOnItemClickListener(new OnItemClickListener() {
			// view点中listview中条目对应对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// position点中条目在listview中的索引
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					// 根据索引选中条目,获取填充条目的对象
					if (position < mCustomerList.size() + 1) {
						// 使用用户应用的集合
						mAppInfo = mCustomerList.get(position - 1);
					} else {
						// 系统应用的集合(索引-用户应用的条目总数-灰色条目占用的两个条目)
						mAppInfo = mSystemList.get(position
								- mCustomerList.size() - 2);
					}
					showPopupWindow(view);
				}
			}
		});
	}

	protected void showPopupWindow(View listItemView) {
		// 放置在popupWindow中的view
		View view = View.inflate(this, R.layout.popupwindow_view, null);

		TextView tv_uninstall = (TextView) view.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) view.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) view.findViewById(R.id.tv_share);

		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);

		// 在弹出popupWindow时候添加相应的动画效果(缩放,透明不透明)

		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		// 设置动画的执行时长
		alphaAnimation.setDuration(500);
		// 保留动画执行结束后的状态
		alphaAnimation.setFillAfter(true);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(500);
		scaleAnimation.setFillAfter(true);

		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);

		view.startAnimation(animationSet);

		// 1,添加到popupWindow中去
		mPopupWindow = new PopupWindow(view,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		// 2,popupWindow添加一个透明背景(蓝色背景图片已经在view上有做设置)
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		// 3,将popupWindow放置到指定位置(放在哪个控件的下面,x轴偏移,y轴偏移)
		mPopupWindow
				.showAsDropDown(listItemView, 50, -listItemView.getHeight());
	}

	private void getAppInfoData() {
		new Thread() {
			public void run() {
				mAppInfoList = AppInfoProvider
						.getAppInfoList(getApplicationContext());
				// 准备用户应用集合
				mCustomerList = new ArrayList<AppInfo>();
				// 准备系统应用集合
				mSystemList = new ArrayList<AppInfo>();

				// 循环初始的集合,划分系统应用和用户应用
				for (AppInfo appInfo : mAppInfoList) {
					if (!appInfo.isSystem) {
						// 用户应用
						mCustomerList.add(appInfo);
					} else {
						// 系统应用
						mSystemList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initTitle() {
		// 1.获取磁盘可用大小，磁盘路径
		String path = Environment.getDataDirectory().getAbsolutePath();

		// 2.获取sd卡可用大小，sd卡路径

		String sdPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		// 3,.获取以上两个路径下文件夹的可用大小()

		String memoryAvailSpace = Formatter.formatFileSize(this,
				getAvailSpace(path));
		String sdMemoryAvailSpace = Formatter.formatFileSize(this,
				getAvailSpace(sdPath));
		TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);

		tv_memory.setText("磁盘可用:" + memoryAvailSpace);
		tv_sd_memory.setText("sd卡可用:" + sdMemoryAvailSpace);

		tv_app_title = (TextView) findViewById(R.id.tv_app_title);
	}

	/**
	 * int 最大只能代表2个G ，所以改为long
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
		// 获取可用磁盘大小类
		StatFs statFs = new StatFs(path);
		// 获取可用区块的个数
		long count = statFs.getAvailableBlocks();
		// 获取区块的大小
		long size = statFs.getBlockSize();
		// 区块的大小*可用区块的个数 == 可用空间大小
		return count * size;
	}

	@Override
	public void onClick(View v) {
		// 点中某一个条目以后,隐藏窗体
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
		switch (v.getId()) {
		case R.id.tv_uninstall:
			uninstall();
			break;
		case R.id.tv_start:
			start();
			break;
		case R.id.tv_share:
			// 第三方平台分享,shareSDK(集成市面上的几乎所有的开发平台),
			// 调用微信api(文档
			// http://www.weixin.com/fav.do?content="我今天吃鱼了"&),发送请求给微信的服务器,微信将此数据更新至朋友圈

			// 开启一个当前手机上可以对外发送文本的应用,短信
			share();
			break;
		}
	}

	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "分享一个很火的应用," + mAppInfo.name);
		startActivity(intent);
	}

	private void start() {
		// 每一个应用,桌面上会有启动图标
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(mAppInfo.packageName);
		if (intent != null) {
			startActivity(intent);
		} else {
			ToastUtil.show(this, "此应用无法开启");
		}
	}

	private void uninstall() {
		// 查看卸载activity源码清单配置文件(action,category,data(packageName))
		if (mAppInfo.isSystem) {
			ToastUtil.show(this, "系统应用不能卸载");
		} else {
			// 开启卸载activity,做卸载操作
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + mAppInfo.packageName));
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 刷新数据,
		getAppInfoData();
		super.onActivityResult(requestCode, resultCode, data);
	}

}
