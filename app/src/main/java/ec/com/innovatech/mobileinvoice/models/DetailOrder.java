package ec.com.innovatech.mobileinvoice.models;

public class DetailOrder {
    private Integer idItem;
    private String barCodeItem ;
    private String valueCatalogDriverUnit ;
    private String valueDriverUnit ;
    private String quantity ;
    private String description ;
    private String unitValue ;
    private String subTotal ;
    private String discount;
    private String existsTax ;

    public DetailOrder(){
    }
    public DetailOrder(Integer idItem, String barCodeItem, String valueCatalogDriverUnit, String valueDriverUnit, String quantity, String description, String unitValue, String subTotal, String discount, String existsTax){
        this.idItem = idItem;
        this.barCodeItem = barCodeItem;
        this.valueCatalogDriverUnit = valueCatalogDriverUnit;
        this.valueDriverUnit = valueDriverUnit;
        this.quantity = quantity;
        this.description = description;
        this.unitValue = unitValue;
        this.subTotal = subTotal;
        this.discount = discount;
        this.existsTax = existsTax;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public String getBarCodeItem() {
        return barCodeItem;
    }

    public String getValueCatalogDriverUnit() {
        return valueCatalogDriverUnit;
    }

    public void setValueCatalogDriverUnit(String valueCatalogDriverUnit) {
        this.valueCatalogDriverUnit = valueCatalogDriverUnit;
    }

    public void setBarCodeItem(String barCodeItem) {
        this.barCodeItem = barCodeItem;
    }

    public String getValueDriverUnit() {
        return valueDriverUnit;
    }

    public void setValueDriverUnit(String valueDriverUnit) {
        this.valueDriverUnit = valueDriverUnit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getExistsTax() {
        return existsTax;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setExistsTax(String existsTax) {
        this.existsTax = existsTax;
    }
}
