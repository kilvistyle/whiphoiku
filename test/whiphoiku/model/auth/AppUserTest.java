package whiphoiku.model.auth;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class AppUserTest extends AppEngineTestCase {

    private AppUser model = new AppUser();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
