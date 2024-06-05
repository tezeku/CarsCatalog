package com.example.myapp;

public class CarItem {
    // Свойства ----------------------
    private String carThumbnail, carBrand, carModel, carInfo;
    private int carYear, price;
    private boolean expanded;
    // Свойства ----------------------

    // Геттеры -----------------------
    public String getCarThumbnail() { return carThumbnail; }
    public int getCarYear() {
        return carYear;
    }
    public int getPrice() { return price; }

    public String getCarBrand() {
        return carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarInfo() {
        return carInfo;
    }

    public Boolean isExpanded() { return expanded; }

    // Геттеры -----------------------

    public void setExpanded(Boolean expanded) { this.expanded = expanded; }

    // Конструктор
    public CarItem(String carThumbnail, String carBrand, String carModel, int carYear, int price, String carInfo) {
        this.carThumbnail = carThumbnail;
        this.carYear = carYear;
        this.price = price;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carInfo = carInfo;
        this.expanded = false;
    }

    public CarItem() {}
}
