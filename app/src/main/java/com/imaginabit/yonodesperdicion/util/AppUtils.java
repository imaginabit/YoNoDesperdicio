package com.imaginabit.yonodesperdicion.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Antonio de Sousa Barroso
 */
public class AppUtils {
	public static ConnectivityManager connectivityManager = null;

	public static final String extractKeyHash(final Context context, final String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				// First Signature
				return Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		}
		catch (PackageManager.NameNotFoundException e) {
			Log.d("KeyHash:", "NameNotFoundException=" + e.getMessage());
		}
		catch (NoSuchAlgorithmException e) {
			Log.d("KeyHash:", e.getMessage());
		}
		return null;
	}
    
    public static boolean isEmptyOrNull(String s) {
        return (s == null) || (s.trim().length() == 0);
    }  
    
    public static boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }  
    
    public static boolean isNotEmptyOrNull(String s) {
        return (s != null) && (s.trim().length() > 0);
    } 
    
    public static boolean isNotEmpty(String s) {
        return (s != null) && (s.trim().length() > 0);
    }
    
    public static final TelephonyManager getTelephonyManager(final Context context) {
    	return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }
    
    // Location
    public static final LocationManager getLocationManager(final Context context) {
    	return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public static boolean isGPSLocationProviderEnabled(final Context context) {
    	return getLocationManager(context).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    public static boolean isNetworkLocationProviderEnabled(final Context context) {
    	return getLocationManager(context).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    // Connectivity
    
    public static boolean isActiveNetworkConnection(final Context context) {
    	if (connectivityManager == null) {
    		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	}
    	
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	if (networkInfo == null) {
    		return false;
    	}
    	
        return (networkInfo.isAvailable() && networkInfo.isConnected());
    }
    
	// Vibrator

    private static Vibrator vibrator;
	public static final Vibrator getVibrator(Context context) {
		if (vibrator == null) {
			vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
		}
		return vibrator;
	}
	
    public static void vibrate(final Context context, long time) {
    	Vibrator vibrator = AppUtils.getVibrator(context);
    	if (vibrator != null) {
    		vibrator.vibrate(time);
    	}
    }
    
    public static String avoidNull(final String s) {
    	return avoidNull(s, "");
    }
    
    public static String avoidNull(final String s, final String defaultValue) {
    	if (s == null || "null".equals(s)) {
    		return defaultValue;
    	}
    	return s;
    }
    
    public static String avoidEmptyOrNull(final String s, final String defaultValue) {
    	if (AppUtils.isEmptyOrNull(s) || "null".equals(s)) {
    		return defaultValue;
    	}
    	return s;    	
    }
    
  	/**
	 * Launch GPS options
	 */
    public static void launchGPSOptions(final Activity activity, final int requestCode) {
        final ComponentName toLaunch = new ComponentName(
        													"com.android.settings",
        													"com.android.settings.SecuritySettings"
        												);
        
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        activity.startActivityForResult(intent, requestCode);
    }
    
    public static boolean isNumeric(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        
        int length = s.length();
        for (int i = 0; i < length; i++) {
            if (! Character.isDigit(s.codePointAt(i))) {
                return false;
            }
        }
        
        return true;
    }

	/**
	 * Checks if Service is Running
	 */
	public static boolean isServiceRunning(final Context context, String serviceName) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(info.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Play Sound
	 */
	public static void playSound(final Context context, int soundResourceID) {
		try {
			MediaPlayer player = new MediaPlayer();
			player.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + soundResourceID));
			
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mediaPlayer) {
					mediaPlayer.release();
				}
			});

			player.prepare();
			player.start();
		} 
		catch (Exception e) {
			// Ignored
		}
	}

	public static boolean isValidEmail(String email) {
		if (isEmptyOrNull(email)) {
			return false;
		} else {
			return Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}
	}

	public static int calcHours(long time) {
		return (int)((time / 3600000L) % 24);
	}

	public static int calcMinutes(long time) {
		return (int)((time / 60000L) % 60);
	}

	public static int calcSeconds(long time) {
		return (int)((time % 60000L) / 1000L);
	}

	public static String formatLocationInDegrees(double latitude, double longitude) {
		try {
			int latitudeInSeconds = (int) Math.round(latitude * 3600);
			int latitudeInDegrees = (latitudeInSeconds / 3600);
			latitudeInSeconds = Math.abs(latitudeInSeconds % 3600);

			int latitudeInMinutes = (latitudeInSeconds / 60);
			latitudeInSeconds %= 60;

			int longitudeInSeconds = (int) Math.round(longitude * 3600);
			int longitudeInDegrees = (longitudeInSeconds / 3600);

			longitudeInSeconds = Math.abs(longitudeInSeconds % 3600);

			int longitudeInMinutes = (longitudeInSeconds / 60);

			longitudeInSeconds %= 60;

			String latitudeDegree = (latitudeInDegrees >= 0) ? "N" : "S";
			String longitudeDegrees = (latitudeInDegrees >= 0) ? "E" : "W";

			return Math.abs(latitudeInDegrees) + "° " + latitudeInMinutes + "' " + latitudeInSeconds + "\" " + latitudeDegree + "  " +
				   Math.abs(longitudeInDegrees) + "° " + longitudeInMinutes + "' " + longitudeInSeconds + "\" " + longitudeDegrees;
		}
		catch (Exception e) {
			return String.format("%8.5f", latitude) + "  " + String.format("%8.5f", longitude);
		}
	}

	public static double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return 0.0d;
		}
	}

	public static double parseIntToDouble(String s) {
		try {
			int n = Integer.parseInt(s);
			return (n + 0.0d);
		} catch (NumberFormatException e) {
			return 0.0d;
		}
	}
}