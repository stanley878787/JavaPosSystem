package models;

public class Product {

    private String product_id;
    private String category;
    private String name;
    private double price;
    private String photo;
    
    private String description;

    public Product(String id, String category, String name, double price, String photo, String description) {
        this.product_id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.photo = photo;
        this.description = description;
    }

    public Product() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Product [id=" + product_id + ", name=" + name + ", price=" + price + "]";
    }

}