package com.example.gesanidas.unipipmsplishopping;

/**
 * Created by gesanidas on 3/11/2017.
 */

public class Order
{

    //a model class for the user created orders
    String customerName;
    int productId;
    long timestamp;

    public Order(String customerName, int productId, long timestamp) {
        this.customerName = customerName;
        this.productId = productId;
        this.timestamp = timestamp;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
