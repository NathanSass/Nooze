package com.nathansass.nooze.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nathansass.nooze.R;
import com.nathansass.nooze.adapters.ArticleArrayAdapter;
import com.nathansass.nooze.models.Article;
import com.nathansass.nooze.models.Settings;
import com.nathansass.nooze.util.EndlessRecyclerViewScrollListener;
import com.nathansass.nooze.util.ItemClickSupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
//    GridView gvResults;

    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    Settings settings;

    String previousSearch;

    int page = 1;

    private final int REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings = new Settings();

        setUpViews();

    }

    public void setUpViews() {
        previousSearch = "";
        etQuery = (EditText) findViewById(R.id.etQuery);
        RecyclerView articleRecycler = (RecyclerView) findViewById(R.id.rvResults);

        btnSearch = (Button) findViewById(R.id.btnSearch);

        articles = new ArrayList<>();

        adapter = new ArticleArrayAdapter(this, articles);
        articleRecycler.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        articleRecycler.setLayoutManager(gridLayoutManager);

        articleRecycler.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                onArticleEndlessSearch();
                Toast.makeText(getApplication(), "load page: " + page, Toast.LENGTH_SHORT).show();
            }
        });

        ItemClickSupport.addTo(articleRecycler).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent i = new Intent( getApplicationContext(), ArticleActivity.class );

                        Article article = articles.get(position);

                        i.putExtra("article", article);

                        startActivity(i);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

            onArticleSearch(getWindow().getDecorView().getRootView());

        }
    }

    public void onArticleEndlessSearch() {
        String query = etQuery.getText().toString();

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


    public void onArticleSearch(View view) { // What if I just want to call this
        String query = etQuery.getText().toString();

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
