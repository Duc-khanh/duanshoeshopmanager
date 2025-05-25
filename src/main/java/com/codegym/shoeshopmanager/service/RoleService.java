package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Role;
import com.codegym.shoeshopmanager.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IRoleService<Role> {
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public Iterable<Role> findAll() {
        return roleRepository.findAll();
    }
}
