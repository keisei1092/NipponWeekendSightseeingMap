package jp.ac.dendai.im.nipponweekendsightseeingmap;

import jp.ac.dendai.im.web.Item;

import java.util.List;

/**
 * Created by keisei on 12/22/15.
 */
public class Weather {
    public static final String WEATHER_FEEDS[] = {
        "http://rss.weather.yahoo.co.jp/rss/days/1400.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/3410.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/4410.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/5410.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/5110.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/5610.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/6200.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/6610.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/7200.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/8210.xml",
        "http://rss.weather.yahoo.co.jp/rss/days/9110.xml"
    };

    /**
     * 土曜日の天気をチェックする
     * 「晴のち雨」「晴時々雪」とかあるのでそれらは除外
     * TODO: 土曜日の天気チェックするだけじゃ週末観光マップじゃないよね
     * @return 晴れかどうかの配列
     */
    public boolean[] checkWeekend() {
        FeedFactory ff = new FeedFactory();
        boolean hoge[] = new boolean[7];

        for (int i = 0; i < WEATHER_FEEDS.length; i++) {
            List<Item> list = ff.fetchItems(WEATHER_FEEDS[i]);
            for (int r = 0; r < list.size(); r++) {
                if (list.get(r).getPubDate().contains("Sat")) {
                    if (
                            !list.get(r).getDescription().contains("雨")
                            && list.get(r).getDescription().contains("雪")
                            && list.get(r).getDescription().contains("晴")
                        ) {
                        hoge[i] = true;
                        continue;
                    }
                }
            }
            hoge[i] = false;
        }
        return hoge;
    }
}
