package com.example.amosh.newsfeedguardian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private QueryUtils() {
    }

    public static List<Feed> extractFeeds(String json) {

        List<Feed> feeds = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(json);

            if (jsonResponse.getInt("totalItems") == 0) {
                return feeds;
            }

            JSONArray jsonArray = jsonResponse.getJSONArray("response");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject feedObject = jsonArray.getJSONObject(i);


                JSONObject feedInfo = feedObject.getJSONObject("results");
                String title = feedInfo.getString("webTitle");
                String url = feedInfo.getString("webUrl");
                String section = feedInfo.getString("sectionName");
                String date = feedInfo.getString("webPublicationDate");
                Feed feed = new Feed(url, title, date, section);

                feeds.add(feed);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feeds;
    }
}

