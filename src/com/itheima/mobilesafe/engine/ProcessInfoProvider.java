package com.itheima.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.domain.ProcessInfo;

public class ProcessInfoProvider {
	/**
	 * @param ctx
	 *            上下文环境
	 * @return 正在运行的进程总数
	 */
	public static int getProcessCount(Context ctx) {
		// 1,获取activity管理者对象
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 2,获取当前手机的进程集合
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		// 3,返回集合的总数
		return runningAppProcesses.size();
	}

	/**
	 * @param ctx
	 *            上下文环境
	 * @return 可用内存空间的大小 byte
	 */
	public static long getAvailSpace(Context ctx) {
		// 1,获取activity管理者对象
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 2,获取封装内存信息的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 3,对以上对象赋值
		am.getMemoryInfo(memoryInfo);
		// 4,可用内存大小的获取
		return memoryInfo.availMem;
	}

	/**
	 * @param ctx
	 *            上下文环境
	 * @return 可用内存空间的总大小 byte ,0获取总大小失败
	 */
	public static long getTotalSpace(Context ctx) {
		// //1,获取activity管理者对象
		// ActivityManager am = (ActivityManager)
		// ctx.getSystemService(Context.ACTIVITY_SERVICE);
		// //2,获取封装内存信息的对象
		// MemoryInfo memoryInfo = new MemoryInfo();
		// //3,对以上对象赋值
		// am.getMemoryInfo(memoryInfo);
		// //4,可用内存大小的获取
		// return memoryInfo.totalMem;

		// 在手机中,有一个配置文件记录了手机的可用内存大小proc/meminfo
		try {
			// 1,定位文件路径
			FileReader fileReader = new FileReader("proc/meminfo");
			// 2,创建BufferReader
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			// 3,读一行
			String readLine = bufferedReader.readLine();
			// 4,通过ASCII对字符串中的每一个字符做匹配
			char[] charArray = readLine.toCharArray();
			// 5,循环遍历上诉的字符数组
			StringBuffer sb = new StringBuffer();
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString()) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @param ctx
	 *            上下文环境
	 * @return 封装了进程对象(ProcessInfo)的集合
	 */
	public static List<ProcessInfo> getProcessList(Context ctx) {
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		// 1,获取activityManger对象
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 2,获取正在运行的进程
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		PackageManager pm = ctx.getPackageManager();
		// 3,循环遍历每一个进程对象,获取相关信息
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			// 4,获取进程名称==包名
			ProcessInfo processInfo = new ProcessInfo();
			processInfo.packageName = runningAppProcessInfo.processName;

			// 5,获取进程的唯一性的id,pid,此pid可以辅助获取当前进程占用的内存大小
			android.os.Debug.MemoryInfo[] processMemoryInfo = am
					.getProcessMemoryInfo(new int[] { runningAppProcessInfo.pid });
			// 6,获取数组中索引位置为0的MemoryInfo对象
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			// 7,以占用的内存大小
			processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						processInfo.packageName, 0);
				// 8,应用名称
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				// 9,应用的图标
				processInfo.drawable = applicationInfo.loadIcon(pm);
				// 10,应用类型(系统,状态机)
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();

				// 如果进程没有应用名称,就拿包名作为其应用名称
				processInfo.name = processInfo.packageName;
				// 如果进程没有应用图标,拿当前应用的icon作为其默认图标
				processInfo.drawable = ctx.getResources().getDrawable(
						R.drawable.ic_launcher);
				// 没有名称的进程默认为系统进程
				processInfo.isSystem = true;
			}
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}

	/**
	 * 杀死手机进程
	 */
	public static void killProcess(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取所有的后台进程
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			if (runningAppProcessInfo.processName.equals(ctx.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
	}
}
