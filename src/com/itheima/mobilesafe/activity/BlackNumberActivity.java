package com.itheima.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.db.domain.BlackNumberInfo;
import com.itheima.mobilesafe.utils.ToastUtil;

//1.复用convertView
//2.对findViewById次数的优化，使用ViewHolder
//3.将ViewHolder定义成静态，不会去创建多个对象
//4.listView如果有多个条目的时候，可以做分页算法，每一加载20条，逆序

public class BlackNumberActivity extends Activity {

	private Button bt_add;
	private ListView lv_blacknumber;
	private BlackNumberDao mDao;
	private List<BlackNumberInfo> mBlackNumberInfoList;
	private int mode = 1;
	private MyAdapter mAdapter;
	private boolean mIsLoad = false;
	private int mCount;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			// 4.告知listView可以去设置数据适配器
			if (mAdapter == null) {
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}

		};
	};

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBlackNumberInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mBlackNumberInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// TODO Auto-generated method stub
			/*
			 * View view = null; if (convertView == null) { view =
			 * View.inflate(getApplicationContext(),
			 * R.layout.listview_blacknumber_item, null); } else { view =
			 * convertView; }
			 */
			// 复用convertView

			// 1.复用ViewHolder
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_blacknumber_item, null);
				holder = new ViewHolder();
				holder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 1.数据库的删除，
					mDao.delete(mBlackNumberInfoList.get(position).phone);
					// 2.集合中的删除，
					mBlackNumberInfoList.remove(position);
					// 3.通知数据适配器刷新
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}

				}
			});

			holder.tv_phone.setText(mBlackNumberInfoList.get(position).phone);
			// tv_mode.setText(mBlackNumberInfoList.get(position).mode);

			int mode = Integer
					.parseInt(mBlackNumberInfoList.get(position).mode);
			switch (mode) {
			case 1:
				holder.tv_mode.setText("拦截短信");
				break;
			case 2:
				holder.tv_mode.setText("拦截电话");
				break;
			case 3:
				holder.tv_mode.setText("拦截所有");
				break;

			}
			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);

		initUI();

		initData();
	}

	/*
	 * private void loadData(int index) { // 1.获取操作黑名单数据库的对象 mDao =
	 * BlackNumberDao.getInstance(getApplicationContext()); // 2.查询所有数据
	 * mBlackNumberInfoList = mDao.find(index);
	 * 
	 * // 3.通过消息机制告知主线程可以去使用数据的集合
	 * 
	 * mHandler.sendEmptyMessage(0); }
	 */

	private void initData() {
		// TODO Auto-generated method stub

		new Thread() {
			@Override
			public void run() {
				// 1.获取操作黑名单数据库的对象
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				// 2.查询所有数据
				mBlackNumberInfoList = mDao.find(0);
				mCount = mDao.getCount();
				// 3.通过消息机制告知主线程可以去使用数据的集合

				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	private void initUI() {
		// TODO Auto-generated method stub

		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_list_blacknumber);

		bt_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		// 监听其滚动状态

		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			// 滚动过程中，状态发生改变调用方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

				// OnScrollListener.SCROLL_STATE_FLING 飞速滚动
				// OnScrollListener.SCROLL_STATE_IDLE 空闲状态
				// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手触摸着去滚动状态

				if (mBlackNumberInfoList != null) {
					// 条件一：滚动到停止状态
					// 条件二：最后一个条目可见（最后一个条目的索引值>=数据适配器中集合的总条目个数-1）
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition() >= mBlackNumberInfoList
									.size() - 1 && !mIsLoad) {
						/*
						 * // mIsLoad防止重置加载的变量 //
						 * 如果当前正在加载mIsLoad就会为true，本次加载完毕后，再将mIsLoad改为false //
						 * 如果下一次加载需要去做执行的时候，会判断上述mIsLoad的变量，是否为false，
						 * 如果为true，就需要等待上一次加载完成，将其改为false后再去加载
						 */
						// 如果条目的总数大于集合大小的时候，才可以去继续加载更多
						if (mCount > mBlackNumberInfoList.size()) {
							// 加载下一页数据
							new Thread() {
								public void run() {
									mDao = BlackNumberDao
											.getInstance(getApplicationContext());
									// 2.查询所有数据
									List<BlackNumberInfo> moreData = mDao
											.find(mBlackNumberInfoList.size());
									// 3.添加下一页数据的过程
									mBlackNumberInfoList.addAll(moreData);

									// 4.通知数据适配器刷新

									mHandler.sendEmptyMessage(0);
								};
							}.start();
						}

					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void showDialog() {
		// TODO Auto-generated method stub
		Builder builder = new AlertDialog.Builder(this);

		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);

		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_submit = (Button) view.findViewById(R.id.bt_sumbit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		// 监听其选中条目的切换过程

		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				switch (checkedId) {
				case R.id.rb_sms:
					// 拦截短信
					mode = 1;
					break;
				case R.id.rb_phone:
					// 拦截电话
					mode = 2;
					break;
				case R.id.rb_all:
					// 拦截所有
					mode = 3;
					break;

				default:
					break;
				}
			}
		});

		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 1.获取输入框中 的电话号码
				String phone = et_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {
					// 2.数据库插入当前输入的拦截电话号码

					mDao.insert(phone, mode + "");
					// 3.让数据库和集合保持同步（1.数据库中数据重新读一遍2.手动向集合中添加一个对象（插入数据构建的对象））
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.phone = phone;
					blackNumberInfo.mode = mode + "";

					// 4.、将对象插入到集合的最顶部

					mBlackNumberInfoList.add(0, blackNumberInfo);

					// 5.通知数据适配器刷新(数据适配器中的数据有改变了)
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}

					// 6.隐藏对话框
					dialog.dismiss();
				} else {
					ToastUtil.show(getApplicationContext(), "请输入拦截号码");
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
}
