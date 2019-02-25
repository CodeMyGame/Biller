package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 9/10/2017.
 */

public class ServiceAddBean {
    public String title;
    public String des;
    public String cost;
    public ServiceAddBean(String title, String des, String cost) {

        this.title = title;
        this.des = des;
        this.cost = cost;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDes() {
        return this.des;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCost() {
        return this.cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
    public void setDes(String des) {
        this.des = des;
    }

}
