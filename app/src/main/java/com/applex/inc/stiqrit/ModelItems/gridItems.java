package com.applex.inc.stiqrit.ModelItems;

public class gridItems {
    private int mResourceImage;
    private String mName;
    private String mData;

    public gridItems() {

    }

    public gridItems(int ImageResource, String mName, String mData) {
        mResourceImage = ImageResource;
        this.mName = mName;
        this.mData = mData;
    }

    public int getmResourceImage() {
        return mResourceImage;
    }

    public String getmName() {
        return mName;
    }

    public String getmData() {
        return mData;
    }
}
