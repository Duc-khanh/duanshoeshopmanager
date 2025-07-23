package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Category;
import java.util.List;

public interface ICategoryService {
    List<Category> findAll();
    Category findById(int id);
    void save(Category category);
    void delete(int id);
}
