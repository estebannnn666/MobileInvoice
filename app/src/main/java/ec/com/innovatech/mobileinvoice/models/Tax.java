package ec.com.innovatech.mobileinvoice.models;

public class Tax {

    private String id;
    private String nameTax;
    private String descriptionTax;
    private String valueTax;

    public Tax(){
    }

    public Tax(String id, String nameTax, String descriptionTax, String valueTax){
        this.id = id;
        this.nameTax = nameTax;
        this.descriptionTax = descriptionTax;
        this.valueTax = valueTax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameTax() {
        return nameTax;
    }

    public void setNameTax(String nameTax) {
        this.nameTax = nameTax;
    }

    public String getDescriptionTax() {
        return descriptionTax;
    }

    public void setDescriptionTax(String descriptionTax) {
        this.descriptionTax = descriptionTax;
    }

    public String getValueTax() {
        return valueTax;
    }

    public void setValueTax(String valueTax) {
        this.valueTax = valueTax;
    }
}
