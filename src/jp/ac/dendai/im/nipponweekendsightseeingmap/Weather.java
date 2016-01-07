package jp.ac.dendai.im.nipponweekendsightseeingmap;

import jp.ac.dendai.im.web.Item;

import java.util.*;

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
     * 週末の天気をチェックする
     * 「晴のち雨」「晴時々雪」とかあるのでそれらは除外
     * TODO: 土曜日の天気チェックするだけじゃ週末観光マップじゃない
     * TODO: 土曜の天気と日曜の天気がどちらも晴の地域を取るようにする
     * @return 晴れかどうかの配列
     */
    public List<Integer> checkWeekend() {
        FeedFactory ff = new FeedFactory();
        List<Integer> sunnySpots = new ArrayList<>();

        // 地域ごと
        for (int i = 0; i < WEATHER_FEEDS.length; i++) {
            // 天気RSSのURLをたたいてListへ
            List<Item> list = ff.fetchItems(WEATHER_FEEDS[i]);
            // フィード参照して土曜日の天気が晴れだったらsunnySpotListに地域番号を入れる
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getTitle().contains("土")) {
                    if (
                            !list.get(j).getDescription().contains("雨")
                            && !list.get(j).getDescription().contains("雪")
                            && list.get(j).getDescription().contains("晴")
                        ) {
                        sunnySpots.add(i);
                        continue;
                    }
                }
            }
        }
        return sunnySpots;
    }
}
