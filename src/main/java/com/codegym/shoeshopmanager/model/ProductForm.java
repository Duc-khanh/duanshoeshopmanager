package com.codegym.shoeshopmanager.model;

import org.springframework.web.multipart.MultipartFile;

public class ProductForm {
    private int id;
    private String name;
    private String description;
    private MultipartFile imageFile;

    public ProductForm() {
    }

    public ProductForm(int id, String name, String description, MultipartFile imageFile) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageFile = imageFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImage(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}

