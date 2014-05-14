package whiphoiku.controller.auth;

import org.slim3.controller.Navigation;

import whiphoiku.controller.AbstractController;

public class SuccessController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
        return forward("success.jsp");
    }
}
