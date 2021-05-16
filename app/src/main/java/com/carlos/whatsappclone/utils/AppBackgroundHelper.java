package com.carlos.whatsappclone.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.UsersProvider;

import java.util.List;

public class  AppBackgroundHelper {

    public static void online(Context context){
        UsersProvider usersProvider = new UsersProvider();
        AuthProvider authProvider = new AuthProvider();
        if (authProvider.getUid() != null) {
             usersProvider.updateOnline(authProvider.getUid(), !isApplicationSentToBackground(context));
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
