package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 9/29/2017.
 */

public class MyOrderBean {
    public String customerName, invoiceNo, totalCost, orderNo,status,mobile;

    public MyOrderBean(String customerName, String invoiceNo, String totalCost, String orderNo,String status,String mobile) {
        this.customerName = customerName;
        this.invoiceNo = invoiceNo;
        this.totalCost = totalCost;
        this.orderNo = orderNo;
        this.status = status;
        this.mobile = mobile;
    }

    public String getCustomerName() {
        return this.customerName;
    }


    public String getInvoiceNo() {
        return this.invoiceNo;
    }

    public String getTotalCost() {
        return this.totalCost;
    }

    public String getOrderNo() {
        return this.orderNo;
    }
    public String getStatus() {
        return this.status;
    }
    public String getMobile() {
        return this.mobile;
    }


}
