package com.codegym.shoeshopmanager.model;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;


@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productID;

    private String productName;
    private String description;
    private Double price;
    private String image;
    private Integer stock;

    private Integer discountPercent;
    @Transient
    private MultipartFile imageFile;

    @ManyToOne
    @JoinColumn(name = "categoryID")
    private Category category;

    public double getDiscountedPrice() {
        if (discountPercent != null && discountPercent > 0) {
            return price - (price * discountPercent / 100.0);
        }
        return price;
    }

    public Product() {
    }

    public Product(Integer productID, String productName, String description, Double price, String image, Integer stock, Integer discountPercent, MultipartFile imageFile, Category category) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.image = image;
        this.stock = stock;
        this.discountPercent = discountPercent;
        this.imageFile = imageFile;
        this.category = category;
    }

    public Product(Integer productID, String productName, String description, Double price, String image, Integer stock, Category category) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.image = image;
        this.stock = stock;
        this.category = category;
    }

    public Product(String productName, String description, Double price, String image, Integer stock, Category category) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.image = image;
        this.stock = stock;
        this.category = category;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", stock=" + stock +
                ", imageFile=" + imageFile +
                ", category=" + category +
                '}';
    }
}
