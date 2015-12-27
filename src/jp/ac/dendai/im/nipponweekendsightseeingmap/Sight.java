package jp.ac.dendai.im.nipponweekendsightseeingmap;

import jp.ac.dendai.im.web.Item;

import java.util.*;

/**
 * Created by keisei on 12/22/15.
 */
public class Sight {
    public static final String SIGHT_FEEDS[] = {
        "http://guide.travel.co.jp/feed/archive/r1/",
        "http://guide.travel.co.jp/feed/archive/r2/",
        "http://guide.travel.co.jp/feed/archive/r3/",
        "http://guide.travel.co.jp/feed/archive/r4/",
        "http://guide.travel.co.jp/feed/archive/r5/",
        "http://guide.travel.co.jp/feed/archive/r6/",
        "http://guide.travel.co.jp/feed/archive/r7/",
        "http://guide.travel.co.jp/feed/archive/r8/",
        "http://guide.travel.co.jp/feed/archive/r9/",
        "http://guide.travel.co.jp/feed/archive/r10/",
        "http://guide.travel.co.jp/feed/archive/p47/"
    };

    /**
     * 週末が晴の場所リストから観光情報リストを返す
     * TODO: 観光情報リストは日付順に並べる
     * @param sunnySpotList 晴の地域番号リスト
     * @return 観光情報リスト
     */
    public List<String> fetchSightListFromAreasList(List<Integer> sunnySpotList) {
        FeedFactory ff = new FeedFactory();
        List<Item> sightItems = new ArrayList<>();
        if (sunnySpotList.size() == 0) {
            System.out.println("週末晴れの地点はありません。");
        }
        for (int i = 0; i < sunnySpotList.size(); i++) {
            System.out.println(
                "週末の "
                + convertNumberToAreaString(sunnySpotList.get(i))
                + " 地方は晴の模様です。観光情報を検索します。"
            );
            sightItems.addAll(
                    ff.fetchItems(SIGHT_FEEDS[sunnySpotList.get(i)])
            );
        }
        List<String> sightStrings = convertItemsListToStringsList(sunnySpotList, sightItems);
        return sightStrings;
    }

    /**
     * フィードアイテムのリストからタイトルを返す
     * @param sunnySpotList 晴の地域番号リスト
     * @param listItems フィードアイテムのリスト
     * @return "[(地域)] (タイトル)"のList
     */
    private List<String> convertItemsListToStringsList(List<Integer> sunnySpotList, List<Item> listItems) {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < sunnySpotList.size(); i++) {
            for (int j = 0; j < listItems.size(); j++) {
                listString.add(
                    convertNumberToAreaString(sunnySpotList.get(i))
                    + listItems.get(j).getTitle()
                );
            }
        }
        return listString;
    }

    /**
     * 地域番号を地域名に変換
     * @param i 地域番号
     * @return "[地域名]"
     */
    private String convertNumberToAreaString(int i) {
        String area = null;
        switch (i) {
            case 0:
                area = "北海道";
                break;
            case 1:
                area = "東北";
                break;
            case 2:
                area = "関東";
                break;
            case 3:
                area = "信州";
                break;
            case 4:
                area = "東海";
                break;
            case 5:
                area = "北陸";
                break;
            case 6:
                area = "近畿";
                break;
            case 7:
                area = "中国";
                break;
            case 8:
                area = "四国";
                break;
            case 9:
                area = "九州";
                break;
            case 10:
                area = "沖縄";
                break;

        }
        return "[" + area + "] ";
    }
}
