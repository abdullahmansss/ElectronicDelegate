package muhammed.awad.electronicdelegate.Models;

public class MedicineModel
{
    String imageurl,info,name,price;

    public MedicineModel() {
    }

    public MedicineModel(String imageurl, String info, String name, String price) {
        this.imageurl = imageurl;
        this.info = info;
        this.name = name;
        this.price = price;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
