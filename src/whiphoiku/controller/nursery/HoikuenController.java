package whiphoiku.controller.nursery;

import org.slim3.controller.Navigation;

import whiphoiku.controller.AbstractController;

public class HoikuenController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
        return forward("hoikuen.jsp");
    }
}
