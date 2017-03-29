package com.nago.recipesite.service;


import com.nago.recipesite.model.User;

import java.util.List;

public interface UserService {
    void save(User user);
    void save(List<User> users);
    List<User> findAll();
    User findOne(Long id);
    User findByUsername(String username);
}
