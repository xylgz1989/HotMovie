package com.xyl.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 应用崩溃捕捉类
 * @author xyl
 *
 * <p>注：此为单例类，建议放在Application的子类使用，记得加WRITE_EXTERNAL_STORAGE权限</p>
 *使用方法：<p>1.调用getInstance()方法生成对象</p>2.调用init(Context ctx)方法进行初始化</p>
 * @author xyl
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = CrashHandler.class.getSimpleName();
	private static final boolean DEBUG = true;
	private static final String PARENT_PATH = Environment.getExternalStorageDirectory()
			.getPath() + "/CrashLog/";
	private static String path = null;
	private static final String FILE_NAME = "crash";
	private static final String FILE_NAME_SUFFIX = ".txt";

	private static CrashHandler sInstance = new CrashHandler();
	private UncaughtExceptionHandler mDefaultCrashHandler;
	private Context mCtx;

	private CrashHandler(){

	}

	public static CrashHandler getInstance(){
		return sInstance;
	}

	public void init(Context ctx){
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mCtx = ctx.getApplicationContext();
		path = PARENT_PATH + mCtx.getPackageName()+ "/";
	}
    /**
     * 这是最关键的函数，当程序有未被捕获的异常，系统将会自动调用uncaughtException方法
     */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		dumpExceptionToSDCard(ex);
		uploadExceptionToServer();

		ex.printStackTrace();
        //如果系统提供默认的异常处理器，交给系统去结束程序，否则就自己结束自己
		if(mDefaultCrashHandler != null){
			mDefaultCrashHandler.uncaughtException(thread, ex);
		}else{
			Process.killProcess(Process.myPid());
		}
	}
    /**
     * 导出异常信息到SD卡中
     * @param ex
     */
	private void dumpExceptionToSDCard(Throwable ex) {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			if(DEBUG){
				Log.w(TAG, "sdcard unmounted,skip dump exception");
				return;
			}
		}

		File dir = new File(path);
		if(!dir.exists()){
			/*boolean isCreated = */dir.mkdirs();
//			Log.i(TAG, "dir created?"+isCreated);
		}
		long current = System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date(current));
		File file = new File(path + FILE_NAME + time + FILE_NAME_SUFFIX);

		try {
			PrintWriter pw = new PrintWriter(
					new BufferedWriter(new FileWriter(file)));
			pw.println(time);
			dumpPhoneInfo(pw);
			pw.println();
			ex.printStackTrace(pw);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "dump crash info failed");
		}

	}
    /**
     * 输出设备信息
     * @param pw
     * @throws NameNotFoundException
     */
	private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
		PackageManager pm = mCtx.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mCtx.getPackageName(),
				PackageManager.GET_ACTIVITIES);
		pw.print("App version:");
		pw.print(pi.versionName);
		pw.print("_");
		pw.println(pi.versionCode);

		//Android
		pw.print("OS version:");
		pw.print(Build.VERSION.RELEASE);
		pw.print("_");
		pw.println(Build.VERSION.SDK_INT);

		pw.print("Vendor:");
		pw.println(Build.MANUFACTURER);

		pw.print("Model:");
		pw.println(Build.MODEL);

		//CPU
		pw.print("CPU ABI:");
		pw.println(Build.CPU_ABI);



	}

	private void uploadExceptionToServer() {
		// TODO Upload Exception Message To Server

	}
}
