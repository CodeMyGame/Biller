package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 9/16/2017.
 */

public class HomeBean {
    public String title;
    public int  id;
    public HomeBean(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return this.id;
    }

    public void setId(String cost) {
        this.id =id;
    }

}
