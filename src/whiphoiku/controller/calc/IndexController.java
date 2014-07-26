package whiphoiku.controller.calc;

import org.slim3.controller.Navigation;

import whiphoiku.controller.AbstractController;

public class IndexController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
        return forward("index.jsp");
    }
}
