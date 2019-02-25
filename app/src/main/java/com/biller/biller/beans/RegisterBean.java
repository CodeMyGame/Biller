package com.biller.biller.beans;

import java.util.List;

/**
 * Created by Kapil Gehlot on 9/10/2017.
 */

public class RegisterBean {
    public String fullname;
    public String contact;
    public String address;
    public String firmname;
    public String city;
    public String pin;
    public String bussiness;
    public String subscription;
    public List<ServiceAddBean> services;
    public String date_of_subsscription;
    public String end_date_of_subsscription;
    public String email;
    public String password;
    public String alphabet;
    public String count;
    public RegisterBean(String email, String password, String name, String contact, String address, String firmname,
                        String city, String pin, String bussiness, String date_of_subsscription, String end_date_of_subsscription
            , String subscription, List<ServiceAddBean> services, String alphabet, String count) {
        this.email = email;
        this.password = password;
        this.fullname = name;
        this.contact = contact;
        this.firmname = firmname;
        this.address = address;
        this.city = city;
        this.pin = pin;
        this.bussiness = bussiness;
        this.services = services;
        this.subscription = subscription;
        this.date_of_subsscription = date_of_subsscription;
        this.end_date_of_subsscription = end_date_of_subsscription;
        this.alphabet = alphabet;
        this.count = count;
    }
}
