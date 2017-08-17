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

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.github.irshulx.EditorCore;
import com.github.irshulx.R;
import com.github.irshulx.models.ControlType;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.Op;
import com.github.irshulx.models.RenderType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class InputExtensions {
    public static final int HEADING = 0;
    public static final int CONTENT = 1;
    private int H1TEXTSIZE = 23;
    private int H2TEXTSIZE = 20;
    private int H3TEXTSIZE = 18;
    private int NORMAL_TEXT_SIZE = 16;
    private int fontFace = R.string.fontFamily__serif;
    EditorCore editorCore;
    private Map<Integer, String> contentTypeface;
    private Map<Integer, String> headingTypeface;

    public int getH1TextSize() {
        return this.H1TEXTSIZE;
    }

    public void setH1TextSize(int size) {
        this.H1TEXTSIZE = size;
    }

    public int getH2TextSize() {
        return this.H2TEXTSIZE;
    }

    public void setH2TextSize(int size) {
        this.H2TEXTSIZE = size;
    }

    public int getH3TextSize() {
        return this.H3TEXTSIZE;
    }

    public void setH3TextSize(int size) {
        this.H3TEXTSIZE = size;
    }

    public int getNormalTextSize() {
        return this.NORMAL_TEXT_SIZE;
    }

    public String getFontFace() {
        return editorCore.getContext().getResources().getString(fontFace);
    }

    public void setFontFace(int fontFace) {
        this.fontFace = fontFace;
    }


    public Map<Integer, String> getContentTypeface() {
        return contentTypeface;
    }

    public void setContentTypeface(Map<Integer, String> contentTypeface) {
        this.contentTypeface = contentTypeface;
    }

    public Map<Integer, String> getHeadingTypeface() {
        return headingTypeface;
    }

    public void setHeadingTypeface(Map<Integer, String> headingTypeface) {
        this.headingTypeface = headingTypeface;
    }


    public InputExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }

    CharSequence getSanitizedHtml(String text) {
        Spanned __ = Html.fromHtml(text);
        CharSequence toReplace = noTrailingwhiteLines(__);
        return toReplace;
    }

    public void setText(TextView textView, String text) {
        CharSequence toReplace = getSanitizedHtml(text);
        textView.setText(toReplace);
    }


    private TextView getNewTextView(String text) {
        final TextView textView = new TextView(new ContextThemeWrapper(this.editorCore.getContext(), R.style.WysiwygEditText));
        addEditableStyling(textView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) editorCore.getContext().getResources().getDimension(R.dimen.edittext_margin_bottom));
        textView.setLayoutParams(params);
        if (!TextUtils.isEmpty(text)) {
            Spanned __ = Html.fromHtml(text);
            CharSequence toReplace = noTrailingwhiteLines(__);
            textView.setText(toReplace);
        }
        return textView;
    }

    public CustomEditText getNewEditTextInst(final String hint, String text) {
        final CustomEditText editText = new CustomEditText(new ContextThemeWrapper(this.editorCore.getContext(), R.style.WysiwygEditText));
        addEditableStyling(editText);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (hint != null) {
            editText.setHint(hint);
        }
        if (text != null) {
            setText(editText, text);
        }
        editText.editorCore = editorCore;
        editText.setTag(editorCore.createTag(ControlType.INPUT));
        editText.setBackgroundDrawable(ContextCompat.getDrawable(this.editorCore.getContext(), R.drawable.invisible_edit_text));
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return editorCore.onKey(v, keyCode, event, editText);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    editText.clearFocus();
                } else {
                    editorCore.setActiveView(v);
                }
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = Html.toHtml(editText.getText());
                Object tag = editText.getTag(R.id.control_tag);
                if (s.length() == 0 && tag != null)
                    editText.setHint(tag.toString());
                if (s.length() > 0) {
                /*
                * if user had pressed enter, replace it with br
                */
                    if (s.charAt(s.length() - 1) == '\n') {
                        SpannableStringBuilder ssb = new SpannableStringBuilder(s);
                        text = Html.toHtml(ssb);
                        if (text.length() > 0)
                            setText(editText, text);
                        else
                            s.clear();
                        int index = editorCore.getParentView().indexOfChild(editText);
                    /* if the index was 0, set the placeholder to empty, behaviour happens when the user just press enter
                     */
                        if (index == 0) {
                            editText.setHint(null);
                            editText.setTag(R.id.control_tag, hint);
                        }
                        int position = index + 1;
                        insertEditText(position, hint, "");
                    }
                }
                if (editorCore.getEditorListener() != null) {
                    editorCore.getEditorListener().onTextChanged(editText, s);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
        return editText;
    }

    private boolean isLastText(int index) {
        if (index == 0)
            return false;
        View view = editorCore.getParentView().getChildAt(index - 1);
        ControlType type = editorCore.getControlType(view);
        return type == ControlType.INPUT;
    }

    private void addEditableStyling(TextView editText) {
        editText.setTypeface(getTypeface(CONTENT, Typeface.NORMAL));
        editText.setFocusableInTouchMode(true);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_TEXT_SIZE);

    }


    public TextView insertEditText(int position, String hint, String text) {
        String nextHint = isLastText(position) ? null : editorCore.placeHolder;
        if (editorCore.getRenderType() == RenderType.Editor) {
            final CustomEditText view = getNewEditTextInst(nextHint, text);
            editorCore.getParentView().addView(view, position);
            final android.os.Handler handler = new android.os.Handler();
            if (!editorCore.renderFromHtml) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setFocus(view);
                    }
                });
                editorCore.setActiveView(view);
            }
            return view;
        } else {
            final TextView view = getNewTextView(text);
            view.setTag(editorCore.createTag(ControlType.INPUT));
            editorCore.getParentView().addView(view);
            return view;
        }
    }


    private EditorControl rewriteTags(EditorControl tag, EditorTextStyle styleToAdd) {
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
        tag = editorCore.updateTagStyle(tag, styleToAdd, Op.Insert);
        return tag;
    }

    public EditorControl updateTagStyle(EditorControl tag, EditorTextStyle style) {
        if (tag.styles != null && tag.styles.contains(style))
            return deleteTagStyle(tag, style);
        return addTagStyle(tag, style);
    }

    public EditorControl deleteTagStyle(EditorControl tag, EditorTextStyle style) {
        tag = editorCore.updateTagStyle(tag, style, Op.Delete);
        return tag;
    }

    public EditorControl addTagStyle(EditorControl tag, EditorTextStyle style) {
        if (isHeader(tag)) {
            if (style == EditorTextStyle.BOLD)
                return tag;
            if (isHeader(style)) {
                tag = deleteTagStyle(tag, EditorTextStyle.H1);
                tag = deleteTagStyle(tag, EditorTextStyle.H2);
                tag = deleteTagStyle(tag, EditorTextStyle.H3);
            }
        }
        if (isHeader(style))
            deleteTagStyle(tag, EditorTextStyle.BOLD);
        tag = editorCore.updateTagStyle(tag, style, Op.Insert);
        return tag;
    }

    public boolean isHeader(EditorTextStyle style) {
        return style == EditorTextStyle.H1 || style == EditorTextStyle.H2 || style == EditorTextStyle.H3;
    }

    public boolean isBold(EditorTextStyle style) {
        return style == EditorTextStyle.BOLD;
    }

    public boolean isItalic(EditorTextStyle style) {
        return style == EditorTextStyle.ITALIC;
    }

    public boolean isText(EditorTextStyle style) {
        return style == EditorTextStyle.BOLD || style == EditorTextStyle.ITALIC;
    }


    public int getTextStyleFromStyle(EditorTextStyle editorTextStyle) {
        if (editorTextStyle == EditorTextStyle.H1)
            return H1TEXTSIZE;
        if (editorTextStyle == EditorTextStyle.H2)
            return H2TEXTSIZE;
        if (editorTextStyle == EditorTextStyle.H3)
            return H3TEXTSIZE;
        return NORMAL_TEXT_SIZE;
    }

    private void updateHeaderTextStyle(TextView textView, EditorTextStyle style) {
        EditorControl tag;
        if (textView == null)
            textView = (TextView) editorCore.getActiveView();

        EditorControl editorControl = editorCore.getControlTag(textView);

        boolean containsItalic = editorCore.containsStyle(editorControl.styles, EditorTextStyle.ITALIC);

        if (isHeader(style)) {
            boolean containsStyle = editorCore.containsStyle(editorControl.styles, style);
            int typeface = Typeface.NORMAL;
            if (containsStyle) {
                if (containsItalic)
                    typeface += Typeface.ITALIC;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_TEXT_SIZE);
                textView.setTypeface(Typeface.create(editorCore.getInputExtensions().getFontFace(), typeface));
                tag = deleteTagStyle(editorControl, style);
            } else {
                typeface = Typeface.BOLD;
                if (containsItalic)
                    typeface += Typeface.ITALIC;
                int textSize = getTextStyleFromStyle(style);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                textView.setTypeface(Typeface.create(editorCore.getInputExtensions().getFontFace(), typeface));
                tag = addTagStyle(editorControl, style);
            }
            textView.setTag(tag);
        }
    }

    public boolean isHeader(EditorControl tag) {
        if (tag == null || tag.styles == null)
            return false;
        for (EditorTextStyle style : tag.styles) {
            if (isHeader(style))
                return true;
            continue;
        }
        return false;
    }


    public void bold(EditorControl tag, TextView textView) {
        boolean isHeader = isHeader(tag);
        if (isHeader)
            return;
        int typeface = isItalic(tag) ? Typeface.ITALIC : Typeface.NORMAL;

        boolean containsStyle = containsStyle(tag, EditorTextStyle.BOLD);

        if (containsStyle) {
            tag = deleteTagStyle(tag, EditorTextStyle.BOLD);
            textView.setTypeface(getTypeface(CONTENT, Typeface.NORMAL + typeface));
        } else {
            tag = addTagStyle(tag, EditorTextStyle.BOLD);
            textView.setTypeface(getTypeface(CONTENT, Typeface.BOLD + typeface));
        }
        textView.setTag(tag);
    }

    private boolean containsStyle(EditorControl tag, EditorTextStyle style) {
        if (tag == null)
            return false;
        List<EditorTextStyle> styles = tag.styles;
        if (styles == null || styles.isEmpty())
            return false;
        for (EditorTextStyle item : styles) {
            if (item == style)
                return true;
        }
        return false;
    }


    public void italic(EditorControl tag, TextView editText) {
        int typeface = isHeader(tag) || isBold(tag) ? Typeface.BOLD : Typeface.NORMAL;

        boolean containsStyle = containsStyle(tag, EditorTextStyle.ITALIC);
        if (containsStyle) {
            tag = deleteTagStyle(tag, EditorTextStyle.ITALIC);
            editText.setTypeface(getTypeface(CONTENT, typeface));
        } else {
            editText.setTypeface(getTypeface(CONTENT, typeface + Typeface.ITALIC));
            tag = addTagStyle(tag, EditorTextStyle.ITALIC);
        }
        editText.setTag(tag);
    }

    private boolean isBold(EditorControl tag) {
        if (tag == null || tag.styles == null)
            return false;
        for (EditorTextStyle item : tag.styles) {
            if (isBold(item)) {
                return true;
            }
            continue;
        }
        return false;
    }

    private boolean isItalic(EditorControl tag) {
        if (tag == null || tag.styles == null)
            return false;
        for (EditorTextStyle item : tag.styles) {
            if (isItalic(item)) {
                return true;
            }
            continue;
        }
        return false;
    }

    public void updateTextStyle(EditorTextStyle style, TextView textView) {
        if (isList(textView)) {
            updateListStyle(textView, style);
            return;
        }
        updateTextViewStyle(style, textView);
    }

    private boolean isList(TextView textView) {
        if (textView == null)
            textView = (TextView) editorCore.getActiveView();
        if (textView == null)
            return false;
        Object outterTag = textView.getTag(R.id.outter_tag);
        return outterTag != null && outterTag instanceof TableLayout;
    }

    public void updateListStyle(TextView textView, EditorTextStyle style) {
        if (textView == null)
            textView = (TextView) editorCore.getActiveView();
        if (textView == null)
            return;
        Object outterTag = textView.getTag(R.id.outter_tag);
        editorCore.getListItemExtensions().updateListStyle((TableLayout) outterTag, style);
    }

    public void updateTextViewStyle(EditorTextStyle style, TextView textView) {
        try {
            if (textView == null)
                textView = (TextView) editorCore.getActiveView();

            EditorControl tag = editorCore.getControlTag(textView);

            if (isHeader(style)) {
                updateHeaderTextStyle(textView, style);
                return;
            }
            if (isText(style)) {
                if (style == EditorTextStyle.BOLD) {
                    bold(tag, textView);
                } else if (style == EditorTextStyle.ITALIC) {
                    italic(tag, textView);
                }
                return;
            }
            if (style == EditorTextStyle.INDENT) {
                int pBottom = textView.getPaddingBottom();
                int pRight = textView.getPaddingRight();
                int pTop = textView.getPaddingTop();
                boolean containsStyle = containsStyle(tag, EditorTextStyle.INDENT);
                if (containsStyle) {
                    tag = editorCore.updateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    textView.setPadding(0, pTop, pRight, pBottom);
                    textView.setTag(tag);
                } else {
                    tag = editorCore.updateTagStyle(tag, EditorTextStyle.INDENT, Op.Insert);
                    textView.setPadding(30, pTop, pRight, pBottom);
                    textView.setTag(tag);
                }
            } else if (style == EditorTextStyle.OUTDENT) {
                int pBottom = textView.getPaddingBottom();
                int pRight = textView.getPaddingRight();
                int pTop = textView.getPaddingTop();
                boolean containsStyle = containsStyle(tag, EditorTextStyle.INDENT);
                if (containsStyle) {
                    tag = editorCore.updateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    textView.setPadding(0, pTop, pRight, pBottom);
                    textView.setTag(tag);
                }
            }
        } catch (Exception e) {

        }
    }

    public void insertLink() {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this.editorCore.getContext());
        inputAlert.setTitle("Add a Link");
        final EditText userInput = new EditText(this.editorCore.getContext());
        //dont forget to add some margins on the left and right to match the title
        userInput.setHint("type the URL here");
        userInput.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        inputAlert.setView(userInput);
        inputAlert.setPositiveButton("Insert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInputValue = userInput.getText().toString();
                insertLink(userInputValue);
            }
        });
        inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = inputAlert.create();
        alertDialog.show();
    }

    public void insertLink(String uri) {
        ControlType controlType = editorCore.getControlType(editorCore.getActiveView());
        EditText editText = (EditText) editorCore.getActiveView();
        if (controlType == ControlType.INPUT || controlType == ControlType.UL_LI) {
            String text = Html.toHtml(editText.getText());
            if (TextUtils.isEmpty(text))
                text = "<p dir=\"ltr\"></p>";
            text = trimLineEnding(text);
            Document _doc = Jsoup.parse(text);
            Elements x = _doc.select("p");
            String existing = x.get(0).html();
            x.get(0).html(existing + " <a href='" + uri + "'>" + uri + "</a>");
            Spanned toTrim = Html.fromHtml(x.toString());
            CharSequence trimmed = noTrailingwhiteLines(toTrim);
            editText.setText(trimmed);   //
            editText.setSelection(editText.getText().length());
        }
    }

    public CharSequence noTrailingwhiteLines(CharSequence text) {
        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    public boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private String trimLineEnding(String s) {
        if (s.charAt(s.length() - 1) == '\n') {
            String formatted = s.toString().substring(0, s.length() - 1);
            return formatted;
        }
        return s;
    }

    /**
     * returns the appropriate typeface
     *
     * @param mode  => whether heading (0) or content(1)
     * @param style => NORMAL, BOLD, BOLDITALIC, ITALIC
     * @return typeface
     */
    public Typeface getTypeface(int mode, int style) {
//        if (mode == HEADING && headingTypeface == null) {
//            return Typeface.create(getFontFace(), style);
//        } else if (mode == CONTENT && contentTypeface == null) {
//
//            return Typeface.create(getFontFace(), style);
//        }
//        if (mode == HEADING && !headingTypeface.containsKey(style)) {
//            throw new IllegalArgumentException("the provided fonts for heading is missing the varient for this style. Please checkout the documentation on adding custom fonts.");
//        } else if (mode == CONTENT && !headingTypeface.containsKey(style)) {
//            throw new IllegalArgumentException("the provided fonts for content is missing the varient for this style. Please checkout the documentation on adding custom fonts.");
//        }
//        if (mode == HEADING) {
//            return FontCache.get(headingTypeface.get(style), editorCore.getContext());
//        } else {
//            return FontCache.get(contentTypeface.get(style), editorCore.getContext());
//        }
        return Typeface.defaultFromStyle(style);
    }

    public void setFocus(CustomEditText view) {
        view.requestFocus();
        InputMethodManager mgr = (InputMethodManager) editorCore.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        view.setSelection(view.getText().length());
        editorCore.setActiveView(view);
    }


    public void setFocusToNext(int startIndex) {
        for (int i = startIndex; i < editorCore.getParentView().getChildCount(); i++) {
            View view = editorCore.getParentView().getChildAt(i);
            ControlType controlType = editorCore.getControlType(view);
            if (controlType == ControlType.hr || controlType == ControlType.img || controlType == ControlType.map || controlType == ControlType.none)
                continue;
            if (controlType == ControlType.INPUT) {
                setFocus((CustomEditText) view);
                break;
            }
            if (controlType == ControlType.ol || controlType == ControlType.ul) {
                editorCore.getListItemExtensions().setFocusToList(view, ListItemExtensions.POSITION_START);
                editorCore.setActiveView(view);
            }
        }
    }

    public CustomEditText getEditTextPrevious(int startIndex) {
        CustomEditText customEditText = null;
        for (int i = 0; i < startIndex; i++) {
            View view = editorCore.getParentView().getChildAt(i);
            ControlType controlType = editorCore.getControlType(view);
            if (controlType == ControlType.hr || controlType == ControlType.img || controlType == ControlType.map || controlType == ControlType.none)
                continue;
            if (controlType == ControlType.INPUT) {
                customEditText = (CustomEditText) view;
                continue;
            }
            if (controlType == ControlType.ol || controlType == ControlType.ul) {
                editorCore.getListItemExtensions().setFocusToList(view, ListItemExtensions.POSITION_START);
                editorCore.setActiveView(view);
            }
        }
        return customEditText;
    }

    public void setFocusToPrevious(int startIndex) {
        for (int i = startIndex; i > 0; i--) {
            View view = editorCore.getParentView().getChildAt(i);
            ControlType controlType = editorCore.getControlType(view);
            if (controlType == ControlType.hr || controlType == ControlType.img || controlType == ControlType.map || controlType == ControlType.none)
                continue;
            if (controlType == ControlType.INPUT) {
                setFocus((CustomEditText) view);
                break;
            }
            if (controlType == ControlType.ol || controlType == ControlType.ul) {
                editorCore.getListItemExtensions().setFocusToList(view, ListItemExtensions.POSITION_START);
                editorCore.setActiveView(view);
            }
        }
    }

}
