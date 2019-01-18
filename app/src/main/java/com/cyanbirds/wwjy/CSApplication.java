package com.cyanbirds.wwjy;

import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;
import android.util.SparseIntArray;

import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.helper.AppActivityLifecycleCallbacks;
import com.cyanbirds.wwjy.helper.CrashHandler;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.base.RetrofitManager;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.memory.PoolConfig;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.memory.PoolParams;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.Set;

import static com.cyanbirds.wwjy.config.AppConstants.BUGLY_ID;

/**
 * 
 * @ClassName:CSApplication
 * @Description:全局Application
 * @author wangyb
 * @Date:2015年5月3日上午10:39:37
 *
 */
public class CSApplication extends MultiDexApplication {

	private static CSApplication sApplication;

	// IWXAPI 是第三方app和微信通信的openapi接口
	public static IWXAPI api;

	public static synchronized CSApplication getInstance() {
		return sApplication;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		sApplication = this;
		AppManager.getExecutorService().execute(() -> {
			MMKV.initialize(sApplication);
			AppManager.setMMKV(MMKV.defaultMMKV());
			AppManager.setContext(sApplication);

			AppManager.setUserInfo();

			registerActivityLifecycleCallbacks(AppActivityLifecycleCallbacks.getInstance());

			initFresco();

			registerWeiXin();
		});

		//初始化短信sdk
		MobSDK.init(this);

		CrashHandler.getInstance().init(sApplication);

		initBugly();
	}

	private void initBugly() {
		// 获取当前进程名
		String processName = AppManager.getProcessName(android.os.Process.myPid());
		// 设置是否为上报进程
		CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
		strategy.setUploadProcess(processName == null || processName.equals(AppManager.pkgName));
		// 初始化Bugly
		CrashReport.initCrashReport(this, BUGLY_ID, false, strategy);
	}

	private void registerWeiXin() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_ID, true);
		api.registerApp(AppConstants.WEIXIN_ID);

		AppManager.setIWX_PAY_API(WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_PAY_ID, true));
		AppManager.getIWX_PAY_API().registerApp(AppConstants.WEIXIN_PAY_ID);
	}

	private void initFresco() {
		DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
				.setBaseDirectoryPath(getCacheDir())
				.setBaseDirectoryName("tan_love")
				.setMaxCacheSize(500*1024*1024)//500MB
				.setMaxCacheSizeOnLowDiskSpace(10 * 1024 * 1024)
				.setMaxCacheSizeOnVeryLowDiskSpace(5 * 1024 * 1024)
				.build();

		Set<RequestListener> listeners = new HashSet<>();
		listeners.add(new RequestLoggingListener());

		int MaxRequestPerTime = 64;
		SparseIntArray defaultBuckets = new SparseIntArray();
		defaultBuckets.put(16 * ByteConstants.KB, MaxRequestPerTime);
		PoolParams smallByteArrayPoolParams = new PoolParams(
				16 * ByteConstants.KB * MaxRequestPerTime,
				2 * ByteConstants.MB,
				defaultBuckets);
		PoolFactory factory = new PoolFactory(
				PoolConfig.newBuilder()
						. setSmallByteArrayPoolParams(smallByteArrayPoolParams)
						.build());

		ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
				.newBuilder(this, RetrofitManager.getInstance().getOkHttpClient())
				.setBitmapsConfig(Bitmap.Config.RGB_565)
				.setDownsampleEnabled(true)
				.setPoolFactory(factory)
				.setMainDiskCacheConfig(diskCacheConfig)
				.setRequestListeners(listeners).build();
		Fresco.initialize(this, config);
	}
}
