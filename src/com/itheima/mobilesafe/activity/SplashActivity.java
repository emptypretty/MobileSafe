package com.itheima.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.PrefUtils;
import com.itheima.mobilesafe.utils.StreamUtil;
import com.itheima.mobilesafe.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * @author Administrator
 * 
 */
public class SplashActivity extends Activity {
	protected static final String tag = "SplashActivity";
	/**
	 * 更新新版本的状态码
	 */
	protected static final int UPDATE_VERSION = 100;
	/**
	 * 进入应用程序主界面状态码
	 */
	protected static final int ENTER_HOME = 101;
	private TextView tv_version_name;
	private int mLocalVersionCode;
	private String mVersionDes;
	private String mDownloadUrl;
	private RelativeLayout rl_root;
	// url地址出错状态码
	protected static final int URL_ERROR = 102;
	protected static final int IO_ERROR = 103;
	protected static final int JSON_ERROR = 104;

	private Handler mHandler = new Handler() {
		@Override
		// alt+ctrl+向下箭头,向下拷贝相同代码
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				// 弹出对话框,提示用户更新
				showUpdateDialog();
				break;
			case ENTER_HOME:
				// 进入应用程序主界面,activity跳转过程
				enterHome();
				break;
			case URL_ERROR:
				ToastUtil.show(getApplicationContext(), "url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(getApplicationContext(), "读取异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(getApplicationContext(), "JSON解析异常");
				enterHome();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 除去掉当前标题头
		// stWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		// 初始化UI
		initUI();
		// 初始化数据
		initData();

		// 初始化动画
		initAnimation();

		// 初始化数据库
		initDB();
	}

	private void initDB() {
		// 1.归属地数据拷贝过程
		initAddressDB("address.db");

	}

	/**
	 * 拷贝数据库值files文件夹下
	 * 
	 * @param dbname
	 *            数据库名称
	 */
	private void initAddressDB(String dbname) {
		// 在files文件夹下创建同名dbname数据库文件过程
		File files = getFilesDir();
		File file = new File(files, dbname);
		if (file.exists()) {
			return;
		}

		InputStream stream = null;
		FileOutputStream fos = null;
		// 2.输入流读取第三方资产目录下的文件

		try {

			stream = getAssets().open(dbname);

			// 3.将读取的内容写入到指定文件夹的文件中去
			fos = new FileOutputStream(file);
			// 4.每次的读取内容大小
			byte[] bs = new byte[1024];
			int temp = -1;
			while ((temp = stream.read(bs)) != -1) {
				fos.write(bs, 0, temp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream != null && fos != null) {
				try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * 加入淡入的动画
	 */
	private void initAnimation() {
		// TODO Auto-generated method stub
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);

	}

	/**
	 * 弹出对话框，提示用户更新
	 */
	protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		// 对话框，是依赖于activity存在的
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("版本更新");
		builder.setMessage(mVersionDes);

		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// 下载apk,apk链接地址，downloadUrl
						downloadApk();
					}
				});
		builder.setNegativeButton("稍后再说",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						enterHome();
					}
				});
		// 点击取消事件监听
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				enterHome();
				dialog.dismiss();
			}
		});

		builder.show();
	}

	/**
	 * 
	 */
	protected void downloadApk() {
		// TODO Auto-generated method stub
		// apk下载链接地址，放置apk的所在路径

		// 1.判断sd卡是否可用，是否挂上
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 2.获取SD卡路径
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "MobileSafe.apk";

			// 发送请求，获取APK，并放置到指定路径；
			HttpUtils httpUtils = new HttpUtils();
			// 传递参数（下载地址，下载应用放置位置）
			// Utils.download(mDownloadUrl,target, callback)
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {

				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// TODO Auto-generated method stub
					// 下载成功
					Log.i(tag, "下载成功");
					File file = arg0.result;
					// 提示用户安装
					installApk(file);
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.i(tag, "下载失败");
					// 下载失败

				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					Log.i(tag, "开始下载");
				}

				// 下载过程中的方法
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					// TODO Auto-generated method stub
					super.onLoading(total, current, isUploading);
					Log.i(tag, "下载中");
				}
			});
		}
	}

	/**
	 * 安装对应apk
	 * 
	 * @param file
	 *            安装文件
	 */
	protected void installApk(File file) {
		// TODO Auto-generated method stub
		// 系统应用界面，源码，安装apk入口
		// 通过隐式意图去开启activity
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		/*
		 * intent.setData(Uri.fromFile(file));
		 * intent.setType("application/vnd.android.package-archive");
		 */
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		// startActivity(intent);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}

	/**
	 * 进入应用程序主界面
	 */
	protected void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();

	}

	/**
	 * 获取数据方法
	 */
	private void initData() {
		// 1.应用版本名称
		tv_version_name.setText("版本名称:" + getVersionName());
		Log.i("tv_version_name------>", getVersionName());
		// 2.检测(本地版本号和服务器版本号比对)是否更新，如果有更新，提示用户下载
		// 获取本地版本号
		mLocalVersionCode = getVersionCode();
		Log.i("mLocalVersionCode------>", "" + getVersionCode());
		// 获取服务器的版本号(客户端发请求，服务器给响应，(json,xml))
		// http://www.0xxx.com/uodate74.json?key=value 返回200请求成功，流的方式将数据读取下来
		// json中内容包含：
		/*
		 * 更新版本的版本名称 新版本的描述信息 服务器版本号 新版本apk下载地址
		 */
		// checkVersion();

		boolean is_Update = PrefUtils.getBoolean(this,
				ConstantValue.UPDATE_VERSION, false);

		Log.i(tag, "is_Update == " + is_Update + "");
		if (is_Update) {
			checkVersion();
		} else { // 延时跳转到主界面过程,消息机制 // mHandler.sendEmptyMessage(0);
			// 通过消息机制延时处理消息,在4秒后处理消息的时候,去跳转到主界面

			// 1,延时消息类型状态码 2,延时处理此消息的时间
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}

	}

	/**
	 * 检测版本号
	 */
	private void checkVersion() {

		new Thread() {
			@Override
			public void run() {
				super.run();

				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				// 发送请求获取数据，参数则为请求json的链接地址
				try {
					// 1,封装url地址
					URL url = new URL("http://192.168.1.103:8080/update.json");
					Log.i(tag, "url ===" + url.openConnection());

					// 2，开启一个链接
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					// 3.设置常见请求参数（请求头）
					// 请求超时
					connection.setConnectTimeout(2000);
					// 读取超时
					connection.setReadTimeout(2000);
					// 4.获取请求成功响应码
					if (connection.getResponseCode() == 200) {
						// 5,以流的形式，将数据获取下来
						InputStream is = connection.getInputStream();
						// 6,将流转换成字符串（工具封装类）
						String json = StreamUtil.streamToString(is);
						Log.i(tag, json);

						JSONObject jsonObject = new JSONObject(json);

						// debug调试,解决问题
						String versionName = jsonObject
								.getString("versionName");
						mVersionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject
								.getString("versionCode");
						mDownloadUrl = jsonObject.getString("downloadUrl");

						// 日志打印
						Log.i(tag, versionName);
						Log.i(tag, mVersionDes);
						Log.i(tag, versionCode);
						Log.i(tag, mDownloadUrl);

						// 8,比对版本号(服务器版本号>本地版本号,提示用户更新)
						if (mLocalVersionCode < Integer.parseInt(versionCode)) {
							// 提示用户更新,弹出对话框(UI),消息机制
							msg.what = UPDATE_VERSION;
						} else {
							// 进入应用程序主界面
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					// 指定睡眠时间,请求网络的时长超过4秒则不做处理
					// 请求网络的时长小于4秒,强制让其睡眠满4秒钟
					long endTime = System.currentTimeMillis();
					if (endTime - startTime < 4000) {
						try {
							Thread.sleep(4000 - (endTime - startTime));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(msg);
				}
			};
		}.start();
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * } });
		 */

	}

	/**
	 * 返回版本号
	 * 
	 * @return 非0 则代表获取成功
	 */
	private int getVersionCode() {
		// 1.包管理者对象packageManager
		PackageManager pm = getPackageManager();
		// 2.从包的管理者对象中，获取指定包名的基本信息(版本名称，版本号),传0代表获取基本信息;
		try {
			// 3.获取版本名称
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取版本名称：清单文件中
	 * 
	 * @return 应用版本名称 返回null代表异常
	 */
	private String getVersionName() {
		// 1.包管理者对象packageManager
		PackageManager pm = getPackageManager();
		// 2.从包的管理者对象中，获取指定包名的基本信息(版本名称，版本号),传0代表获取基本信息;
		try {
			// 3.获取版本名称
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

}
