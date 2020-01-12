package com.applex.inc.stiqrit.ModelItems;


public class historyItems {

    private String mTitle;
    private String mDesc;
    private String mDate;



    private String mCode;
    public historyItems() {

    }

    public historyItems(String title, String desc, String date,String code) {
        mTitle = title;
        mDesc = desc;
        mDate = date;
        mCode = code;
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

    public String getmCode() {
        return mCode;
    }

}
