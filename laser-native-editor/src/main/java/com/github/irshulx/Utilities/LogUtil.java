package com.github.irshulx.Utilities;

import android.util.Log;

public class LogUtil {


    public static void traceInvokingMethod() {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        int i = 3;
        String className = null;
        for (StackTraceElement stackTraceElement : traces) {
            className = stackTraceElement.getClassName();
            if (className.equals(LogUtil.class.getName()) || className.equals("dalvik.system.VMStack") || className.equals(Thread.class.getName()))
                continue;

            if (i-- == 0) {
                Log.d("InvokingMethod", stackTraceElement.toString());
            }
        }
    }
}
