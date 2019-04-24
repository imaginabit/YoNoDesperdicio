package com.imaginabit.yonodesperdicion;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imaginabit.yonodesperdicion.models.Offers;
import com.imaginabit.yonodesperdicion.utils.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class App extends Application {
	private static boolean isAppRunning = false;

	public static Context appContext;

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

	private Offers offersApi;

	@Override
	public void onCreate() {
		super.onCreate();

		appContext = getApplicationContext();
		try {
			// get: Default Uncaught Exception Handler
			defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
			// set: Default Uncaught Exception Handler
			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

			createNotificationChannel();
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

	private void initializeFeedApi() {

		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
				.create();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Constants.HOME_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.build();

		
	}

	private void createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.default_notification_channel_id);
			String description = getString(R.string.default_notification_channel_description);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}
}
