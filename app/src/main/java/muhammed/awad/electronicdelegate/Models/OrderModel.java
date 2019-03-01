package muhammed.awad.electronicdelegate.Models;

public class OrderModel
{
    String order_image,order_name,pharmacy_name,order_price,order_location,order_quantity;

    public OrderModel() {
    }

    public OrderModel(String order_image, String order_name, String pharmacy_name, String order_price, String order_location, String order_quantity) {
        this.order_image = order_image;
        this.order_name = order_name;
        this.pharmacy_name = pharmacy_name;
        this.order_price = order_price;
        this.order_location = order_location;
        this.order_quantity = order_quantity;
    }

    public String getOrder_image() {
        return order_image;
    }

    public void setOrder_image(String order_image) {
        this.order_image = order_image;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getPharmacy_name() {
        return pharmacy_name;
    }

    public void setPharmacy_name(String pharmacy_name) {
        this.pharmacy_name = pharmacy_name;
    }

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }

    public String getOrder_location() {
        return order_location;
    }

    public void setOrder_location(String order_location) {
        this.order_location = order_location;
    }

    public String getOrder_quantity() {
        return order_quantity;
    }

    public void setOrder_quantity(String order_quantity) {
        this.order_quantity = order_quantity;
    }
}
