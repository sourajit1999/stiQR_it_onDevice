package com.applex.inc.stiqrit.ModelItems;

class Upload {
    private String mname;
    private String mimageurl;

    public Upload(){

    }

    public Upload(String name, String imageurl){
        if (name.trim().equals("")){
            name="No name";
        }
        mname= name;
        mimageurl=imageurl;
    }

    public String getName(){
        return mname;
    }
    public void setName(String name){
        mname=name;
    }
    public String getImageurl(){
        return mimageurl;
    }
    public void setImageurl(String imageurl){
        mimageurl=imageurl;
    }
}
