package com.nathansass.nooze.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nathansass.nooze.R;
import com.nathansass.nooze.adapters.ArticleRecyclerAdapter;
import com.nathansass.nooze.models.Article;
import com.nathansass.nooze.models.Settings;
import com.nathansass.nooze.util.EndlessRecyclerViewScrollListener;
import com.nathansass.nooze.util.ItemClickSupport;
import com.nathansass.nooze.util.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    Context context;

    ArrayList<Article> articles;
    ArticleRecyclerAdapter adapter;

    Settings settings;

    String previousSearch;

    int page = 1;
    String query = "";


    Menu menu;
    SearchView searchView;
    TextView selectCategory; // this holds the previously clicked category

    @BindView(R.id.horizontalScrollView)
    HorizontalScrollView horizontalScrollView;
    @BindView(R.id.adventureSports)
    TextView adventureCategory;

    private final int REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        context = this;

        settings = new Settings();

        setUpViews();

        setFirstSearch();

    }

    public void setFirstSearch() {
        selectCategory = adventureCategory;
        settings.newsCategories.add((String) adventureCategory.getText());
        adventureCategory.setTextColor( ContextCompat.getColor(context, R.color.colorAccent) );
        onArticleSearch("");
    }

    public void setUpViews() {
        previousSearch = "";

        RecyclerView articleRecycler = (RecyclerView) findViewById(R.id.rvResults);

        articles = new ArrayList<>();

        adapter = new ArticleRecyclerAdapter(this, articles);
        articleRecycler.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        articleRecycler.setLayoutManager(gridLayoutManager);

        SpacesItemDecoration decoration = new SpacesItemDecoration(8);
        articleRecycler.addItemDecoration(decoration);

        articleRecycler.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                resetSearchArea();
                onArticleEndlessSearch();
            }
        });

        ItemClickSupport.addTo(articleRecycler).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        resetSearchArea();

                        Intent i = new Intent( getApplicationContext(), ArticleActivity.class );

                        Article article = articles.get(position);

                        i.putExtra("article", Parcels.wrap(article));

                        startActivity(i);
                    }
                }
        );
    }

    @OnClick({ R.id.adventureSports, R.id.arts, R.id.business, R.id.entrepreneurs, R.id.environment, R.id.fashion, R.id.financial, R.id.food, R.id.health, R.id.sports, R.id.style, R.id.technology, R.id.us, R.id.wealth, R.id.world })
    public void onCategoryClick(View view) {
        TextView tvClicked = (TextView) view;
        String category = (String) tvClicked.getText();

        int containerWidth = horizontalScrollView.getWidth() / 2;
        int tvWidth = tvClicked.getWidth() / 2;

        horizontalScrollView.smoothScrollTo(tvClicked.getLeft() - containerWidth + tvWidth , 0);

        if (settings.newsCategories.contains(category)) {
            // change color back to regular color and remove it from array
            settings.newsCategories.clear();
            tvClicked.setTextColor( ContextCompat.getColor(context, R.color.buttonBackground) );
        } else {
            // highlight the item selected, remove color from old item
            settings.newsCategories.clear();

            if (selectCategory != null) { // if there is a previously selected value
                selectCategory.setTextColor( ContextCompat.getColor(context, R.color.buttonBackground) );
            }

            tvClicked.setTextColor( ContextCompat.getColor(context, R.color.colorAccent) );
            settings.newsCategories.add(category);

            selectCategory = tvClicked;

        }

        // clear any existing queries
        onArticleSearch("");


//        Toast.makeText(getApplication(), "item clicked: " + category, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        this.menu = menu;

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.clearFocus();

                if (selectCategory != null) { // if there is a previously selected value
                    selectCategory.setTextColor( ContextCompat.getColor(context, R.color.buttonBackground) );
                    settings.newsCategories.remove( selectCategory.getText() ); // BUGBUG: will remove from settings if also chosen there, relying on the fact there are so many more options to choose from in the main search
                }

                onArticleSearch(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(SearchActivity.this, SettingsActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            // Extract name value from result extras
            settings = (Settings) data.getExtras().get("settings");
            int code = data.getExtras().getInt("code", 0);

            onArticleSearch();

        }
    }

    public void onArticleEndlessSearch() {

        if (previousSearch != query) {
            page = 1;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "1c1bd6892f7e49668dd62865cba5b5f8");
        params.put("page", page);

        page += 1;

        if (query.length() > 0) {
            params.put("q", query);
        }

        if (settings.getNewsCategories().length() > 0) {
            params.put("fq", settings.getNewsCategories());
        }

        if (settings.getBeginDate().length() > 0) {
            params.put("begin_date", settings.getBeginDate());
        }

        if (settings.sortBy != null) {
            params.put("sort", settings.sortBy);
        }


        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArticleResults = null;
                try {

                    int curSize = articles.size();

//                    articles.clear();
//                    adapter.notifyDataSetChanged();
                    jsonArticleResults = response.getJSONObject("response").getJSONArray("docs");

                    ArrayList<Article> newArticles = Article.fromJsonArray(jsonArticleResults);

                    articles.addAll(newArticles);

                    adapter.notifyItemRangeInserted(curSize, newArticles.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //TODO: handle failure
                Toast.makeText(getApplication(), "Failure connecting with NYTimes", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "Failure: " + errorResponse.toString());
            }
        });

    }

    private void resetSearchArea() {

        //Why didn't these work
//        searchView.clearFocus();
//        searchView.clear
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        searchItem.collapseActionView();

        searchView.setQuery("",false);
        searchView.setIconified(true);
    }

    public void onArticleSearch(String query) {
        this.query = query;
        onArticleSearch();
    }

    public void onArticleSearch() { // What if I just want to call this
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "1c1bd6892f7e49668dd62865cba5b5f8");
        params.put("page", 0);

        if (query.length() > 0) {
            params.put("q", query);
        }

        if (settings.getNewsCategories().length() > 0) {
            params.put("fq", settings.getNewsCategories());
        }

        if (settings.getBeginDate().length() > 0) {
            params.put("begin_date", settings.getBeginDate());
        }

        if (settings.sortBy != null) {
            params.put("sort", settings.sortBy);
        }

        articles.clear();
        adapter.notifyDataSetChanged();
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArticleResults = null;
                try {
//                    articles.clear();
                    jsonArticleResults = response.getJSONObject("response").getJSONArray("docs");

                    ArrayList<Article> newArticles = Article.fromJsonArray(jsonArticleResults);
                    articles.addAll(Article.fromJsonArray(jsonArticleResults));
                    adapter.notifyItemRangeInserted(0, newArticles.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //TODO: handle failure
                Toast.makeText(getApplication(), "Failure connecting with NYTimes", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "Failure: " + errorResponse.toString());
            }
        });
    }
}
