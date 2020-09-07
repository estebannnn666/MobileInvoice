package ec.com.innovatech.mobileinvoice.models;

public class DriverUnitSpinner {
    private int id;
    private String nameDriverUnit;

    public DriverUnitSpinner(int id, String nameDriverUnit) {
        this.id = id;
        this.nameDriverUnit = nameDriverUnit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameDriverUnit() {
        return nameDriverUnit;
    }

    public void setNameDriverUnit(String nameDriverUnit) {
        this.nameDriverUnit = nameDriverUnit;
    }

    @Override
    public String toString() {
        return nameDriverUnit;
    }
}
