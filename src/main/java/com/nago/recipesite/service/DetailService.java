package com.nago.recipesite.service;

import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DetailService implements UserDetailsService {
  @Autowired
  UserRepository users;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = users.findByUsername(username);
    if(user==null){
      throw new UsernameNotFoundException(username + "was not found");
    }
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        AuthorityUtils.createAuthorityList(user.getRoles())
    );
  }


}
