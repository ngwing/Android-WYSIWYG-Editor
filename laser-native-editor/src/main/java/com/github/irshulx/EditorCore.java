package com.github.irshulx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.Components.CustomEditText;
import com.github.irshulx.Components.DividerExtensions;
import com.github.irshulx.Components.HTMLExtensions;
import com.github.irshulx.Components.ImageExtensions;
import com.github.irshulx.Components.InputExtensions;
import com.github.irshulx.Components.ListItemExtensions;
import com.github.irshulx.Components.MapExtensions;
import com.github.irshulx.models.ControlType;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.Node;
import com.github.irshulx.models.Op;
import com.github.irshulx.models.RenderType;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class EditorCore extends LinearLayout {
    /*
    * EditText initializors
    */
    public String placeHolder = null;
    public String imageDescriptionHint = null;
    public String uploadingHint = null;
    public String uploadSuccessHint = null;
    public String uploadFailHint = null;
    public String firstLineWarningHint = null;
    public int dividerMargin = 0;

    public boolean renderFromHtml = false;
    /*
    * Divider initializors
    */
    private final String SHAREDPREFERENCE = "QA";
    private Context context;
    private Activity activity;
    protected LinearLayout parentView;
    private RenderType renderType;
    private Resources resources;
    private View activeView;
    private Gson gson;
    private Utilities utilities;
    private EditorListener listener;
    public final int MAP_MARKER_REQUEST = 20;
    public final int PICK_IMAGE_REQUEST = 1;
    private InputExtensions inputExtensions;
    private ImageExtensions imageExtensions;
    private ListItemExtensions listItemExtensions;
    private DividerExtensions dividerExtensions;
    private HTMLExtensions htmlExtensions;
    private MapExtensions mapExtensions;

    public EditorCore(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.activity = (Activity) context;
        this.setOrientation(VERTICAL);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        loadStateFromAttrs(attrs);
        utilities = new Utilities();
        this.resources = context.getResources();
        gson = new Gson();
        inputExtensions = new InputExtensions(this);
        imageExtensions = new ImageExtensions(this);
        listItemExtensions = new ListItemExtensions(this);
        dividerExtensions = new DividerExtensions(this);
        mapExtensions = new MapExtensions(this);
        htmlExtensions = new HTMLExtensions(this);
        this.parentView = this;
    }

    //region Getters_and_Setters

    /**
     *
     *
     * Exposed
     */

    /**
     * returns activity
     *
     * @return
     */
    public Activity getActivity() {
        return this.activity;
    }

    /**
     * used to get the editor node
     *
     * @return
     */
    public LinearLayout getParentView() {
        return this.parentView;
    }

    /**
     * Get number of childs in the editor
     *
     * @return
     */
    public int getParentChildCount() {
        return this.parentView.getChildCount();
    }

    /**
     * returns whether editor is set as Editor or Rendeder
     *
     * @return
     */
    public RenderType getRenderType() {
        return this.renderType;
    }

    /**
     * no idea what this is
     *
     * @return
     */
    public Resources getResources() {
        return this.resources;
    }

    /**
     * The current active view on the editor
     *
     * @return
     */
    public View getActiveView() {
        return this.activeView;
    }

    public void setActiveView(View view) {
        this.activeView = view;
    }

    public Utilities getUtilitiles() {
        return this.utilities;
    }

    public EditorListener getEditorListener() {
        return this.listener;
    }

    public void setEditorListener(EditorListener listener) {
        this.listener = listener;
    }

    /*
     *
     * Getters and setters for  extensions
     *
     */
    public InputExtensions getInputExtensions() {
        return this.inputExtensions;
    }

    public ImageExtensions getImageExtensions() {
        return this.imageExtensions;
    }

    public MapExtensions getMapExtensions() {
        return this.mapExtensions;
    }

    public HTMLExtensions getHtmlExtensions() {
        return this.htmlExtensions;
    }

    public ListItemExtensions getListItemExtensions() {
        return this.listItemExtensions;
    }

    public DividerExtensions getDividerExtensions() {
        return this.dividerExtensions;
    }

    //endregion

    private void loadStateFromAttrs(AttributeSet attributeSet) {
        if (attributeSet == null) {
            return; // quick exit
        }

        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.editor);

            this.placeHolder = a.getString(R.styleable.editor_placeholder);

            this.imageDescriptionHint = a.getString(R.styleable.editor_imageDescriptionHint);
            if (imageDescriptionHint == null || imageDescriptionHint.trim().length() == 0)
                imageDescriptionHint = context.getString(R.string.image_description_hint);

            this.uploadingHint = a.getString(R.styleable.editor_uploadingHint);
            if (uploadingHint == null || uploadingHint.trim().length() == 0)
                uploadingHint = context.getString(R.string.uploading_hint);

            this.uploadSuccessHint = a.getString(R.styleable.editor_uploadSuccessHint);
            if (uploadSuccessHint == null || uploadSuccessHint.trim().length() == 0)
                uploadSuccessHint = context.getString(R.string.upload_success);

            this.uploadFailHint = a.getString(R.styleable.editor_uploadFailHint);
            if (uploadFailHint == null || uploadFailHint.trim().length() == 0)
                uploadFailHint = context.getString(R.string.upload_fail);

            this.firstLineWarningHint = a.getString(R.styleable.editor_firstLineWarningHint);
            if (firstLineWarningHint == null || firstLineWarningHint.trim().length() == 0)
                firstLineWarningHint = context.getString(R.string.first_line_warning);

            String renderType = a.getString(R.styleable.editor_render_type);
            if (TextUtils.isEmpty(renderType)) {
                this.renderType = RenderType.Editor;
            } else {
                this.renderType = renderType.toLowerCase().equals("renderer") ? RenderType.Renderer : RenderType.Editor;
            }

        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }
    }

    /**
     * determine target index for the next insert,
     *
     * @param nextType
     * @return
     */
    public int determineIndex(ControlType nextType) {
        int size = this.parentView.getChildCount();
        if (this.renderType == RenderType.Renderer)
            return size;
        View view = this.activeView;
        if (view == null)
            return size;
        int currentIndex = this.parentView.indexOfChild(view);
        ControlType activeType = getControlType(view);
        if (activeType == ControlType.INPUT) {
            int length = ((EditText) this.activeView).getText().length();
            if (length > 0) {
                return nextType == ControlType.UL_LI || nextType == ControlType.OL_LI ? currentIndex : (currentIndex + 1);
            } else {
                return currentIndex;
            }
        } else if (activeType == ControlType.UL_LI || activeType == ControlType.OL_LI) {
            EditText edittext = (EditText) view.findViewById(R.id.txtText);
            if (edittext.getText().length() > 0) {

            }
            return size;
        } else {
            return size;
        }
    }

    public boolean containsStyle(List<EditorTextStyle> styles, EditorTextStyle style) {
        for (EditorTextStyle item : styles) {
            if (item == style) {
                return true;
            }
            continue;
        }
        return false;
    }

    public EditorControl updateTagStyle(EditorControl controlTag, EditorTextStyle style, Op op) {
        List<EditorTextStyle> styles = controlTag.controlStyles;
        if (op == Op.Delete) {
            int index = styles.indexOf(style);
            if (index != -1) {
                styles.remove(index);
                controlTag.controlStyles = styles;
            }
        } else {
            int index = styles.indexOf(style);
            if (index == -1) {
                styles.add(style);
            }
        }
        return controlTag;
    }

    public ControlType getControlType(int index) {
        View view = parentView.getChildAt(index);
        if (view == null)
            return null;
        return getControlType(view);
    }

    public ControlType getControlType(View view) {
        if (view == null)
            return null;
        EditorControl control = (EditorControl) view.getTag();
        if (control == null)
            return null;
        return control.type;
    }

    public EditorControl getControlTag(int index) {
        View view = parentView.getChildAt(index);
        return getControlTag(view);
    }

    public EditorControl getControlTag(View view) {
        if (view == null)
            return null;
        EditorControl control = (EditorControl) view.getTag();
        return control;
    }

    public EditorControl createTag(ControlType type) {
        EditorControl control = new EditorControl();
        control.type = type;
        control.controlStyles = new ArrayList<>();
        switch (type) {
            case hr:
            case img:
            case INPUT:
            case ul:
            case UL_LI:
        }
        return control;
    }

    public void deleteFocusedPrevious(EditText view) {
        int index = parentView.indexOfChild(view);
        if (index == 0)
            return;
        EditorControl contentType = (EditorControl) ((View) view.getParent()).getTag();
        /*
         *
         * If the person was on an active ul|li, move him to the previous node
         *
         */
        if (contentType != null && (contentType.type == ControlType.OL_LI || contentType.type == ControlType.UL_LI)) {
            listItemExtensions.validateAndRemoveLisNode(view, contentType);
            return;
        }

        View prevView = parentView.getChildAt(index - 1);
        EditorControl prevControl = (EditorControl) prevView.getTag();
        ControlType previousType = prevControl.type;

        /**
         * If its an image or map, do not delete edittext, as there is nothing to focus on after image
         */
//        if (previousType == ControlType.img || previousType == ControlType.map) {
//            return;
//        }
        /*
         *
         * If the person was on edittext,  had removed the whole text, we need to move into the previous line
         *
         */

        if (previousType == ControlType.ol || previousType == ControlType.ul) {
         /*
         *
         * previous node on the editor is a list, set focus to its inside
         *
         */
            this.parentView.removeView(view);
            listItemExtensions.setFocusToList(prevView, ListItemExtensions.POSITION_END);
            return;
        }

        if (previousType == ControlType.INPUT) {
            removeParent(view);
            return;
        }
        removeParent(prevView);
    }

    private void checkInputHint(int index) {
        View view = parentView.getChildAt(index);
        if (view == null)
            return;
        ControlType type = getControlType(view);
        if (type != ControlType.INPUT)
            return;

        String hint = placeHolder;
        if (index > 0) {
            View prevView = getParentView().getChildAt(index - 1);
            ControlType prevType = getControlType(prevView);
            if (prevType == ControlType.INPUT)
                hint = null;
        }
        TextView tv = (TextView) view;
        tv.setHint(hint);
    }

    public int removeParent(final View view) {
        int indexOfDeleteItem = parentView.indexOfChild(view);
        View nextItem = null;
        //remove hr if its on top of the delete field
        checkInputHint(indexOfDeleteItem);
//        if (dividerExtensions.deleteHr(Math.max(0, indexOfDeleteItem - 1)))
//            indexOfDeleteItem -= 1;

        if (view != this.activeView) {
            parentView.removeView(view);
            return indexOfDeleteItem;
        }

        for (int i = 0; i < indexOfDeleteItem; i++) {
            if (getControlType(parentView.getChildAt(i)) == ControlType.INPUT) {
                nextItem = parentView.getChildAt(i);
                continue;
            }
        }
        if (nextItem != null) {
            CustomEditText text = (CustomEditText) nextItem;
            if (text.requestFocusFromTouch()) {
                text.setSelection(text.getText().length());
            }
            this.activeView = nextItem;
        }

        this.parentView.post(new Runnable() {
            @Override
            public void run() {
                parentView.removeView(view);
            }
        });
        return indexOfDeleteItem;
    }


    public EditorContent getStateFromString(String content) {
        if (content == null) {
            content = getValue("editorState", "");
        }
        EditorContent deserialized = gson.fromJson(content, EditorContent.class);
        return deserialized;
    }

    public String getValue(String Key, String defaultVal) {
        SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCE, 0);
        return preferences.getString(Key, defaultVal);

    }

    public void putValue(String Key, String Value) {
        SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public String getContentAsSerialized() {
        EditorContent state = getContent();
        return serializeContent(state);
    }

    public String getContentAsSerialized(EditorContent state) {
        return serializeContent(state);
    }

    public EditorContent getContentDeserialized(String EditorContentSerialized) {
        EditorContent Deserialized = gson.fromJson(EditorContentSerialized, EditorContent.class);
        return Deserialized;
    }

    public String serializeContent(EditorContent content) {
        String serialized = gson.toJson(content);
        return serialized;
    }

    public EditorContent getContent() {

        if (this.renderType == RenderType.Renderer) {
            utilities.toastItOut("This option only available in editor mode");
            return null;
        }

        int childCount = this.parentView.getChildCount();
        EditorContent editorState = new EditorContent();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            Node node = new Node();
            View view = parentView.getChildAt(i);
            ControlType type = getControlType(view);
            node.type = type;
            node.content = new ArrayList<>();
            switch (type) {
                case INPUT:
                    EditText edittext = (EditText) view;
                    EditorControl tag = (EditorControl) view.getTag();
                    node.contentStyles = tag.controlStyles;
                    node.content.add(Html.toHtml(edittext.getText()));
                    list.add(node);
                    break;
                case img:
                    EditorControl imgTag = (EditorControl) view.getTag();
                    if (!TextUtils.isEmpty(imgTag.path)) {
                        node.content.add(imgTag.path);
                        Editable desc = ((EditText) view.findViewById(R.id.desc)).getText();
                        node.content.add(desc.length() > 0 ? desc.toString() : "");
                        list.add(node);
                    }
                    //field type, content[]
                    break;
                case hr:
                    list.add(node);
                    break;
                case ul:
                case ol:
                    TableLayout table = (TableLayout) view;
                    int rowCount = table.getChildCount();
                    for (int j = 0; j < rowCount; j++) {
                        View row = table.getChildAt(j);
                        EditText li = (EditText) row.findViewById(R.id.txtText);
                        node.content.add(Html.toHtml(li.getText()));
                    }
                    list.add(node);
                    break;
                case map:
                    EditorControl mapTag = (EditorControl) view.getTag();
                    Editable desc = ((CustomEditText) view.findViewById(R.id.desc)).getText();
                    node.content.add(mapTag.cords);
                    node.content.add(desc.length() > 0 ? desc.toString() : "");
                    list.add(node);
            }
        }
        editorState.nodes = list;
        return editorState;
    }

    public void renderEditor(EditorContent content) {
        this.parentView.removeAllViews();
        for (Node item : content.nodes) {
            switch (item.type) {
                case INPUT:
                    String text = item.content.get(0);
                    TextView view = inputExtensions.insertEditText(0, this.placeHolder, text);
                    if (item.contentStyles != null) {
                        for (EditorTextStyle style : item.contentStyles) {
                            inputExtensions.updateTextStyle(style, view);
                        }
                    }
                    break;
                case hr:
                    dividerExtensions.insertDivider();
                    break;
                case img:
                    String path = item.content.get(0);
                    String desc = item.content.get(1);
                    imageExtensions.loadImage(path, desc);
                    break;
                case ul:
                case ol:
                    TableLayout layout = null;
                    for (int i = 0; i < item.content.size(); i++) {
                        if (i == 0) {
                            layout = listItemExtensions.insertList(content.nodes.indexOf(item), item.type == ControlType.ol, item.content.get(i));
                        } else {
                            listItemExtensions.AddListItem(layout, item.type == ControlType.ol, item.content.get(i));
                        }
                    }
                    break;
                case map:
                    mapExtensions.insertMap(item.content.get(0), item.content.get(1), true);
                    break;
            }
        }
    }


    public boolean isLastRow(View view) {
        int index = this.parentView.indexOfChild(view);
        int length = this.parentView.getChildCount();
        return length - 1 == index;
    }

    public void renderHtml(String content) {
        renderFromHtml = true;
        htmlExtensions.parseHtml(content);
        renderFromHtml = false;

        checkLastInputHint();
        requestLastFocusView();
    }

    private void checkLastInputHint() {
        for (int index = getParentChildCount() - 1, prevIndex = index - 1; index >= 0; index--) {
            prevIndex = index - 1;
            if (!isInput(prevIndex)) {
                showNextInputHint(index);
            }
        }
    }

    private void requestLastFocusView() {
        for (int index = getParentChildCount() - 1; index >= 0; index--) {
            if (isInput(index)) {
                if (!isEmptyText(index) || !isInput(index - 1)) {
                    requestFocusView(index);
                    return;
                }
            }
        }
    }

    private boolean isEmptyText(int index) {
        View view = parentView.getChildAt(index);
        if (view == null || !isInput(index))
            return true;
        CustomEditText text = (CustomEditText) view;
        return text.getText().toString().trim().length() == 0;
    }

    private void requestFocusView(int index) {
        View view = parentView.getChildAt(index);
        if (view == null || !isInput(index))
            return;
        CustomEditText text = (CustomEditText) view;
        if (text.requestFocusFromTouch())
            text.setSelection(text.getText().length());
        this.activeView = view;
    }

    private boolean isInput(int index) {
        return getControlType(index) == ControlType.INPUT;
    }

    public void clearAllContents() {
        this.parentView.removeAllViews();

    }

    public void onBackspace(CustomEditText editText) {
        int len = editText.getText().length();
        int selection = editText.getSelectionStart();
        if (selection == 0)
            return;
        editText.getText().delete(selection, 1);

//                if(editText.requestFocus())
//                editText.setSelection(editText.getText().length());
    }

    public boolean onKey(View v, int keyCode, KeyEvent event, CustomEditText editText) {
        if (keyCode != KeyEvent.KEYCODE_DEL) {
            return false;
        }
        if (inputExtensions.isEditTextEmpty(editText)) {
            deleteFocusedPrevious(editText);
            int controlCount = getParentChildCount();
            if (controlCount == 1)
                return checkLastControl();
            return false;
        }
        int length = editText.getText().length();
        int selectionStart = editText.getSelectionStart();

        ControlType controlType = getControlType(this.activeView);
        CustomEditText nextFocus;
        if (selectionStart == 0 && length > 0) {
            if ((controlType == ControlType.UL_LI || controlType == ControlType.OL_LI)) {
                //now that we are inside the edittext, focus inside it
                int index = listItemExtensions.getIndexOnEditorByEditText(editText);
                if (index == 0) {
                    deleteFocusedPrevious(editText);
                }
            } else {
                int index = getParentView().indexOfChild(editText);
                if (index == 0)
                    return false;
                nextFocus = inputExtensions.getEditTextPrevious(index);
                deleteFocusedPrevious(editText);
                if (nextFocus != null)
                    nextFocus.setText(nextFocus.getText().toString() + editText.getText().toString());
            }
        }
        return false;
    }

    private boolean checkLastControl() {
        EditorControl control = getControlTag(getParentView().getChildAt(0));
        if (control == null)
            return false;
        switch (control.type) {
            case ul:
            case ol:
                parentView.removeAllViews();
                break;
        }

        return false;
    }

    public void showNextInputHint(int index) {
        View view = getParentView().getChildAt(index);
        ControlType type = getControlType(view);
        if (type != ControlType.INPUT)
            return;
        TextView tv = (TextView) view;
        tv.setHint(placeHolder);
    }

    public void onUpload(Bitmap image, Uri uri, String uuid) {
        if (listener == null)
            return;
        listener.onUpload(image, uri, uuid);

    }

    public void onInsertImage(String url, int index, ImageView imageView) {
        if (listener == null)
            return;
        listener.onInsertImage(url, index, imageView);
    }

    public class Utilities {
        public int[] getScreenDimension() {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int[] dimen = {width, height};
            return dimen;
        }

        public void toastItOut(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
