package com.biller.biller.beans;



/**
 * Created by Kapil Gehlot on 11/5/2017.
 */

public class NewServiceBean {

    public String name;
    public NewServiceBean(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
