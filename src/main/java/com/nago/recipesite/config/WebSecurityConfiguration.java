package com.nago.recipesite.config;

import com.nago.recipesite.model.User;
import com.nago.recipesite.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Autowired
  DetailService userDetailsService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(User.PASSWORD_ENCODER);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/signup").permitAll()
        .anyRequest().permitAll()
        .and()
        .formLogin()
        .loginPage("/login")
        .permitAll()
        .successHandler(loginSuccessHandler())
        .failureHandler(loginFailureHandler())
        .and()
        .logout()
        .permitAll()
        .logoutSuccessUrl("/login")
        .and()
        .csrf().disable();

    http.headers().frameOptions().disable();
  }

  public AuthenticationSuccessHandler loginSuccessHandler() {
    return (request, response, authentication) -> response.sendRedirect("/");
  }

  public AuthenticationFailureHandler loginFailureHandler() {
    return (request, response, exception) -> {
      response.sendRedirect("/login");
    };
  }

  @Bean
  public EvaluationContextExtension securityExtension() {
    return new EvaluationContextExtensionSupport() {
      @Override
      public String getExtensionId() {
        return "security";
      }

      @Override
      public Object getRootObject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new SecurityExpressionRoot(authentication) {
        };
      }
    };
  }

}
