package com.itheima.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.CommonNumberDao;
import com.itheima.mobilesafe.db.dao.CommonNumberDao.Child;
import com.itheima.mobilesafe.db.dao.CommonNumberDao.Group;

public class CommonNumberActivity extends Activity {
	private List<Group> mGroupList;
	private ExpandableListView elv;
	private MyAdapter mAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			elv.setAdapter(mAdapter);
		};
	};

	class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			// 获取groupPosition组中childPosition索引的孩子对象
			return mGroupList.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.list_item_common_number, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

			tv_name.setText(getChild(groupPosition, childPosition).name);
			tv_phone.setText(getChild(groupPosition, childPosition).number);

			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroupList.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return mGroupList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mGroupList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		// 返回组所在的view对象方法
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("			" + mGroupList.get(groupPosition).name);
			textView.setTextSize(20);
			textView.setTextColor(Color.RED);
			return textView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		// 组中的孩子节点是否可以被点击 true 可以 false 不行
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);

		elv = (ExpandableListView) findViewById(R.id.elv);
		initData();
		elv.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 点中条目,拨打此条目上的电话号码
				String phone = mAdapter.getChild(groupPosition, childPosition).number;
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + phone));
				startActivity(intent);

				return false;
			}
		});
	}

	private void initData() {
		new Thread() {
			public void run() {
				mGroupList = CommonNumberDao.getGroup();
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
