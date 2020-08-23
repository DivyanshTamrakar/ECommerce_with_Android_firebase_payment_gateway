package com.example.ecommerce_app.Model;

public class Products {


    private String category, date, image, pid, product_description, product_name, product_price, time;

    public Products() {

    }


    public Products(String category, String date, String image, String pid, String product_description, String product_name, String product_price, String time) {
        this.category = category;
        this.date = date;
        this.image = image;
        this.pid = pid;
        this.product_description = product_description;
        this.product_name = product_name;
        this.product_price = product_price;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
