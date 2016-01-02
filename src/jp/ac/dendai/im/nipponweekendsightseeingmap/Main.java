package jp.ac.dendai.im.nipponweekendsightseeingmap;

import java.util.*;

/**
 * Created by keisei on 12/22/15.
 */
public class Main {
    public static final int SIGHTLIST_LENGTH = 10;

    public static void main(String[] args) {
        Weather w = new Weather();
        Sight s = new Sight();

        List<Integer> sunnySpots = w.checkWeekend();
        List<String> sightInfo = s.fetchSightListFromAreasList(sunnySpots);

        sightInfo.forEach(System.out::println);
        // TODO: 晴れの地点をリストで持って、pubDateの昇順でソートして表示
    }
}
