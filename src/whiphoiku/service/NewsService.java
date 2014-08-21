package whiphoiku.service;

import java.util.List;

import org.slim3.util.StringUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import whiphoiku.dao.news.TopicDao;
import whiphoiku.meta.news.TopicMeta;
import whiphoiku.model.news.Topic;


public class NewsService {

    private TopicDao topicDao = new TopicDao();

    public Key putTopic(Topic topic, String id){
        if(! StringUtil.isEmpty(id)){
            topic.setKey(KeyFactory.createKey("Topic", Long.parseLong(id)));
        }
        return topicDao.put(topic);
    }

    public Topic getTopicById(String id){
        Key key = KeyFactory.createKey("Topic", Long.parseLong(id));
        return topicDao.get(key);
    }

    public List<Topic> getNews4Admin(){
        return topicDao.findAllOrder((new TopicMeta()).openDate.desc);
    }

    public List<Topic> getNews4Top(){
        return topicDao.findOpenTopicHead(3, (new TopicMeta()).openDate.desc);
    }

    public void delTopic(String id) {
        Key key = KeyFactory.createKey("Topic", Long.parseLong(id));
        topicDao.delete(key);
    }
}
