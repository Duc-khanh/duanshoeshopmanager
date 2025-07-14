package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Category;

import java.util.List;

public interface ICategoryService<T> {
    List<T> findAll();
    T findById(int id);
    void save(T t);
    void delete(int id);



}
