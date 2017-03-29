package com.nago.recipesite.dao;

import com.nago.recipesite.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
  User findByUsername(String username);
  List<User> findAll();
}
