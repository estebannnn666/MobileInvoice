package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class Client  implements Serializable {
    private Integer id;
    private String buyType;
    private String type;
    private String document;
    private String firstName;
    private String secondName;
    private String firstLastName;
    private String secondLastName;
    private String name;
    private String address;
    private String city;
    private String telephone;
    private String email;
    private String zoneValueCode;
    private Integer zoneTypeCode;
    private String userId;

    public Client(){
    }

    public Client(Integer id, String buyType, String type, String document, String name, String firstName, String secondName, String firstLastName, String secondLastName, String address, String city, String telephone, String email, String zoneValueCode, Integer zoneTypeCode, String userId){
        this.id = id;
        this.buyType = buyType;
        this.type = type;
        this.document = document;
        this.name = name;
        this.firstName = firstName;
        this.secondName = secondName;
        this.firstLastName = firstLastName;
        this.secondLastName = secondLastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        this.email = email;
        this.zoneValueCode = zoneValueCode;
        this.zoneTypeCode = zoneTypeCode;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
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

    public String getZoneValueCode() {
        return zoneValueCode;
    }
    public void setZoneValueCode(String zoneValueCode) {
        this.zoneValueCode = zoneValueCode;
    }

    public Integer getZoneTypeCode() {
        return zoneTypeCode;
    }

    public void setZoneTypeCode(Integer zoneTypeCode) {
        this.zoneTypeCode = zoneTypeCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
