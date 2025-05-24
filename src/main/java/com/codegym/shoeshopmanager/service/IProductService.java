package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    List<Product> findAll();
    Product findById(Integer id);
    void save(Product product);
    void delete(Integer id);
    List<Product> searchByName(String productName);
    String uploadImage(MultipartFile file) throws IOException;

}
