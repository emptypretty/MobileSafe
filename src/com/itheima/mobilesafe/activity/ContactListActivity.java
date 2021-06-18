package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class ContactListActivity extends Activity {

	protected static final String tag = "ContactListActivity";
	private ListView lv_contact_list;
	private List<HashMap<String, String>> mContactList = new ArrayList<HashMap<String, String>>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 8.去填充数据适配器
			MyAdapter myAdapter = new MyAdapter();
			lv_contact_list.setAdapter(myAdapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		initUI();
		initData();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			// TODO Auto-generated method stub
			return mContactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// 1,生成当前listview一个条目相应的view对象
			View view = View.inflate(getApplicationContext(),
					R.layout.listview_contact_item, null);

			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));
			return view;
		}
	}

	/**
	 * 获取联系人数据方法
	 */
	private void initData() {
		// TODO Auto-generated method stub
		// 因为读取系统联系人，可能是一个耗时操作，放置到 子线程中处理
		new Thread() {
			@Override
			public void run() {
				// 1.获取内容解析器对象(访问地址（后门）)
				ContentResolver contentResolver = getContentResolver();
				// 2. 因为读取系统联系人数据库表过程(读取联系人权限)
				Cursor cursor = contentResolver.query(Uri
						.parse("content://com.android.contacts/raw_contacts"),
						new String[] { "contact_id" }, null, null, null);
				// 3.循环游标，直到没有数据为止

				// mContactList.clear();

				while (cursor.moveToNext()) {
					String id = cursor.getString(0);
					Log.i(tag, "id = " + id);// 1,2
					// 4，根据用户唯一性id的值，查询data和mimetype表生成的视图，获取data以及mimetype字段
					Cursor indexCursor = contentResolver.query(
							Uri.parse("content://com.android.contacts/data"),
							new String[] { "data1", "mimetype" },
							"raw_contact_id = ?", new String[] { id }, null);
					// 5.循环获取每一个联系人的电话号码以及姓名，数据类型
					HashMap<String, String> hashMap = new HashMap<String, String>();
					while (indexCursor.moveToNext()) {
						String data = indexCursor.getString(0);
						String type = indexCursor.getString(1);
						// Log.i(tag, "data = " + indexCursor.getString(0));
						// Log.i(tag, "mimetype = " + indexCursor.getString(1));
						// 根据类型mimetype来判断是用户名称还是电话号码
						// 6.区分类型去给hashMap
						if (type.equals("vnd.android.cursor.item/phone_v2")) {
							if (!TextUtils.isEmpty(data)) {
								hashMap.put("phone", data);
							}

						} else if (type.equals("vnd.android.cursor.item/name")) {
							if (!TextUtils.isEmpty(data)) {
								hashMap.put("name", data);
							}
						}
					}
					indexCursor.close();
					mContactList.add(hashMap);
				}
				cursor.close();

				// 7.消息机制,发送一个空的消息，告知主线程可以去使用子线程已经填充好的数据集合
				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	private void initUI() {
		// TODO Auto-generated method stub
		lv_contact_list = (ListView) findViewById(R.id.lv_contact);
		lv_contact_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 1.position点中条目的索引值，集合的索引值
				String phone = mContactList.get(position).get("phone");
				// 2.将此电话号码传递给前一个界面
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				// 3，关闭此界面
				finish();

			}
		});
	}
}
