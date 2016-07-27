package com.nathansass.nooze.models;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nathansass on 7/27/16.
 */
public class Settings implements Serializable {

    public LocalDate beginDate;
    public ArrayList<String> newsCategories;
    public Boolean sortNewest;

    public Settings(LocalDate beginDate, ArrayList<String> newsCategories, Boolean sortNewest ) {
        this.beginDate = beginDate;
        this.newsCategories = newsCategories;
        this.sortNewest = sortNewest;
    }
    public Settings() {

    }

}
