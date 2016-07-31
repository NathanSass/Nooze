package com.nathansass.nooze.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nathansass.nooze.R;

/**
 * Created by nathansass on 7/30/16.
 */
public class ItemArticleNoImageResult extends RecyclerView.ViewHolder {

    TextView tvTitle, tvDaysOld, tvSnippet;
    public ItemArticleNoImageResult(View view) {
        super(view);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDaysOld = (TextView) view.findViewById(R.id.tvDaysOld);
        tvSnippet = (TextView) view.findViewById(R.id.tvSnippet);
    }
}
