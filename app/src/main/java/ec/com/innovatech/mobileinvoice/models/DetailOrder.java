package ec.com.innovatech.mobileinvoice.models;

public class DetailOrder {
    private String id;
    private String barCodeItem ;
    private String valueCatalogDriverUnit ;
    private String valueDriverUnit ;
    private String quantity ;
    private String description ;
    private String unitValue ;
    private String subTotal ;
    private String existsTax ;

    public DetailOrder(){
    }
    public DetailOrder(String id, String barCodeItem, String valueCatalogDriverUnit, String valueDriverUnit, String quantity, String description, String unitValue, String subTotal, String existsTax){
        this.id = id;
        this.barCodeItem = barCodeItem;
        this.valueCatalogDriverUnit = valueCatalogDriverUnit;
        this.valueDriverUnit = valueDriverUnit;
        this.quantity = quantity;
        this.description = description;
        this.unitValue = unitValue;
        this.subTotal = subTotal;
        this.existsTax = existsTax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setExistsTax(String existsTax) {
        this.existsTax = existsTax;
    }
}