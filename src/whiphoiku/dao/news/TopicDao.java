package whiphoiku.dao.news;

import java.util.Calendar;
import java.util.List;

import org.slim3.datastore.DaoBase;
import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.SortCriterion;

import whiphoiku.meta.news.TopicMeta;
import whiphoiku.model.news.Topic;

public class TopicDao extends DaoBase<Topic>{

    private TopicMeta topicMeta = new TopicMeta();

    public List<Topic> findAllOrder(SortCriterion... s){
        ModelQuery<Topic> query = query();
        for (SortCriterion sortCriterion : s) {
            query.sort(sortCriterion);
        }
        return query.asList();
    }

    public List<Topic> findOpenTopicHead(int limits, SortCriterion... s){
        Calendar today = Calendar.getInstance();
        Calendar cld = Calendar.getInstance();
        today.setTimeInMillis(0);
        today.set(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld.get(Calendar.DAY_OF_MONTH));

        ModelQuery<Topic> query = query();
        query.filter(topicMeta.openStatus.equal(Topic.OPEN_STATUS.PUBLIC));
        query.filter(topicMeta.openDate.lessThan(today.getTime()));
        query.filter(topicMeta.openDate.isNotNull());
        for (SortCriterion sortCriterion : s) {
            query.sort(sortCriterion);
        }
        return query.limit(limits).asList();
    }
}
