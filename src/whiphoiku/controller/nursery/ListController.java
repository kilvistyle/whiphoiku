package whiphoiku.controller.nursery;

import org.slim3.controller.Navigation;

import whiphoiku.controller.AbstractController;

public class ListController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
        return forward("list.jsp");
    }
}
