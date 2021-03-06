package com.nathansass.nooze.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nathansass.nooze.R;

/**
 * Created by nathansass on 7/30/16.
 */
public class ItemArticleResult extends RecyclerView.ViewHolder {


    TextView tvTitle, tvDaysOld;
    ImageView imageView;
    public ItemArticleResult(View view) {
        super(view);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDaysOld = (TextView) view.findViewById(R.id.tvDaysOld);
        imageView = (ImageView) view.findViewById(R.id.ivImage);
    }

}
