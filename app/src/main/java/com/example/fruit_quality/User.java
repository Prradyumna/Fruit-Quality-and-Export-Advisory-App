package com.example.fruit_quality;



public class User {


    String Name,Address,Mobileno,imageurl;

    public User() {
    }

    public User(String name, String address, String mobileno, String imageurl) {
        Name = name;
        Address = address;
        Mobileno = mobileno;
        this.imageurl = imageurl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMobileno() {
        return Mobileno;
    }

    public void setMobileno(String mobileno) {
        Mobileno = mobileno;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
