package com.example.amosh.newsfeedguardian;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QueryUtils {

    private QueryUtils() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Feed> extractFeeds(String json) {

        List<Feed> feeds = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(json);


            JSONObject response = jsonResponse.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            if (results.length() == 0) {
                return feeds;
            }

            for (int i = 0; i < results.length(); i++) {

                JSONObject feedInfo = results.getJSONObject(i);

                String title = feedInfo.getString("webTitle");
                String url = feedInfo.getString("webUrl");
                String section = feedInfo.getString("sectionName");
                String dated = feedInfo.getString("webPublicationDate");
                String date = formatDate(dated);
                Feed feed = new Feed(url, title, date, section);

                feeds.add(feed);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feeds;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }
}

