package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 11/4/2017.
 */

public class BalanceBean {
    public String sNo;
    public String cName;
    public String collection;
    public String debit;
    public String getsNo() {
        return sNo;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setsNo(String sNo) {

        this.sNo = sNo;
    }

    public BalanceBean(String sNo, String cName ,String collection,String debit){

        this.cName = cName;
        this.collection = collection;
        this.sNo = sNo;
        this.debit = debit;
    }
}
