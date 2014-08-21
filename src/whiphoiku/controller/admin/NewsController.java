package whiphoiku.controller.admin;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.BeanUtil;
import org.slim3.util.StringUtil;

import com.google.appengine.api.datastore.Key;

import whiphoiku.model.news.Topic;
import whiphoiku.service.NewsService;

public class NewsController extends Controller {

    private NewsService newsService = new NewsService();

    @Override
    public Navigation run() throws Exception {
        if(isPost()){
            register();
        }
        if(isGet() &&  ! StringUtil.isEmpty((String)requestScope("dataId"))){
            Topic editTarget = newsService.getTopicById((String)requestScope("dataId"));
            requestScope("initData", editTarget);
        }
        requestScope("news", this.newsService.getNews4Admin());
        return forward("news.jsp");
    }

    private Key register(){
        Topic topic = new Topic();
        BeanUtil.copy(this.request, topic);
        return this.newsService.putTopic(topic, asString("dataId"));
    }
}
