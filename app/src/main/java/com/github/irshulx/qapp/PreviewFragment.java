package com.github.irshulx.qapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.irshulx.Components.ImageExtensions;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.Utilities.ImageUrlWrapper;
import com.github.irshulx.models.EditorContent;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PreviewFragment extends Fragment {
    private static final String SERIALIZED = "SERIALIZED";
    private static final String HTML = "HTML";

    private String html;

    private OnFragmentInteractionListener mListener;

    public PreviewFragment() {
    }

    public static PreviewFragment newInstance(String serialized, String html) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED, serialized);
        args.putString(HTML, html);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            html = getArguments().getString(HTML);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        final Editor editor = (Editor) view.findViewById(R.id.renderer);
        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
//        editor.setHeadingTypeface(headingTypeface);
//        editor.setContentTypeface(contentTypeface);
        editor.setDividerLayout(R.layout.tmpl_divider_layout);
        editor.setEditorImageLayout(R.layout.tmpl_image_view);
        editor.setListItemLayout(R.layout.tmpl_list_item);
        editor.setEditorListener(new EditorListener() {
            @Override
            public void onTextChanged(EditText editText, Editable text) {

            }

            @Override
            public void onUpload(Bitmap image, Uri uri, String uuid) {

            }

            @Override
            public void onInsertImage(String url, int index, ImageView imageView) {
                glideShow(url, imageView);
            }
        });
        editor.renderHtml(html);

        return view;
    }

    private void glideShow(String url, ImageView imageView) {
        new DownloadImageTask(imageView).execute(url);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            ImageUrlWrapper wrapper = ImageUrlWrapper.wrap(urldisplay);
            String url = wrapper.getUrl();
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Map<Integer, String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Audiowide-Regular.ttf");
        return typefaceMap;
    }

    public Map<Integer, String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Lato-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Lato-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Lato-BoldItalic.ttf");
        return typefaceMap;
    }
}
