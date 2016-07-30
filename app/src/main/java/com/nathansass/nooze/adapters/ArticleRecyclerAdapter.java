package com.nathansass.nooze.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nathansass.nooze.R;
import com.nathansass.nooze.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nathansass on 7/25/16.
 */
public class ArticleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


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

    private final int HASIMAGE = 0, NOIMAGE =1;

    public ArticleRecyclerAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public int getItemViewType(int position) {
        Article article = articles.get(position);
        String thumbnail = article.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)) { //has image
            return HASIMAGE;
        } else { // no image
            return NOIMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        View noImage;

        switch (viewType) {
            case HASIMAGE:
                View hasImage = inflater.inflate(R.layout.item_article_result, parent, false);
                viewHolder = new ItemArticleResult(hasImage);
                break;
            case NOIMAGE:
                noImage = inflater.inflate(R.layout.item_article_no_image_result, parent, false);
                viewHolder = new ItemArticleNoImageResult(noImage);
                break;
            default: // BUGBUG: error state
                Toast.makeText(context,"Error in choosing proper layout for article result", Toast.LENGTH_SHORT).show();
                noImage = inflater.inflate(R.layout.item_article_no_image_result, parent, false);
                viewHolder = new ItemArticleNoImageResult(noImage);
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Article article = articles.get(position);

        switch (viewHolder.getItemViewType()) {
            case HASIMAGE:
                ItemArticleResult hasImageViewHolder = (ItemArticleResult) viewHolder;
                configureHasImageViewHolder(hasImageViewHolder, position);
                break;
            case NOIMAGE:
                ItemArticleNoImageResult noImageViewHolder = (ItemArticleNoImageResult) viewHolder;
                configureNoImageViewHolder(noImageViewHolder, position);
                break;
            default:
                Toast.makeText(context,"Error in choosing proper layout for article result, in onBindViewHolder", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void configureHasImageViewHolder(ItemArticleResult vhHasImage, int position) {
        Article article = articles.get(position);
        if (article != null) {
            vhHasImage.tvTitle.setText(article.getHeadline());
            vhHasImage.tvDaysOld.setText(article.getAgeOfArticleInDays());

            String thumbnail = article.getThumbNail();
            Picasso.with(getContext()).load(thumbnail).into(vhHasImage.imageView);
        }
    }

    private void configureNoImageViewHolder(ItemArticleNoImageResult vhNoImage, int position) {
        Article article = articles.get(position);
        if (article != null) {
            vhNoImage.tvTitle.setText(article.getHeadline());
            vhNoImage.tvDaysOld.setText(article.getAgeOfArticleInDays());
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

}
