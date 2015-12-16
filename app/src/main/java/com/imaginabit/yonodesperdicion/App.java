package com.imaginabit.yonodesperdicion;

import android.app.Application;
import android.util.Log;

import com.imaginabit.yonodesperdicion.utils.Utils;

/**
 *
 */
public class App extends Application {
	private static boolean isAppRunning = false;

	// Uncaught Exception Handler
	private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = null;
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable t) {
			Log.e("YoNoDesperdicio", "Uncaught Exception: ", t);
			
			try {
					// @TODO implement email report
			}
			catch (Exception e) {
				Log.e("MoveSafer", "Uncaught Exception: ", e);
			}
			
			// Run Default
			defaultUncaughtExceptionHandler.uncaughtException(thread, t);
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			// get: Default Uncaught Exception Handler
			defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
			// set: Default Uncaught Exception Handler
			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		}
		catch (Exception e) {
			Log.e("YoNoDesperdicio", e.getMessage());
			Utils.vibrate(getApplicationContext(), 500);
		}
	}

	public static synchronized boolean isAppRunning() {
		return isAppRunning;
	}

	public static synchronized void setIsAppRunning(boolean isAppRunning) {
		App.isAppRunning = isAppRunning;
	}

	public static void release() {
		isAppRunning = false;
	}
}
