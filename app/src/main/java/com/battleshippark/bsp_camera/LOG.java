package com.battleshippark.bsp_camera;

import android.util.Log;

/**
 * format parameter in each methods is used for String.format()
 */
public class LOG {
	public static void v(String format, Object... args) {

	}

	public static void d(String format, Object... args) {

	}

	public static void i(String className, String format, Object... args) {
		Log.i(className, String.format(format, args));
	}

	public static void w(String format, Object... args) {

	}

	public static void e(String format, Object... args) {

	}
}
