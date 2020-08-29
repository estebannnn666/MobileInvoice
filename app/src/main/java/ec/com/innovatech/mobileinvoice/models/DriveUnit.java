package ec.com.innovatech.mobileinvoice.models;

public class DriveUnit {

    private String id;
    private String unitDriveValue;
    private String unitDriveValueCode;
    private String unitDriveTypeCode;
    private String unitDriveName;
    private String isDefault;

    public DriveUnit(){
    }

    public DriveUnit(String id, String unitDriveValue, String unitDriveValueCode, String unitDriveTypeCode, String unitDriveName, String isDefault){
        this.id = id;
        this.unitDriveValue = unitDriveValue;
        this.unitDriveValueCode = unitDriveValueCode;
        this.unitDriveTypeCode = unitDriveTypeCode;
        this.unitDriveName = unitDriveName;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitDriveValue() {
        return unitDriveValue;
    }

    public void setUnitDriveValue(String unitDriveValue) {
        this.unitDriveValue = unitDriveValue;
    }

    public String getUnitDriveValueCode() {
        return unitDriveValueCode;
    }

    public void setUnitDriveValueCode(String unitDriveValueCode) {
        this.unitDriveValueCode = unitDriveValueCode;
    }

    public String getUnitDriveTypeCode() {
        return unitDriveTypeCode;
    }

    public void setUnitDriveTypeCode(String unitDriveTypeCode) {
        this.unitDriveTypeCode = unitDriveTypeCode;
    }

    public String getUnitDriveName() {
        return unitDriveName;
    }

    public void setUnitDriveName(String unitDriveName) {
        this.unitDriveName = unitDriveName;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
