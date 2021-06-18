package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static void show(Context ctx,String message){
		Toast.makeText(ctx, message, 0).show();
	}
}
