package com.codegym.shoeshopmanager.service;

public interface IRoleService<T> {
    Iterable<T> findAll();
}
