package com.github.irshulx.Components;

import android.widget.TableLayout;
import android.widget.TextView;

import com.github.irshulx.EditorCore;
import com.github.irshulx.Utilities.ImageUrlWrapper;
import com.github.irshulx.models.ControlType;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.HtmlTag;
import com.github.irshulx.models.Node;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.util.Iterator;
import java.util.List;

/**
 * Created by mkallingal on 5/25/2016.
 */
public class HTMLExtensions {
    EditorCore editorCore;

    public HTMLExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }

    public void parseHtml(String htmlString) {
        parseHtmlNode(htmlString);
    }

    private void parseHtmlNode(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        for (Element element : doc.body().children()) {
            if (!matchesTag(element.tagName()))
                continue;
            buildNode(element);
        }
    }

    private void buildNode(Element element) {
        String html;
        HtmlTag tag = HtmlTag.valueOf(element.tagName().toLowerCase());
        int count = editorCore.getParentView().getChildCount();
        switch (tag) {
            case br:
                editorCore.getInputExtensions().insertEditText(count, null, null);
                break;
            case hr:
                editorCore.getDividerExtensions().insertDivider();
                break;
            case h1:
            case h2:
            case h3:
                TextView header = renderHeader(tag, element);
                renderClass(element, header);
                break;
            case p:
                TextView tv = renderParagraph(element);
                renderClass(element, tv);
                break;
            case ul:
            case ol:
                TableLayout tableLayout = renderList(tag == HtmlTag.ol, element);
                renderClass(element, tableLayout);
                break;
            case img:
                renderImage(element);
                break;
            case audio:
                renderAudio(element);
                break;
            case div:
                html = element.html();
                parseHtml(html);
                break;
        }
    }


    private boolean hasChild(Element element) {
        Elements children = element.children();
        return children != null && children.size() > 0;
    }

    private TextView renderParagraph(Element element) {
        return editorCore.getInputExtensions().insertEditText(editorCore.getParentChildCount(), null, element.html());
    }


    private void renderClass(Element element, TextView textView) {
        String classAttr = element.attr("class");
        if (classAttr == null || classAttr.isEmpty())
            return;

        String[] classes = classAttr.split(" ");
        for (String classString : classes) {
            EditorTextStyle style = getStyle(classString.toUpperCase());
            if (style == null)
                continue;
            editorCore.getInputExtensions().updateTextStyle(style, textView);
        }
    }

    private EditorTextStyle getStyle(String classString) {
        EditorTextStyle style = null;
        try {
            style = EditorTextStyle.valueOf(classString.toUpperCase());
        } catch (IllegalArgumentException e) {

        }
        return style;
    }

    private void renderClass(Element element, TableLayout tableLayout) {
        String classAttr = element.attr("class");
        if (classAttr == null || classAttr.isEmpty())
            return;

        String[] classes = classAttr.split(" ");
        for (String classString : classes) {
            EditorTextStyle style = EditorTextStyle.valueOf(classString.toUpperCase());
            if (style == null)
                continue;
            editorCore.getListItemExtensions().updateListStyle(tableLayout, style);
        }
    }

    private void renderImage(Element element) {
        String url = element.attr("src");
        String description = element.attr("alt");
        int index = editorCore.getParentChildCount();
        editorCore.getImageExtensions().insertImage(url, description, index);
    }

    private void renderAudio(Element element) {
        String url = element.attr("src");
        editorCore.getAudioExtensions().insertAudio(url);
    }

    private TableLayout renderList(boolean isOrdered, Element element) {
        if (element.children().size() > 0) {
            Element li = element.child(0);
            String text = getHtmlSpan(li);
            TableLayout tableLayout = editorCore.getListItemExtensions().insertList(editorCore.getParentChildCount(), isOrdered, text);
            for (int i = 1; i < element.children().size(); i++) {
                li = element.child(i);
                text = getHtmlSpan(li);
                editorCore.getListItemExtensions().addListItem(tableLayout, isOrdered, text);
            }
            return tableLayout;
        }
        return null;
    }

    private TextView renderHeader(HtmlTag tag, Element element) {
        int count = editorCore.getParentView().getChildCount();
        String text = getHtmlSpan(element);
        TextView editText = editorCore.getInputExtensions().insertEditText(count, null, text);
        EditorTextStyle style = tag == HtmlTag.h1 ? EditorTextStyle.H1 : tag == HtmlTag.h2 ? EditorTextStyle.H2 : EditorTextStyle.H3;
        editorCore.getInputExtensions().updateTextStyle(style, editText);
        return editText;
    }

    private String getHtmlSpan(Element element) {
        Element el = new Element(Tag.valueOf("span"), "");
        el.attributes().put("style", element.attr("style"));
        el.html(element.html());
        return el.toString();
    }

    private boolean hasChildren(Element element) {
        return element.getAllElements().size() > 0;
    }


    private static boolean matchesTag(String test) {
        for (HtmlTag tag : HtmlTag.values()) {
            if (tag.name().equals(test)) {
                return true;
            }
        }
        return false;
    }

    private String getTemplateHtml(ControlType child) {
        String template = null;
        switch (child) {
            case INPUT:
                template = "<{{$tag}} class = \"{{$class}}\" data-tag=\"input\" >{{$content}}</{{$tag}}>";
                break;
            case hr:
                template = "<hr data-tag=\"hr\"/>";
                break;
            case img:
                template = "<div class=\"img\" data-tag=\"img\"><img src=\"{{$content}}\" alt=\"{{$desc}}\"/></div>";
                break;
            case audio:
                template = "<div class = \"audio\" data-tag=\"audio\"><audio class = \"data\" src=\"{{$content}}\" preload=\"false\"/></div>";
                break;
            case map:
                template = "<div class=\"block\" data-tag=\"map\"><img src=\"{{$content}}\" /><span text-align:'center'>{{$desc}}</span></div>";
                break;
            case ol:
                template = "<ol class = \"{{$class}}\" data-tag=\"ol\">{{$content}}</ol>";
                break;
            case ul:
                template = "<ul class = \"{{$class}}\" data-tag=\"ul\">{{$content}}</ul>";
                break;
            case OL_LI:
            case UL_LI:
                template = "<li>{{$content}}</li>";
                break;
        }
        return template;
    }

    private String getInputHtml(Node item) {
        boolean isParagraph = true;
        String tmpl = getTemplateHtml(ControlType.INPUT);
        //  CharSequence content= android.text.Html.fromHtml(item.content.get(0)).toString();
        //  CharSequence trimmed= editorCore.getInputExtensions().noTrailingwhiteLines(content);
        String trimmed = Jsoup.parse(item.content.get(0)).body().select("p").html();
        StringBuilder textClassStringBuilder = new StringBuilder();
        if (item.styles.size() > 0) {
            for (EditorTextStyle style : item.styles) {
                switch (style) {
                    case BOLD:
                    case ITALIC:
                    case INDENT:
                    case OUTDENT:
                        append(textClassStringBuilder, style);
                        break;
                    case H1:
                        tmpl = tmpl.replace("{{$tag}}", "h1");
                        isParagraph = false;
                        break;
                    case H2:
                        tmpl = tmpl.replace("{{$tag}}", "h2");
                        isParagraph = false;
                        break;
                    case H3:
                        tmpl = tmpl.replace("{{$tag}}", "h3");
                        isParagraph = false;
                        break;
                    case NORMAL:
                        tmpl = tmpl.replace("{{$tag}}", "p");
                        isParagraph = true;
                        break;
                }
            }
            if (isParagraph)
                tmpl = tmpl.replace("{{$tag}}", "p");
            tmpl = tmpl.replace("{{$content}}", trimmed);
            tmpl = tmpl.replace("{{$class}}", textClassStringBuilder.toString());
            return tmpl;
        }
        tmpl = tmpl.replace("{{$tag}}", "p");
        tmpl = tmpl.replace("{{$content}}", trimmed);
        tmpl = tmpl.replace("{{$class}}", "");
        return tmpl;
    }

    private void append(StringBuilder stringBuilder, EditorTextStyle style) {
        if (stringBuilder.length() > 0)
            stringBuilder.append(" ");
        stringBuilder.append(style.name().toLowerCase());
    }

    public String getContentAsHTML() {
        return getContentAsHTML(editorCore.getContent());
    }

    public String getContentAsHTML(EditorContent content) {
        StringBuilder htmlBlock = new StringBuilder();
        String html;
        for (Node item : content.nodes) {
            switch (item.type) {
                case INPUT:
                    html = getInputHtml(item);
                    htmlBlock.append(html);
                    break;
                case img:
                    ImageUrlWrapper wrapper = ImageUrlWrapper.wrap(item.content.get(0));
                    String templateHtml = getTemplateHtml(item.type);
                    templateHtml = templateHtml.replace("{{$content}}", wrapper.getUrl());
                    String desc = item.content.get(1);
                    if (desc != null)
                        templateHtml = templateHtml.replace("{{$desc}}", desc);
                    htmlBlock.append(templateHtml);
                    break;
                case audio:
                    String path = item.content.get(0);
                    htmlBlock.append(getTemplateHtml(item.type).replace("{{$content}}", path));
                    break;
                case hr:
                    htmlBlock.append(getTemplateHtml(item.type));
                    break;
                case map:
                    htmlBlock.append(getTemplateHtml(item.type).replace("{{$content}}", editorCore.getMapExtensions().getCordsAsUri(item.content.get(0))).replace("{{$desc}}", item.content.get(1)));
                    break;
                case ul:
                case ol:
                    htmlBlock.append(getListAsHtml(item));
                    break;
            }
        }
        return htmlBlock.toString();
    }

    public String getContentAsHTML(String editorContentAsSerialized) {
        EditorContent content = editorCore.getContentDeserialized(editorContentAsSerialized);
        return getContentAsHTML(content);
    }

    private String getListAsHtml(Node item) {
        int count = item.content.size();
        String templateParent = getTemplateHtml(item.type);
        StringBuilder childBlock = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String templateLi = getTemplateHtml(item.type == ControlType.ul ? ControlType.UL_LI : ControlType.OL_LI);
            String trimmed = Jsoup.parse(item.content.get(i)).body().select("p").html();
            templateLi = templateLi.replace("{{$content}}", trimmed);
            childBlock.append(templateLi);
        }
        templateParent = templateParent.replace("{{$content}}", childBlock.toString());
        String cssClass = getCSSClass(item);
        templateParent = templateParent.replace("{{$class}}", cssClass);
        return templateParent;
    }

    private String getCSSClass(Node item) {
        StringBuilder sb = new StringBuilder();
        List<EditorTextStyle> styles = item.styles;
        if (styles == null || styles.isEmpty())
            return "";
        Iterator<EditorTextStyle> iter = styles.iterator();
        EditorTextStyle style;
        while (iter.hasNext()) {
            style = iter.next();
            sb.append(style.name().toLowerCase());
            if (iter.hasNext())
                sb.append(" ");
        }
        return sb.toString();
    }
}
