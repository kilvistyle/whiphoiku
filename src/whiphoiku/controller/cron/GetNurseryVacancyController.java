package whiphoiku.controller.cron;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import whiphoiku.service.master.NurseryService;

public class GetNurseryVacancyController extends Controller {

    private NurseryService nurseryService = new NurseryService();

    @Override
    public Navigation run() throws Exception {
        nurseryService.setNurseryVacancyInfo();
        return null;
    }
}
