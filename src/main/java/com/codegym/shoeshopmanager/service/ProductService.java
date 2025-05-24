package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;
    @Value("${file-upload}")
    private String uploadPath;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public void delete(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchByName(String productName) {
        return productRepository.searchByName(productName);
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            File dest = new File(uploadPath, fileName);
            file.transferTo(dest);
            return "/image/" + fileName;
        }
        return null;
    }
}
