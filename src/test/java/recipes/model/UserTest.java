package recipes.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.nago.recipesite.model.User;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
    private User user = new User();

    @Before
    public void setUp() throws Exception {
        user.setUsername("user");
        user.setPassword("password");
        user.setRoles(new String[]{"ROLE_USER"});
    }

    @Test
    public void isNotAdmin() throws Exception {
        assertFalse(user.isAdmin());
    }

    @Test
    public void isAdmin() throws Exception {
        user.setRoles(new String[]{"ROLE_USER", "ROLE_ADMIN"});

        assertTrue(user.isAdmin());
    }
}
