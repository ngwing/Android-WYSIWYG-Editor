package com.github.irshulx;

import android.graphics.Bitmap;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by IRSHU on 27/2/2017.
 */

public interface EditorListener {
    void onTextChanged(EditText editText, Editable text);

    void onUpload(Bitmap image, String uuid);

    void onInsertImage(String url, int index, ImageView imageView);
}