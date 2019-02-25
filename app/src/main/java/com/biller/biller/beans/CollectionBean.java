package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 10/18/2017.
 */

public class CollectionBean {
    public String sNo;
    public String cName;
    public String collection;

    public String getsNo() {
        return sNo;
    }

    public String getcName() {
        return cName;
    }

    public String getCollection() {
        return collection;
    }


    public CollectionBean(String sNo, String cName ,String collection){

        this.cName = cName;
        this.collection = collection;
        this.sNo = sNo;
    }
}
