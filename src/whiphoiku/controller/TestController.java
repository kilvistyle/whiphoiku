package whiphoiku.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class TestController extends Controller {

    @Override
    public Navigation run() throws Exception {
        return forward("test.jsp");
        // redirect to bootstrap sample html
//        return redirect("/bootstrap_sample/build/");
    }
}
