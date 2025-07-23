package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;

import java.util.List;

public interface IFavoriteService {
    void addFavorite(User user, Product product);
    void removeFavorite(User user, Product product);
    List<Product> getFavoriteProducts(User user);
    boolean isFavorite(User user, Product product);

    int countFavoritesByUser(User user);
}

