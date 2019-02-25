package com.biller.biller.beans;

/**
 * Created by Kapil Gehlot on 10/8/2017.
 */

public class ExpenditureBean {
    public String amount;
    public String description;
    public String date_of_expenditure;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_of_expenditure() {
        return date_of_expenditure;
    }

    public void setDate_of_expenditure(String date_of_expenditure) {
        this.date_of_expenditure = date_of_expenditure;
    }

    public ExpenditureBean(String amount, String description, String date_of_expenditure){
        this.amount = amount;
        this.description = description;
        this.date_of_expenditure = date_of_expenditure;
    }
}
