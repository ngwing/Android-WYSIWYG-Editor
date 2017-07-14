package com.github.irshulx.Utilities;

public class ImageUrlWrapper {
    private String url;
    private int width;
    private int height;
    private String postfix;
    private String sizeString;

    public static ImageUrlWrapper wrap(String url) {
        ImageUrlWrapper wrapper = new ImageUrlWrapper();
        wrapper.doExtract(url);
        return wrapper;
    }

    public static String extract(String url) {
        int indexPrefix = url.lastIndexOf("[");

        if (indexPrefix == -1)
            return url;

        return url.substring(0, indexPrefix);
    }

    private void doExtract(String filepath) {
        if (filepath == null || filepath.trim().length() == 0)
            return;
        int indexPrefix = filepath.lastIndexOf("[");
        int indexComma = filepath.lastIndexOf(",");

        if (indexPrefix < 0 || indexComma < 0) {
            setUrl(filepath);
            return;
        }

        String url = filepath.substring(0, indexPrefix);
        setUrl(url);
        String w = filepath.substring(indexPrefix + 1, indexComma);
        String h = filepath.substring(indexComma + 1, filepath.length() - 1);
        try {
            width = Integer.parseInt(w);
            height = Integer.parseInt(h);
        } catch (NumberFormatException e) {
        }
        sizeString = "[" + width + "," + height + "]";
    }

    public String getUrl() {
        return url;
    }

    public String getSizeString() {
        return sizeString;
    }

    public void setUrl(String url) {
        this.url = url;
        int index = url.lastIndexOf(".");
        if (index < 0)
            return;
        this.postfix = url.substring(index);
    }

    public float getHeightWidthRatio() {
        return (float) height / (float) width;
    }


    public int getWidth() {
        return width;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "ImageUrlWrapper [url=" + url + ", width=" + width + ", height=" + height + "]";
    }
}
