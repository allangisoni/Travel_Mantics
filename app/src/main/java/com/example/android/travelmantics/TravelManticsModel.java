package com.example.android.travelmantics;


public class TravelManticsModel {

    String imageView;
    String city;
    String amount;
    String hotel;

    public TravelManticsModel(){

    }

    public TravelManticsModel(  String amount,String city, String hotel, String imageView){


        this.amount = amount;
        this.city = city;
        this.hotel = hotel;
        this.imageView = imageView;
    }

    public String getImageView() {
        return imageView;
    }

    public void setImageView(String imageView) {
        this.imageView = imageView;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }


}
