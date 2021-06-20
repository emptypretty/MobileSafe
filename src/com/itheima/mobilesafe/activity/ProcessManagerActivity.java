package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.domain.ProcessInfo;
import com.itheima.mobilesafe.engine.ProcessInfoProvider;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.ToastUtil;

public class ProcessManagerActivity extends Activity implements OnClickListener {
	private TextView tv_process_count, tv_avail_space, tv_app_title;
	private ListView lv_list_process;
	private Button bt_select_all, bt_select_reverse, bt_clear, bt_setting;
	// 用户进程集合
	private List<ProcessInfo> mCustomerList;
	// 系统进程集合
	private List<ProcessInfo> mSystemList;

	private MyAdapter mAdapter;

	private ProcessInfo mProcessInfo;
	// 进程总数
	private int mProcessCount;
	// 剩余空间大小
	private long mAvailSpace;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mAdapter == null) {
				mAdapter = new MyAdapter();
				lv_list_process.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		};
	};
	private String mStrTotalSpace;

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			boolean isCheck = PrefUtils.getBoolean(getApplicationContext(),
					ConstantValue.IS_SYSTEM_VISABLE, false);
			if (isCheck) {
				int count = mCustomerList.size() + mSystemList.size();
				tv_process_count.setText("进程总数:" + count);
				// 显示系统进程
				return mCustomerList.size() + mSystemList.size() + 2;
			} else {
				// 隐藏系统进程
				tv_process_count.setText("进程总数:" + mCustomerList.size());
				return mCustomerList.size() + 1;
			}
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
		public ProcessInfo getItem(int position) {
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
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
					holder.tv_app_des.setText("用户进程(" + mCustomerList.size()
							+ ")");
				} else {
					holder.tv_app_des.setText("系统进程(" + mSystemList.size()
							+ ")");
				}
				// 7,返回一个条目的view对象
				return convertView;
			} else {
				ViewHolder holder = null;
				// 1,判断是否有可以复用的convertView
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_process, null);
					// 2,复用holder,减少findViewById次数
					holder = new ViewHolder();
					// 3,查找item中控件操作
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_process_name = (TextView) convertView
							.findViewById(R.id.tv_process_name);
					holder.tv_process_memsize = (TextView) convertView
							.findViewById(R.id.tv_process_memsize);
					holder.cb_box = (CheckBox) convertView
							.findViewById(R.id.cb_box);
					// 4,给convertView设置tag,让系统存储holder对象
					convertView.setTag(holder);
				} else {
					// 5,复用convertView同时,需要去复用holder
					holder = (ViewHolder) convertView.getTag();
				}
				// 6,给控件赋值
				holder.iv_icon
						.setBackgroundDrawable(getItem(position).drawable);
				holder.tv_process_name.setText(getItem(position).name);
				String strMemSize = Formatter.formatFileSize(
						getApplicationContext(), getItem(position).memSize);
				;
				holder.tv_process_memsize.setText("内存占用:" + strMemSize);

				// 7,如果现在展示的是当前手机卫士的应用

				if (getItem(position).packageName.equals(getPackageName())) {
					holder.cb_box.setVisibility(View.GONE);
				} else {
					holder.cb_box.setVisibility(View.VISIBLE);
				}
				// 根据集合中索引获取的对象中isCheck字段去处理是否选中逻辑
				holder.cb_box.setChecked(getItem(position).isCheck);
				// 8,返回一个条目的view对象
				return convertView;
			}

		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_process_name;
		TextView tv_process_memsize;
		CheckBox cb_box;
	}

	static class ViewTitleHolder {
		TextView tv_app_des;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		initTitle();
		initTitleData();
		initListData();
	}

	private void initListData() {
		getProcessData();
		lv_list_process.setOnScrollListener(new OnScrollListener() {
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
						tv_app_title.setText("系统进程(" + mSystemList.size() + ")");
					} else {
						// 让顶部描述标题,去描述用户应用
						tv_app_title.setText("用户进程(" + mCustomerList.size()
								+ ")");
					}
				}
			}
		});

		lv_list_process.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 1,灰色条目即使点击也没有效果
				if (position == 0 || position == mCustomerList.size() + 1) {
					// 灰色条目只显示文字,不能去使用appInfo集合中数据
					return;
				} else {
					// 2,当前应用(手机卫士)即使点击也没有效果
					if (position < mCustomerList.size() + 1) {
						// 使用用户应用的集合
						if (!mCustomerList.get(position - 1).packageName
								.equals(getPackageName())) {
							mProcessInfo = mCustomerList.get(position - 1);
						}
					} else {
						// 系统应用的集合(索引-用户应用的条目总数-灰色条目占用的两个条目)
						mProcessInfo = mSystemList.get(position
								- mCustomerList.size() - 2);
					}
					if (mProcessInfo != null) {
						// 业务逻辑选中状态的切换
						mProcessInfo.isCheck = !mProcessInfo.isCheck;

						// UI做选中状态的切换
						CheckBox cb_box = (CheckBox) view
								.findViewById(R.id.cb_box);
						// 修改checkBox当前状态
						cb_box.setChecked(mProcessInfo.isCheck);
					}
				}
			}
		});
	}

	private void getProcessData() {
		new Thread() {
			public void run() {
				List<ProcessInfo> processList = ProcessInfoProvider
						.getProcessList(getApplicationContext());

				mCustomerList = new ArrayList<ProcessInfo>();
				mSystemList = new ArrayList<ProcessInfo>();

				for (ProcessInfo processInfo : processList) {
					if (!processInfo.isSystem) {
						mCustomerList.add(processInfo);
					} else {
						mSystemList.add(processInfo);
					}
				}
				// 发送消息,告知主线程可以去填充数据适配器了
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initTitle() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_avail_space = (TextView) findViewById(R.id.tv_avail_space);

		lv_list_process = (ListView) findViewById(R.id.lv_list_process);
		tv_app_title = (TextView) findViewById(R.id.tv_app_title);

		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_setting = (Button) findViewById(R.id.bt_setting);

		bt_select_all.setOnClickListener(this);
		bt_select_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
	}

	private void initTitleData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(this);

		// resId找不到20 0x16进程
		tv_process_count.setText("进程总数:" + mProcessCount);

		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);

		long totalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);

		tv_avail_space.setText("剩余/总共:" + strAvailSpace + "/" + mStrTotalSpace);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			selectAll();
			break;
		case R.id.bt_select_reverse:
			selectReverse();
			break;
		case R.id.bt_clear:
			clear();
			break;
		case R.id.bt_setting:
			setting();
			break;
		}
	}

	private void setting() {
		Intent intent = new Intent(this, ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// boolean isCheck =
		// PrefUtils.getBoolean(this,ConstantValue.IS_SYSTEM_VISABLE,false);
		// 通知数据适配器刷新(isCheck true 显示系统进程 false 隐藏系统进程)
		// 隐藏系统进程,让数据适配器展示到用户进程以后,后续的内容不再展示,getCount()
		mAdapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void clear() {
		List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		// 将勾选上的条目所在的进程,杀死,进程总数减少,可用的内存大小添加,listView数据适配器也需要去刷新
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			if (processInfo.isCheck) {
				// 选中,杀死,更新内存,更新集合---->更新数据适配器
				// 不能在循环此集合过程中去删除这个集合中的数据,创建一个临时变量去记录要杀死进程所在对象
				// mCustomerList.remove(processInfo);
				killProcessList.add(processInfo);
			}
		}

		for (ProcessInfo processInfo : mSystemList) {
			if (processInfo.isCheck) {
				killProcessList.add(processInfo);
			}
		}

		// 释放出来的可用大小
		long releaseAvailSpace = 0;
		// 循环遍历要去杀死进程所在的集合,获取每一个进程的包名,通过ActivityManager将其杀死
		for (ProcessInfo processInfo : killProcessList) {
			// 1,获取进程包名
			String packageName = processInfo.packageName;
			// 2,获取ActivityManager对象
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			// 3,杀死进程
			am.killBackgroundProcesses(packageName);
			// 4,进程总数变化(添加killProcessList.size())
			// 5,杀死一个进程,会去释放内存空间,添加可用的内存大小
			releaseAvailSpace += processInfo.memSize;

			// 6,从用户应用的集合中去删除杀死进程的对象
			if (mCustomerList.contains(processInfo)) {
				mCustomerList.remove(processInfo);
			}
			// 7,从系统应用的集合中去删除杀死进程的对象
			if (mSystemList.contains(processInfo)) {
				mSystemList.remove(processInfo);
			}
		}
		// 8,通知数据适配器刷新
		mAdapter.notifyDataSetChanged();

		// 9,总进程数据的更新,减少
		mProcessCount = mProcessCount - killProcessList.size();
		tv_process_count.setText("进程总数:" + mProcessCount);
		// 10,剩余大小的更新(原有的剩余大小+循环过程中释放的空间大小) = byte--->
		mAvailSpace += releaseAvailSpace;
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
		tv_avail_space.setText("剩余/总共:" + strAvailSpace + "/" + mStrTotalSpace);

		// 11,吐司(告知用户,杀死进程,释放空间)
		ToastUtil.show(this, String.format("杀死了%d进程,释放了%s空间",
				killProcessList.size(),
				Formatter.formatFileSize(this, releaseAvailSpace)));
	}

	/**
	 * 反选,将之前是否选中的状态取反,除了当前手机卫士应用
	 */
	private void selectReverse() {
		// 每一个条目都要选中(系统应用,用户应用,排除当前应用)
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}
		for (ProcessInfo processInfo : mSystemList) {
			processInfo.isCheck = !processInfo.isCheck;
		}
		// 除了要去修改数据以外,还需要告知用户,所有的条目已经选中,刷新数据适配器
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 选中所有的条目,除手机卫士
	 */
	private void selectAll() {
		// 每一个条目都要选中(系统应用,用户应用,排除当前应用)
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = true;
		}
		for (ProcessInfo processInfo : mSystemList) {
			processInfo.isCheck = true;
		}
		// 除了要去修改数据以外,还需要告知用户,所有的条目已经选中,刷新数据适配器
		mAdapter.notifyDataSetChanged();
	}
}
