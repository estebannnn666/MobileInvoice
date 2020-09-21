package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class HeaderOrder implements Serializable {

    private String idOrder ;
    private String orderDate ;
    private String deliveryDate ;
    private String statusOrder ;
    private String clientDocument ;
    private String clientName ;
    private String clientDirection ;
    private String clientPhone ;
    private String discount ;
    private String totalNotTax ;
    private String totalTax ;
    private String totalIva ;
    private String subTotal ;
    private String totalInvoice ;
    private String userId;

    public HeaderOrder(String numberDocument, String orderDate, String deliveryDate, String statusOrder, String clientDocument, String clientName, String clientDirection, String clientPhone, String discount, String totalNotTax, String totalTax, String totalIva, String subTotal, String totalInvoice, String userId){
        this.idOrder = numberDocument;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.statusOrder = statusOrder;
        this.clientDocument = clientDocument;
        this.clientName = clientName;
        this.clientDirection = clientDirection;
        this.clientPhone = clientPhone;
        this.discount = discount;
        this.totalNotTax = totalNotTax;
        this.totalTax = totalTax;
        this.totalIva = totalIva;
        this.subTotal = subTotal;
        this.totalInvoice = totalInvoice;
        this.userId = userId;
    }

    public HeaderOrder(){
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public String getClientDocument() {
        return clientDocument;
    }

    public void setClientDocument(String clientDocument) {
        this.clientDocument = clientDocument;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientDirection() {
        return clientDirection;
    }

    public void setClientDirection(String clientDirection) {
        this.clientDirection = clientDirection;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTotalNotTax() {
        return totalNotTax;
    }

    public void setTotalNotTax(String totalNotTax) {
        this.totalNotTax = totalNotTax;
    }

    public String getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(String totalTax) {
        this.totalTax = totalTax;
    }

    public String getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(String totalIva) {
        this.totalIva = totalIva;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(String totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
