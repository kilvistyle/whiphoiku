package cashack.controller.api;

import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import whiphoiku.controller.api.GetHoikuInfoController;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class GetHoikuInfoControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.start("/api.getHoikuInfo");
        GetHoikuInfoController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));
    }
}
