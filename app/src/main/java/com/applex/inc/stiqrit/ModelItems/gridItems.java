package com.applex.inc.stiqrit.ModelItems;

import android.net.Uri;

public class gridItems {
    private Uri mResourceImage;


    private String mName;
    private String mData;

    public gridItems() {

    }

    public gridItems(Uri ImageResource, String mName, String mData) {
        mResourceImage = ImageResource;
        this.mName = mName;
        this.mData = mData;
    }

    public Uri getmResourceImage() {
        return mResourceImage;
    }

    public String getmName() {
        return mName;
    }

    public String getmData() {
        return mData;
    }

    public void setmResourceImage(Uri mResourceImage) {
        this.mResourceImage = mResourceImage;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmData(String mData) {
        this.mData = mData;
    }

}
