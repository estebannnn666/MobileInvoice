package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class Zone implements Serializable {
    private String zoneName;
    private String zoneValueCode;
    private Integer zoneTypeCode;

    public Zone(){
    }

    public Zone(String zoneName, String zoneValueCode, Integer zoneTypeCode){
        this.zoneName = zoneName;
        this.zoneValueCode = zoneValueCode;
        this.zoneTypeCode = zoneTypeCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
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

    @Override
    public String toString(){
        return zoneName;
    }
}
