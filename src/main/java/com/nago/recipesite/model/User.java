package com.nago.recipesite.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nago.recipesite.core.BaseEntity;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class User extends BaseEntity {
  public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
  private String username;
  private String name;

  @ManyToMany
  private List<Recipe> FavoriteRecipes;

  @JsonIgnore
  private String password;

  @JsonIgnore
  private String[] roles;

  protected User() {
    super();
    FavoriteRecipes = new ArrayList<>();
  }

  public User(String username, String name, String password, String[] roles) {
    this();
    this.username = username;
    this.name = name;
    setPassword(password);
    this.roles = roles;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = PASSWORD_ENCODER.encode(password);
  }

  public List<Recipe> getFavoriteRecipes() {
    return FavoriteRecipes;
  }

  public void setFavoriteRecipes(List<Recipe> favoriteRecipes) {
    this.FavoriteRecipes = favoriteRecipes;
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }
}
