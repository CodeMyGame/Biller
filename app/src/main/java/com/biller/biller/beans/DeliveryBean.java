package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 9/23/2017.
 */

public class DeliveryBean {
    public String title;
    public String count;
    public DeliveryBean(String title, String count) {

        this.title = title;
        this.count = count;
    }

    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
