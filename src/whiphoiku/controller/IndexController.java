package whiphoiku.controller;

import org.slim3.controller.Navigation;

import whiphoiku.service.NewsService;

public class IndexController extends AbstractController {

    private NewsService newsService = new NewsService();

    @Override
    public Navigation run() throws Exception {
        requestScope("news", this.newsService.getNews4Top());
        return forward("index.jsp");
    }
}
