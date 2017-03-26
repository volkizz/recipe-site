package com.nago.recipesite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nago.recipesite.core.BaseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class User extends BaseEntity {
  public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
  private String name;
  private String username;

  @JsonIgnore
  private String password;
  @JsonIgnore
  private String[] roles;
  @OneToMany(mappedBy = "createdBy")
  private List<Recipe> ownRecipes;
  @ManyToMany
  private List<Recipe> favoriteRecipes;

  public User() {
    super();
    ownRecipes = new ArrayList<>();
    favoriteRecipes = new ArrayList<>();
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
    return favoriteRecipes;
  }

  public void setFavoriteRecipes(List<Recipe> favoriteRecipes) {
    this.favoriteRecipes = favoriteRecipes;
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }

  public List<Recipe> getOwnRecipes() {
    return ownRecipes;
  }

  public void setOwnRecipes(List<Recipe> ownRecipes) {
    this.ownRecipes = ownRecipes;
  }

  public void addOwnRecipe(Recipe recipe) {
    ownRecipes.add(recipe);
  }

  public void addFavoriteRecipe(Recipe recipe) {
    favoriteRecipes.add(recipe);
  }

  public void removeOwnRecipe(Recipe recipe) {
    ownRecipes.remove(recipe);
  }

  public void removeFavoriteRecipe(Recipe recipe) {
    favoriteRecipes.remove(recipe);
  }

  public boolean isAdmin() {
    for(int i = 0; i < roles.length; i++) {
      if(roles[i].equals("ROLE_ADMIN")) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    return username.equals(user.username) && user.getId() == this.getId();
  }
}