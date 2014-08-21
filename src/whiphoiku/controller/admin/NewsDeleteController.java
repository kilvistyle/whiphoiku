package whiphoiku.controller.admin;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import whiphoiku.service.NewsService;

public class NewsDeleteController extends Controller {

    private NewsService newsService = new NewsService();

    @Override
    public Navigation run() throws Exception {
        if(isPost()){
            this.newsService.delTopic((String)requestScope("dataId"));
        }
        return redirect("/admin/news");
    }
}
