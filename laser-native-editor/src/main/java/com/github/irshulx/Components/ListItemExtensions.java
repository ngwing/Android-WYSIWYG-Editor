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

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
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
import android.widget.TableRow;
import android.widget.TextView;

import com.github.irshulx.EditorCore;
import com.github.irshulx.R;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.ControlType;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.RenderType;

import java.util.List;

import static com.github.irshulx.Components.InputExtensions.CONTENT;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class ListItemExtensions {
    EditorCore editorCore;
    public static final int POSITION_START = 0;
    public static final int POSITION_END = 1;
    private int listItemTemplate = R.layout.tmpl_list_item;

    public ListItemExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }

    public void setListItemTemplate(int drawable) {
        this.listItemTemplate = drawable;
    }

    public TableLayout insertList(int Index, boolean isOrdered, String text) {

        TableLayout tableLayout = createTable();
        editorCore.getParentView().addView(tableLayout, Index);
        tableLayout.setTag(editorCore.createTag(isOrdered ? ControlType.ol : ControlType.ul));
        addListItem(tableLayout, isOrdered, text);
        return tableLayout;
    }

    public TableLayout createTable() {
        TableLayout table = new TableLayout(editorCore.getContext());
        table.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        table.setPadding(30, 10, 10, 10);
        return table;
    }


    public View addListItem(TableLayout tableLayout, boolean isOrdered, String text) {
        final View childLayout = ((Activity) editorCore.getContext()).getLayoutInflater().inflate(this.listItemTemplate, null);
        final CustomEditText editText = (CustomEditText) childLayout.findViewById(R.id.editText);
        final TextView orderTextView = (TextView) childLayout.findViewById(R.id.labelOrder);
        orderTextView.setTypeface(Typeface.create(editorCore.getInputExtensions().getFontFace(), Typeface.NORMAL));
        editText.setTypeface(Typeface.create(editorCore.getInputExtensions().getFontFace(), Typeface.NORMAL));
        if (isOrdered) {
            int count = tableLayout.getChildCount();
            orderTextView.setText(String.valueOf(count + 1) + ".");
        }
        if (editorCore.getRenderType() == RenderType.Editor) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, editorCore.getInputExtensions().getNormalTextSize());
            editText.setTag(editorCore.createTag(isOrdered ? ControlType.OL_LI : ControlType.UL_LI));
            orderTextView.setTag(editorCore.createTag(ControlType.list_label));
            editText.setTag(R.id.outter_tag, tableLayout);
            childLayout.setTag(editorCore.createTag(isOrdered ? ControlType.OL_LI : ControlType.UL_LI));
            editText.setTypeface(editorCore.getInputExtensions().getTypeface(CONTENT, Typeface.NORMAL));

            updateStyle(tableLayout, editText);
            updateStyle(tableLayout, orderTextView);

            editorCore.setActiveView(editText);
            editorCore.getInputExtensions().setText(editText, text);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editorCore.setActiveView(v);
                }
            });
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        editorCore.setActiveView(v);
                    }
                }
            });
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return editorCore.onKey(v, keyCode, event, editText);
                }
            });

            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //                if (s.length() == 0) {
                    //                    deleteFocusedPrevious(editText);
                    //                }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = Html.toHtml(editText.getText());
                    if (s.length() > 0) {
                        if (s.charAt(s.length() - 1) == '\n') {
                            text = text.replaceAll("<br>", "");
                            TableRow _row = (TableRow) editText.getParent();
                            TableLayout tableLayout = (TableLayout) _row.getParent();
                            ControlType type = editorCore.getControlType(tableLayout);
                            if (s.length() == 0 || s.toString().equals("\n")) {
                                int index = editorCore.getParentView().indexOfChild(tableLayout);
                                tableLayout.removeView(_row);
                                editorCore.getInputExtensions().insertEditText(index + 1, "", "");
                            } else {
                                Spanned __ = Html.fromHtml(text);
                                CharSequence toReplace = editorCore.getInputExtensions().noTrailingwhiteLines(__);

                                if (toReplace.length() > 0) {
                                    editText.setText(toReplace);
                                } else {
                                    editText.getText().clear();
                                }


                                int index = tableLayout.indexOfChild(_row);
                                //  insertEditText(index + 1, "");
                                addListItem(tableLayout, type == ControlType.ol, "");
                            }

                        }
                    }
                }
            });

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) editorCore.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    editText.setSelection(editText.getText().length());
                }
            }, 0);
        } else {
            final TextView textView = (TextView) childLayout.findViewById(R.id.labelText);
            textView.setTypeface(editorCore.getInputExtensions().getTypeface(CONTENT, Typeface.NORMAL));

            /*
            It's a renderer, so instead of EditText,render TextView
             */
            if (!TextUtils.isEmpty(text)) {
                editorCore.getInputExtensions().setText(textView, text);
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, editorCore.getInputExtensions().getNormalTextSize());
            textView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
        }
        tableLayout.addView(childLayout);
        return childLayout;
    }

    public void convertListToNormalText(TableLayout tableLayout, int startIndex) {
        int tableChildCount = tableLayout.getChildCount();
        for (int i = startIndex; i < tableChildCount; i++) {
            View childRow = tableLayout.getChildAt(i);
            tableLayout.removeView(childRow);
            String text = getTextFromListItem(childRow);
            int Index = editorCore.getParentView().indexOfChild(tableLayout);
            editorCore.getInputExtensions().insertEditText(Index + 1, "", text);
            i -= 1;
            tableChildCount -= 1;
        }
        //if item is the last in the table, remove the table from parent

        if (tableLayout.getChildCount() == 0) {
            editorCore.getParentView().removeView(tableLayout);
        }
    }

    public void convertListToOrdered(TableLayout tableLayout) {
        EditorControl editorControl = editorCore.createTag(ControlType.ol);
        tableLayout.setTag(editorControl);
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View childRow = tableLayout.getChildAt(i);
            CustomEditText editText = (CustomEditText) childRow.findViewById(R.id.editText);
            editText.setTag(editorCore.createTag(ControlType.OL_LI));
            childRow.setTag(editorCore.createTag(ControlType.OL_LI));
            TextView bullet = (TextView) childRow.findViewById(R.id.labelOrder);
            bullet.setText(String.valueOf(i + 1) + ".");
        }
    }


    public void updateListStyle(TableLayout tableLayout, EditorTextStyle style) {
        EditorControl control = editorCore.getControlTag(tableLayout);
        editorCore.getInputExtensions().updateTagStyle(control, style);

        if (style == EditorTextStyle.BOLD && editorCore.getInputExtensions().isHeader(control))
            return;

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View childRow = tableLayout.getChildAt(i);
            CustomEditText editText = (CustomEditText) childRow.findViewById(R.id.editText);
            TextView bullet = (TextView) childRow.findViewById(R.id.labelOrder);
            editorCore.getInputExtensions().updateTextViewStyle(style, editText);
            editorCore.getInputExtensions().updateTextViewStyle(style, bullet);
        }
    }

    private void updateStyle(TableLayout tableLayout, TextView textView) {
        EditorControl control = editorCore.getControlTag(tableLayout);
        List<EditorTextStyle> controlStyles = control.controlStyles;
        if (controlStyles == null || controlStyles.isEmpty())
            return;
        for (EditorTextStyle style : controlStyles) {
            editorCore.getInputExtensions().updateTextViewStyle(style, textView);
        }
    }

    public void convertListToUnordered(TableLayout tableLayout) {
        EditorControl type = editorCore.createTag(ControlType.ul);
        tableLayout.setTag(type);
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View childRow = tableLayout.getChildAt(i);
            CustomEditText _EditText = (CustomEditText) childRow.findViewById(R.id.editText);
            _EditText.setTag(editorCore.createTag(ControlType.UL_LI));
            childRow.setTag(editorCore.createTag(ControlType.UL_LI));
            TextView bullet = (TextView) childRow.findViewById(R.id.labelOrder);
            bullet.setText("•");
        }
    }


    public String getTextFromListItem(View row) {
        CustomEditText _text = (CustomEditText) row.findViewById(R.id.editText);
        return _text.getText().toString();
    }

    public void insertlist(boolean isOrdered) {
        View activeView = editorCore.getActiveView();
        ControlType currentFocus = editorCore.getControlType(activeView);
        if (currentFocus == ControlType.UL_LI && !isOrdered) {
                 /* this means, current focus is on n unordered list item, since user clicked
                 on unordered list icon, loop through the parents childs and convert each list item into normal edittext
                 *
                 */
            TableRow row = (TableRow) activeView.getParent();
            TableLayout tableLayout = (TableLayout) row.getParent();
            convertListToNormalText(tableLayout, tableLayout.indexOfChild(row));
                /* this means, current focus is on n unordered list item, since user clicked
                 on unordered list icon, loop through the parents childs and convert each list item into normal edittext
                 *
                 */

        } else if (currentFocus == ControlType.UL_LI && isOrdered) {

                                    /*
                    * user clicked on ordered list item. since it's an unordered list, you need to loop through each and convert each
                    * item into an ordered list.
                    * */
            TableRow _row = (TableRow) activeView.getParent();
            TableLayout tableLayout = (TableLayout) _row.getParent();
            convertListToOrdered(tableLayout);
                                 /*
                    * user clicked on ordered list item. since it's an unordered list, you need to loop through each and convert each
                    * item into an ordered list.
                    * */
        } else if (currentFocus == ControlType.OL_LI && isOrdered) {
                /*
                *
                * this means the item was an ordered list, you need to convert the item into a normal EditText
                *
                * */
            TableRow _row = (TableRow) activeView.getParent();
            TableLayout tableLayout = (TableLayout) _row.getParent();
            convertListToNormalText(tableLayout, tableLayout.indexOfChild(_row));
                /*
                *
                * this means the item was an ordered list, you need to convert the item into a normal EditText
                *
                * */
        } else if (currentFocus == ControlType.OL_LI && !isOrdered) {
                 /*
                *
                * this means the item was an ordered list, you need to convert the item into an unordered list
                *
                * */

            TableRow _row = (TableRow) activeView.getParent();
            TableLayout tableLayout = (TableLayout) _row.getParent();
            convertListToUnordered(tableLayout);
                  /*
                *
                * this means the item was an ordered list, you need to convert the item into an unordered list
                *
                * */
        } else if (isOrdered) {
                 /*
                *
                * it's a normal edit text, convert it into an ordered list. but first check index-1, if it's ordered, should follow the order no.
                * if it's unordered, convert all of em to ordered.
                *
                * */
            int indexOfActiveView = editorCore.getParentView().indexOfChild(editorCore.getActiveView());
            int index = editorCore.determineIndex(ControlType.OL_LI);
            //check if the active view has content
            View view = editorCore.getParentView().getChildAt(index);
            if (view != null) {
                ControlType type = editorCore.getControlType(view); //if then, get the type of that view, this behaviour is so, if that line has text,
                // it needs to be converted to list item
                if (type == ControlType.INPUT) {
                    String text = ((CustomEditText) view).getText().toString();  //get the text, if not null, replace it with list item
                    editorCore.getParentView().removeView(view);

                    if (index == 0) {
                        insertList(index, isOrdered, text);
                    } else if (editorCore.getControlType(editorCore.getParentView().getChildAt(indexOfActiveView - 1)) == ControlType.ol) {
                        TableLayout tableLayout = (TableLayout) editorCore.getParentView().getChildAt(indexOfActiveView - 1);
                        addListItem(tableLayout, isOrdered, text);
                    } else {
                        insertList(index, isOrdered, text);
                    }
                } else {
                    insertList(index, isOrdered, "");    //otherwise
                }
            } else {
                insertList(index, isOrdered, "");
            }


        } else {
                 /*
                *
                * it's a normal edit text, convert it into an un-ordered list
                *
                * */

            int index = editorCore.determineIndex(ControlType.UL_LI);
            //check if the active view has content
            View view = editorCore.getParentView().getChildAt(index);
            if (view != null) {
                ControlType type = editorCore.getControlType(view); //if then, get the type of that view, this behaviour is so, if that line has text,
                // it needs to be converted to list item
                if (type == ControlType.INPUT) {
                    String text = ((EditText) view).getText().toString();  //get the text, if not null, replace it with list item
                    editorCore.getParentView().removeView(view);
                    insertList(index, false, text);
                } else {
                    insertList(index, false, "");    //otherwise
                }
            } else {
                insertList(index, false, "");
            }
        }

    }


    private void rearrangeColumns(TableLayout tableLayout) {
        //TODO, make sure that if OL, all the items are ordered numerically
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            TextView bullet = (TextView) tableRow.findViewById(R.id.labelOrder);
            bullet.setText(String.valueOf(i + 1) + ".");
        }
    }


    public void validateAndRemoveLisNode(View view, EditorControl contentType) {
        /*
         *
         * If the person was on an active ul|li, move him to the previous node
         *
         */
        TableRow _row = (TableRow) view.getParent();
        TableLayout tableLayout = (TableLayout) _row.getParent();
        int indexOnList = tableLayout.indexOfChild(_row);
        if (indexOnList > 0) {
            /**
             * check if the index of the deleted row is <0, if so, move the focus to the previous li
             */
            TableRow focusrow = (TableRow) tableLayout.getChildAt(indexOnList - 1);
            final EditText editText = (EditText) focusrow.findViewById(R.id.editText);
            /**
             * Rearrange the nodes
             */
            if (contentType.type == ControlType.OL_LI)
                rearrangeColumns(tableLayout);
            if (editText.requestFocus()) {
                editText.setSelection(editText.getText().length());
            }
            tableLayout.removeView(_row);
        } else {
            /**
             * The removed row was first on the list. delete the list, and set the focus to previous element on the editor
             */
            editorCore.removeParent(tableLayout);
        }
    }

    public void setFocusToList(View view, int position) {
        TableLayout tableLayout = (TableLayout) view;
        int count = tableLayout.getChildCount();
        if (tableLayout.getChildCount() > 0) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(position == POSITION_START ? 0 : count - 1);
            if (tableRow != null) {
                EditText editText = (EditText) tableRow.findViewById(R.id.editText);
                if (editText.requestFocus()) {
                    editText.setSelection(editText.getText().length());
                }
            }
        }
    }


    public int getIndexOnEditorByEditText(CustomEditText customEditText) {
        TableRow tableRow = (TableRow) customEditText.getParent();
        TableLayout tableLayout = (TableLayout) tableRow.getParent();
        int indexOnTable = tableLayout.indexOfChild(tableRow);
        return indexOnTable;
    }

    public CustomEditText setFocusToSpecific(CustomEditText customEditText) {
        TableRow tableRow = (TableRow) customEditText.getParent();
        TableLayout tableLayout = (TableLayout) tableRow.getParent();
        int indexOnTable = tableLayout.indexOfChild(tableRow);
        if (indexOnTable == 0) {
            //what if index is 0, get the previous on edittext
        }
        TableRow prevRow = (TableRow) tableLayout.getChildAt(indexOnTable - 1);
        if (prevRow != null) {
            CustomEditText editText = (CustomEditText) tableRow.findViewById(R.id.editText);
            if (editText.requestFocus()) {
                editText.setSelection(editText.getText().length());
            }
            return editText;
        }
        return null;
    }
}
