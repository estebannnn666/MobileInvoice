package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class HeaderInvoice implements Serializable {

    private Integer idInvoice;
    private String numberDocument ;
    private String invoiceReferenceCode ;
    private String dateDocument ;
    private String clientDocument ;
    private String clientName ;
    private String clientDirection ;
    private String clientPhone ;
    private String paidOut ;
    private String discount ;
    private String totalNotTax ;
    private String totalTax ;
    private String totalIva ;
    private String subTotal ;
    private String totalInvoice ;
    private String typeDocumentCode ;
    private String valueDocumentCode ;
    private String userId;
    private String seller;

    public HeaderInvoice(Integer idInvoice, String numberDocument, String invoiceReferenceCode, String dateDocument, String clientDocument, String clientName, String clientDirection, String clientPhone, String paidOut, String discount, String totalNotTax, String totalTax, String totalIva, String subTotal, String totalInvoice, String typeDocumentCode, String valueDocumentCode, String userId, String seller){
        this.idInvoice = idInvoice;
        this.numberDocument = numberDocument;
        this.invoiceReferenceCode = invoiceReferenceCode;
        this.dateDocument = dateDocument;
        this.clientDocument = clientDocument;
        this.clientName = clientName;
        this.clientDirection = clientDirection;
        this.clientPhone = clientPhone;
        this.paidOut = paidOut;
        this.discount = discount;
        this.totalNotTax = totalNotTax;
        this.totalTax = totalTax;
        this.totalIva = totalIva;
        this.subTotal = subTotal;
        this.totalInvoice = totalInvoice;
        this.typeDocumentCode = typeDocumentCode;
        this.valueDocumentCode = valueDocumentCode;
        this.userId = userId;
        this.seller = seller;
    }

    public HeaderInvoice(){
    }

    public Integer getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Integer idInvoice) {
        this.idInvoice = idInvoice;
    }

    public String getNumberDocument() {
        return numberDocument;
    }

    public void setNumberDocument(String numberDocument) {
        this.numberDocument = numberDocument;
    }

    public String getInvoiceReferenceCode() {
        return invoiceReferenceCode;
    }

    public void setInvoiceReferenceCode(String invoiceReferenceCode) {
        this.invoiceReferenceCode = invoiceReferenceCode;
    }

    public String getDateDocument() {
        return dateDocument;
    }

    public void setDateDocument(String dateDocument) {
        this.dateDocument = dateDocument;
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

    public String getPaidOut() {
        return paidOut;
    }

    public void setPaidOut(String paidOut) {
        this.paidOut = paidOut;
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

    public String getTypeDocumentCode() {
        return typeDocumentCode;
    }

    public void setTypeDocumentCode(String typeDocumentCode) {
        this.typeDocumentCode = typeDocumentCode;
    }

    public String getValueDocumentCode() {
        return valueDocumentCode;
    }

    public void setValueDocumentCode(String valueDocumentCode) {
        this.valueDocumentCode = valueDocumentCode;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}
