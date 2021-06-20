package com.itheima.mobilesafe.global;

import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

//定义了一个自己的MyApplication,需要告知android系统,运营自己定义的MyApplication
public class MyApplication extends Application {
	protected static final String tag = "MyApplication";
	public static Handler handler = null;
	
	//全局的方法调用
	public void test(){
		Log.i(tag, "调用了MyApplication中的方法................");
	}
	
	@Override
	public void onCreate() {
		//Thread	google市场
//		handler = new Handler();
		//Handler
		//常见资源的释放
		
		
		
		//全局异常的捕获操作
		//设置一个默认的为捕获的异常的处理机制
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				try {
					//此方法在捕获异常后去做调用
					Log.i(tag, "捕获异常");
					//打印异常
					ex.printStackTrace();
					
					//将错误日志写入到sd卡中
					String path = Environment.getExternalStorageDirectory()+File.separator+"error.log";
					
					PrintWriter printWriter = new PrintWriter(new File(path));
					ex.printStackTrace(printWriter);
					printWriter.close();
					
					//闪退应用后,需要去记录,错误日志,并且将其上传到服务器
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		super.onCreate();
	}
}
