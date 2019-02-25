package com.biller.biller.beans;

import java.util.HashMap;

/**
 * Created by Kapil Gehlot on 9/30/2017.
 */

public class NewOrderBean {
    public String totalMoney;
    public String totalIteams;
    public String customerName;
    public String customerMobile;
    public String customerAddress;
    public String date_of_order;
    public HashMap services;
    public String invoiceNo;
    public String paymentStatus;
    public String details;
    public String kgOrQuantity;
    public NewOrderBean(String totalMoney,String totalIteams,String customerName,String customerMobile,
                        String customerAddress , HashMap services,String date_of_order,String invoiceNo,String paymentStatus,
                        String details,String kgOrQuantity) {
        this.totalMoney = totalMoney;
        this.totalIteams = totalIteams;
        this.customerName = customerName;
        this.customerMobile = customerMobile;
        this.customerAddress = customerAddress;
        this.services = services;
        this.date_of_order = date_of_order;
        this.invoiceNo = invoiceNo;
        this.paymentStatus = paymentStatus;
        this.details = details;
        this.kgOrQuantity = kgOrQuantity;
    }
}
