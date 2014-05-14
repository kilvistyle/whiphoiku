package whiphoiku.controller.auth;

import org.slim3.controller.Navigation;

import whiphoiku.controller.AbstractController;

public class CheckYourEmailController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
        return forward("checkYourEmail.jsp");
    }
}
