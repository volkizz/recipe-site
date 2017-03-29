package com.nago.recipesite.dao;

import com.nago.recipesite.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = true)
public interface UserRepository extends CrudRepository<User, Long> {
  User findByUsername(String username);
  List<User> findAll();
}
