package whiphoiku.controller.nursery;

import org.slim3.controller.Navigation;

import whiphoiku.controller.AbstractController;
import whiphoiku.service.master.NurseryService;

public class ListController extends AbstractController {

    private NurseryService nurseryService = new NurseryService();

    @Override
    public Navigation run() throws Exception {
    	
    	String ward = asString("ward");

        requestScope("hoikuList", nurseryService.getHoikuInfoAll());
        return forward("/nursery/list.jsp");
    }
}
