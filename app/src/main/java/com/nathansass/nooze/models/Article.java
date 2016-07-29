package com.nathansass.nooze.models;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nathansass on 7/25/16.
 */
public class Article implements Serializable {
    String webUrl, headline, thumbNail;

    DateTime pubDate;

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            String dateString = jsonObject.getString("pub_date");
            if (dateString.equals("null")) {
                this.pubDate = new DateTime();
            } else {
                this.pubDate = new DateTime( jsonObject.getString("pub_date") );
            }

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbNail = "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                results.add( new Article(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public String getPubDate() {

        return pubDate.getMonthOfYear() + "/" + pubDate.getDayOfMonth() + "/" + pubDate.getYear();
    }

    public String getAgeOfArticleInDays() {
        long diff = new DateTime().getMillis() - pubDate.getMillis();

        long days = diff / (24 * 60 * 60 * 1000);

        return days + "";
    }
}
