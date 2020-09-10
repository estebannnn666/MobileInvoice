package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class Enterprise{

    private String ruc;
    private String name;
    private String address;
    private String city;
    private String country;
    private String telephone;

    public Enterprise(){
    }

    public Enterprise(String ruc, String name, String address, String city, String country, String telephone){
        this.ruc = ruc;
        this.name = name;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        this.country = country;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
