package com.battleshippark.bsp_camera;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 */
public class Application extends android.app.Application {
	private static final Handler handler = new Handler(Looper.getMainLooper());
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		context = this;

		AnalyticsTrackers.initialize(this);
		AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
	}

	public static Handler getHandler() {
		return handler;
	}

	public static Context getContext() {
		return context;
	}
}
