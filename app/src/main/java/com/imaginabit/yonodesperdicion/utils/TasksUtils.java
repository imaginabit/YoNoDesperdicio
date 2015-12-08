package com.imaginabit.yonodesperdicion.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 * @author Antonio de Sousa Barroso
 */
public class TasksUtils {

    @SuppressLint("NewApi")
    public static <T extends AsyncTask<Void, Void, Void>> void execute(T task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    @SuppressLint("NewApi")
    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
}
