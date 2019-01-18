package com.cyanbirds.wwjy.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Process;

import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.request.UploadCrashRequest;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.FileAccessorUtils;
import com.cyanbirds.wwjy.utils.NetworkUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：wangyb
 * 时间：2016/10/13 22:05
 * 描述：捕获异常
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
	private static CrashHandler mInstance;
	private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
	private Context mContext;
	private Handler mHandler;

	private CrashHandler() {
		mHandler = new Handler();
	}

	public static CrashHandler getInstance() {
		if (mInstance == null) {
			synchronized (CrashHandler.class) {
				if (null == mInstance) {
					mInstance = new CrashHandler();
				}
			}
		}
		return mInstance;
	}

	public void init(Context context) {
		mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = context.getApplicationContext();
	}


	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			dumpExceptionToSDCard(ex);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ex.printStackTrace();
		//如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己
		if (mUncaughtExceptionHandler != null) {
			mUncaughtExceptionHandler.uncaughtException(thread, ex);
		} else {
			Process.killProcess(Process.myPid());
		}
	}

	/**
	 * 将异常信息写入sd卡
	 * @param ex
	 * @throws IOException
	 */
	private void dumpExceptionToSDCard(Throwable ex) throws IOException {
		File crashFileDir = FileAccessorUtils.getCrashPathName();
		if (!crashFileDir.exists()) {
			crashFileDir.mkdirs();
		}
		long currentTime = System.currentTimeMillis();
		String crashTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(currentTime));
		File crashFile = new File(FileAccessorUtils.CRASH_PATH, "crash" + crashTime + ".log");
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(crashFile)));
			pw.println(crashTime);
			pw.println("userId=" + AppManager.getClientUser().userId);
			pw.println("channel=" + CheckUtil.getAppMetaData(mContext, "UMENG_CHANNEL"));
			dumpPhoneInfo(pw);
			pw.println();
			ex.printStackTrace(pw);
			pw.close();
			uploadExceptionToServer(crashFile);
		} catch (Exception e) {
			//dump crash info failed
		}
	}

	/**
	 * 获取手机相关信息
	 * @param pw
	 * @throws PackageManager.NameNotFoundException
	 */
	private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
		PackageManager pm = mContext.getPackageManager();
		PackageInfo packageInfo = pm.getPackageInfo(
				mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
		pw.print("App Version：");
		pw.print(packageInfo.versionName);
		pw.print("_");
		pw.println(packageInfo.versionCode);

		//Android版本号
		pw.print("OS Version：");
		pw.print(Build.VERSION.RELEASE);
		pw.print("_");
		pw.println(Build.VERSION.SDK_INT);

		//手机制造商
		pw.print("Vendor：");
		pw.println(Build.MANUFACTURER);

		//手机型号
		pw.print("Model：");
		pw.println(Build.MODEL);

		//CPU架构
		pw.print("CPU ABI：");
		pw.println(Build.CPU_ABI);

		//网络环境
		pw.print("NETWORK：");
		pw.println(NetworkUtils.getNetType(mContext));

	}

	/**
	 * 将异常信息上传至服务器
	 */
	private void uploadExceptionToServer(File crashFile) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(crashFile));
			String data = "";
			while ((data = bufferedReader.readLine()) != null ) {
				stringBuilder.append(data).append("\n");
			}
			new UploadCrashRequest().request(stringBuilder.toString());
			Thread.sleep(300);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
