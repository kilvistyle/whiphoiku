package whiphoiku.model.mail;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class SendMailTest extends AppEngineTestCase {

    private SendMail model = new SendMail();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
