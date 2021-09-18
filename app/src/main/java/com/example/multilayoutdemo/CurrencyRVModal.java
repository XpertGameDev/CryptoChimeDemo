package com.example.multilayoutdemo;

public class CurrencyRVModal {

    String symbol, name;
    double price, pc24h;



    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPc24h() {
        return pc24h;
    }

    public void setPc24h(double pc24h) {
        this.pc24h = pc24h;
    }

    public CurrencyRVModal(String symbol, String name, double price, double pc24h) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.pc24h = pc24h;
    }
}
