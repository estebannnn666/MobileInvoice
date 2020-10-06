package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class Item implements Serializable {
    private Integer id;
    private String barCode;
    private String nameItem;
    private String cost;
    private String priceWholesaler;
    private String priceRetail;
    private String stock;
    private String commissionPercentage;


    public Item(){
    }

    public Item(Integer id, String barCode, String nameItem, String cost, String priceWholesaler, String priceRetail, String stock, String commissionPercentage){
        this.id = id;
        this.barCode = barCode;
        this.nameItem = nameItem;
        this.cost = cost;
        this.priceWholesaler = priceWholesaler;
        this.priceRetail = priceRetail;
        this.stock = stock;
        this.commissionPercentage = commissionPercentage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPriceWholesaler() {
        return priceWholesaler;
    }

    public void setPriceWholesaler(String priceWholesaler) {
        this.priceWholesaler = priceWholesaler;
    }

    public String getPriceRetail() {
        return priceRetail;
    }

    public void setPriceRetail(String priceRetail) {
        this.priceRetail = priceRetail;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(String commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }
}
