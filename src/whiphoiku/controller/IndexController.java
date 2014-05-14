package whiphoiku.controller;

import org.slim3.controller.Navigation;

public class IndexController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
        return forward("index.jsp");
    }
}
