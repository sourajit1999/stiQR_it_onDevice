package com.applex.inc.stiqrit.ModelItems;


public class historyItems {

    private String mTitle;
    private String mDesc;
    private String mDate;



    private String mCode;
    public historyItems() {

    }

    public historyItems(String code,String title, String desc, String date) {
        mCode = code;
        mTitle = title;
        mDesc = desc;
        mDate = date;
    }

    public String getmCode() {
        return mCode;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDesc() {
        return mDesc;
    }

    public String getmDate() {
        return mDate;
    }

}
