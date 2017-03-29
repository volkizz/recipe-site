package recipes.web.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.model.User;
import com.nago.recipesite.web.controller.UserController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository users;

    @Before
    public void setUp() throws Exception {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(viewResolver).build();
    }

    @Test
    public void profileLoadsCorrectViewWithCurrentUser() throws Exception {
        mockMvc.perform(get("/profile"))
        .andExpect(model().attribute("user", nullValue()))
        .andExpect(view().name("profile"));
    }

    @Test
    public void userPageLoadsCorrectViewWithCorrectUser() throws Exception {
        when(users.findOne(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
        .andExpect(model().attribute("user", is(user)))
        .andExpect(view().name("profile"));
    }

    private User user = new User("user", "Tester", "password", new String[] {"ROLE_ADMIN"});
}
