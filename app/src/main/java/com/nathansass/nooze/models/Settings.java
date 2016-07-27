package com.nathansass.nooze.models;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by nathansass on 7/27/16.
 */
public class Settings implements Serializable {

    public LocalDate beginDate;

    public ArrayList<String> newsCategories;

    public String sortBy;
    public Settings() {
        newsCategories = new ArrayList<>();
        beginDate = null;
    }

    public String getNewsCategories() {
        String newsCatsStr = newsCategories.toString();
        return newsCatsStr.substring(1, newsCatsStr.length()-1);
    }

    public String getBeginDate() {
        if (beginDate == null) {
            return "";
        } else {
            DecimalFormat df = new DecimalFormat("00");
            return beginDate.getYear() + "" + df.format(beginDate.getMonthOfYear()) + "" + df.format(beginDate.getDayOfMonth());
        }
    }

}
