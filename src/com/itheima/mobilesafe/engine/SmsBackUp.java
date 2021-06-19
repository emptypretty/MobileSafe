package com.itheima.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackUp {

	// 备份短信方法

	private static int index = 0;

	public static void backup(Context ctx, String path, ProgressDialog pd) {
		FileOutputStream fos = null;
		Cursor cursor = null;
		try {

			// 需要用到的对象上下文环境，备份文件夹路径，进度条所在的对话框对象用于备份过程中进度的更新
			// 1.获取备份短信写入的文件

			File file = new File(path);
			// 2.获取内容解析器，获取短信数据库中数据
			cursor = ctx.getContentResolver().query(
					Uri.parse("content://sms/"),
					new String[] { "address", "date", "type", "body" }, null,
					null, null);
			// 3.文件响应的输出流
			fos = new FileOutputStream(file);
			// 4.序列化数据库中读取的数据，放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			// 5.给此xml作相应设置
			newSerializer.setOutput(fos, "utf-8");
			// DTD（xml规范）
			newSerializer.startDocument("utf-8", true);

			newSerializer.startTag(null, "smss");

			// 6.备份短息总数指定
			pd.setMax(cursor.getCount());

			// 7.读取数据库中的每一行数据写入到xml中

			while (cursor.moveToNext()) {
				newSerializer.startTag(null, "smss");

				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");

				newSerializer.startTag(null, "date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");

				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");

				newSerializer.startTag(null, "body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null, "body");

				newSerializer.endTag(null, "smss");

				// 8.每循环一次需要去让进度条叠加
				index++;

				Thread.sleep(500);
				// progressDialog可以在子线程中更新相应的进度条的改变
				pd.setProgress(index);
			}
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (cursor != null && fos != null) {
					cursor.close();
					fos.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
