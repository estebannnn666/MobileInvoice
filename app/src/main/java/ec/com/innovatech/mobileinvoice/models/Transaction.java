package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    private Integer id;
    private String type;
    private String description;
    private Double valueTransaction;
    private String dateTransaction;
    private String userId;

    public Transaction(){
    }

    public Transaction(Integer id, String type, String description, Double valueTransaction, String userId, String dateTransaction){
        this.id = id;
        this.type = type;
        this.description = description;
        this.valueTransaction = valueTransaction;
        this.dateTransaction = dateTransaction;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getValueTransaction() {
        return valueTransaction;
    }

    public void setValueTransaction(Double valueTransaction) {
        this.valueTransaction = valueTransaction;
    }

    public String getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(String dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
