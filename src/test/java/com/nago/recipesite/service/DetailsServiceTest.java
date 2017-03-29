package com.nago.recipesite.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.nago.recipesite.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class DetailsServiceTest {
    @Mock
    private UserService users;

    @InjectMocks
    private DetailService service;

    @Test(expected = UsernameNotFoundException.class)
    public void notFoundThrowsException() throws Exception {
        service.loadUserByUsername("not existing user");
    }

    @Test
    public void foundUsernameCreatesCredentials() throws Exception {
        when(users.findByUsername("user")).thenReturn(user);

        assertThat(service.loadUserByUsername("user").getUsername(), is("admin"));
    }

    private User user = new User("admin", "Tester1", "password", new String[] {"ROLE_ADMIN"});
}
