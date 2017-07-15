/*
 * Copyright (C) 2016 Muhammed Irshad
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.irshulx.Components;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.irshulx.EditorCore;
import com.github.irshulx.models.ControlType;
import com.github.irshulx.models.EditorControl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class AudioExtensions {
    private EditorCore editorCore;

    public AudioExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }


    public void insertAudio(String path, int index) {
        final View childLayout = editorCore.createAudioView(path);
        final String uuid = generateUUID();
        if (index == -1)
            index = editorCore.determineIndex(ControlType.audio);

        editorCore.showNextInputHint(index);
        editorCore.getParentView().addView(childLayout, index);

        if (editorCore.isLastRow(childLayout) && !editorCore.renderFromHtml) {
            editorCore.getInputExtensions().insertEditText(index + 1, null, null);
        }
        EditorControl control = editorCore.createTag(ControlType.audio);
        control.path = uuid; // set the imageId,so we can recognize later after upload
        childLayout.setTag(control);
//        editorCore.onUpload(image, uri, uuid);
    }

    private void hideInputHint(int index) {
        View view = editorCore.getParentView().getChildAt(index);
        ControlType type = editorCore.getControlType(view);
        if (type != ControlType.INPUT)
            return;

        String hint = editorCore.placeHolder;
        if (index > 0) {
            View prevView = editorCore.getParentView().getChildAt(index - 1);
            ControlType prevType = editorCore.getControlType(prevView);
            if (prevType == ControlType.INPUT)
                hint = null;
        }
        TextView tv = (TextView) view;
        tv.setHint(hint);
    }

    public String generateUUID() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdt = df.format(new Date(System.currentTimeMillis()));
        UUID x = UUID.randomUUID();
        String[] y = x.toString().split("-");
        return y[y.length - 1] + sdt;
    }

    public View findImageById(String imageId) {
        for (int i = 0; i < editorCore.getParentChildCount(); i++) {
            View view = editorCore.getParentView().getChildAt(i);
            EditorControl control = editorCore.getControlTag(view);
            if (!TextUtils.isEmpty(control.path) && control.path.equals(imageId))
                return view;
        }
        return null;
    }

}