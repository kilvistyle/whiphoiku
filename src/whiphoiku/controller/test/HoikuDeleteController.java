package whiphoiku.controller.test;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import whiphoiku.service.master.NurseryService;

public class HoikuDeleteController extends Controller {

    public static final String GET_ZIPCODE = "zipcode";

    private NurseryService nurseryService = new NurseryService();

    @Override
    public Navigation run() throws Exception {
        if(isPost()){
        	this.nurseryService.delHoikuInfo((String)requestScope("dataId"));
        }
        return redirect("/test/hoikuMaster");
    }
}
