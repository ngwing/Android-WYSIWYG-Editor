package com.github.irshulx;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by IRSHU on 27/2/2017.
 */

public interface EditorListener {
    void onTextChanged(EditText editText, Editable text);

    void onUpload(String path, String uuid);

    void onInsertImage(String url, int index, ImageView imageView);

    View createAudioView(String path);
}