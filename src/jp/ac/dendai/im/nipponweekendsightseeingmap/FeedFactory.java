package jp.ac.dendai.im.nipponweekendsightseeingmap;

import jp.ac.dendai.im.web.Feed;
import jp.ac.dendai.im.web.Item;

import java.util.List;

/**
 * Created by keisei on 12/22/15.
 */
public class FeedFactory {
    public List<Item> fetchItems(String url) {
        Feed feed = new Feed();
        feed.setURL(url);
        feed.run();
        return feed.getItemList();
    }
}
