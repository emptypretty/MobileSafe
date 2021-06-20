package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author qbc 查询归属数据库,获取电话所在位置
 */
public class VirusDao {
	private static final String tag = "VirusDao";
	public static String path = "data/data/com.itheima.mobilesafe/files/antivirus.db";

	// 获取所有病毒签名文件的md5码所在集合方法
	public static List<String> getVirusList() {
		List<String> virusList = new ArrayList<String>();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("datable", new String[] { "md5" }, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			virusList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return virusList;
	}
}
