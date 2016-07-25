package com.nathansass.nooze.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.nathansass.nooze.models.Article;

import java.util.List;

/**
 * Created by nathansass on 7/25/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }
}
