package com.nathansass.nooze.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nathansass.nooze.R;
import com.nathansass.nooze.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nathansass on 7/25/16.
 */
public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView;
        public TextView tvDaysOld;
        public TextView tvTitle;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.ivImage);
            tvDaysOld = (TextView) itemView.findViewById(R.id.tvDaysOld);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }

    }

    /* View holder defined above */

    private List<Article> articles;

    private Context context;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder viewHolder, int position) {
        Article article = articles.get(position);

        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getHeadline());

        TextView tvDaysOld = viewHolder.tvDaysOld;
        tvDaysOld.setText( article.getAgeOfArticleInDays());


        ImageView imageView = viewHolder.imageView;
        String thumbnail = article.getThumbNail();

        if ( !TextUtils.isEmpty(thumbnail) ) {
            Picasso.with(getContext()).load(thumbnail).into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

}
