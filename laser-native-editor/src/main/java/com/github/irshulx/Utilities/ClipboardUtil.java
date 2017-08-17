package com.github.irshulx.Utilities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {

    public static void copy(Context context, String text) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        cmb.setPrimaryClip(clip);
    }

    public static String getClipboardContent(Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getPrimaryClip().getItemAt(0).getText().toString().trim().replace("\\n+", "\n").replace(" +", " ");
    }
}