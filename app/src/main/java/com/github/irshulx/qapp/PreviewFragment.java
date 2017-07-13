package com.github.irshulx.qapp;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.irshulx.Editor;
import com.github.irshulx.models.EditorContent;

import java.util.HashMap;
import java.util.Map;

public class PreviewFragment extends Fragment {
    private static final String SERIALIZED = "";

    private String mSerialized;

    private OnFragmentInteractionListener mListener;

    public PreviewFragment() {
    }
    public static PreviewFragment newInstance(String serialized) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED, serialized);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSerialized = getArguments().getString(SERIALIZED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_preview, container, false);

        Editor editor= (Editor)view.findViewById(R.id.renderer);
        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
        editor.setHeadingTypeface(headingTypeface);
        editor.setContentTypeface(contentTypeface);
        editor.setDividerLayout(R.layout.tmpl_divider_layout);
        editor.setEditorImageLayout(R.layout.tmpl_image_view);
        editor.setListItemLayout(R.layout.tmpl_list_item);
//        String content= mSerialized;
//        EditorContent dserialized= renderer.getContentDeserialized(content);
//        renderer.render(dserialized);
        String html = "<p data-tag=\"input\">哈哈哈哈哈哈哈哈哈哈</p><hr data-tag=\"hr\"/><hr data-tag=\"hr\"/><hr data-tag=\"hr\"/><hr data-tag=\"hr\"/><div data-tag=\"img\"><img src=\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\" /><br/></div><p data-tag=\"input\" ><b><i>呵呵红红火火恍恍惚</i></b></p>";

        editor.renderHtml(html);
        return  view;
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

    public Map<Integer,String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL,"fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.BOLD,"fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.ITALIC,"fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC,"fonts/Audiowide-Regular.ttf");
        return typefaceMap;
    }

    public Map<Integer,String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL,"fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD,"fonts/Lato-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC,"fonts/Lato-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC,"fonts/Lato-BoldItalic.ttf");
        return typefaceMap;
    }
}
