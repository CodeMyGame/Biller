package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 9/17/2017.
 */

public class Category1Bean {
    public String title;
    public String cost;
    public String category;
    public Category1Bean(String title,String cost,String category) {
        this.title = title;
        this.cost = cost;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return this.title;
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

}
