package ec.com.innovatech.mobileinvoice.models;

import java.io.Serializable;

public class ImageItem implements Serializable {
    private Integer id;
    private String barCode;
    private String image;


    public ImageItem(){
    }

    public ImageItem(Integer id, String barCode, String image){
        this.id = id;
        this.barCode = barCode;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
