package com.nago.recipesite.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

@Configuration
public class UploadingFileConfig {

  @Bean
  public CommonsMultipartResolver multipartResolver() {
    final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
    commonsMultipartResolver.setMaxUploadSize(-1);
    return commonsMultipartResolver;
  }

  @Bean
  public FilterRegistrationBean multipartFilterRegistrationBean() {
    final MultipartFilter multipartFilter = new MultipartFilter();
    final FilterRegistrationBean
        filterRegistrationBean =
        new FilterRegistrationBean(multipartFilter);
    filterRegistrationBean
        .addInitParameter("multipartResolverBeanName", "commonsMultipartResolver");
    return filterRegistrationBean;
  }
}
