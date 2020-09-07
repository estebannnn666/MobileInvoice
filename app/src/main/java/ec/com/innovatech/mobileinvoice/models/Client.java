package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class Client  implements Serializable {
    private String buyType;
    private String type;
    private String document;
    private String name;
    private String address;
    private String city;
    private String telephone;
    private String email;


    public Client(){
    }

    public Client(String buyType, String type, String document, String name, String address, String city, String telephone, String email){
        this.buyType = buyType;
        this.type = type;
        this.document = document;
        this.name = name;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        this.email = email;
    }

    public String getBuyType() {
        return buyType;
    }

    public void setBuyType(String buyType) {
        this.buyType = buyType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
