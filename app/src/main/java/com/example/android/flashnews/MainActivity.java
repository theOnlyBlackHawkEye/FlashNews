package com.example.android.flashnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsObject>>,
        SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    private NewsAdaptor mAdapter;
    private TextView mEmptyStateTextView;
    private TextView pageNumberTextView;

    static final int NEWS_LOADER_ID = 1;
    private SearchView searchContent;
    private int pageNumber = 1;
    private boolean isSameSearch = false;
    String orderBy;
    String pageSize;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        searchContent = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchContent.setQueryHint("Search Content");
        searchContent.setIconifiedByDefault(true);
        searchContent.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnected()) {
                    isSameSearch = false;
                    NewsObject.searchWord = query;
                    Log.v("MainActivity", NewsObject.searchWord);
                    mAdapter.clear();
                    mEmptyStateTextView.setVisibility(View.GONE);
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.VISIBLE);
                    getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                } else {
                    mAdapter.clear();
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsListView = (ListView) findViewById(R.id.list);

        mAdapter = new NewsAdaptor(this, new ArrayList<NewsObject>());

        newsListView.setAdapter(mAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = mAdapter.getItem(position).getWebUrl();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
            }
        });

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            orderBy = getString(R.string.settings_order_by_default);
            pageSize = getString(R.string.settings_page_size_default);
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        ImageView nextPageImage = (ImageView) findViewById(R.id.next_page);
        nextPageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnected()) {
                    if (pageNumber < NewsObject.totalNumberOfPages) {
                        isSameSearch = true;
                        pageNumber++;
                        mAdapter.clear();
                        mEmptyStateTextView.setVisibility(View.GONE);
                        View loadingIndicator = findViewById(R.id.loading_indicator);
                        loadingIndicator.setVisibility(View.VISIBLE);
                        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    } else {
                        Toast.makeText(MainActivity.this, "Already at Last Page", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mAdapter.clear();
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });

        ImageView previousPageImage = (ImageView) findViewById(R.id.previous_page);
        previousPageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnected()) {
                    if (pageNumber > 1) {
                        isSameSearch = true;
                        pageNumber--;
                        mAdapter.clear();
                        mEmptyStateTextView.setVisibility(View.GONE);
                        View loadingIndicator = findViewById(R.id.loading_indicator);
                        loadingIndicator.setVisibility(View.VISIBLE);
                        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    } else {
                        Toast.makeText(MainActivity.this, "Already at First Page", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mAdapter.clear();
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_page_size_key)) ||
                key.equals(getString(R.string.settings_order_by_key))){

            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<List<NewsObject>> onCreateLoader(int i, @Nullable Bundle bundle) {


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        pageSize = sharedPrefs.getString(getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));



        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("lang", "en");
        if(NewsObject.searchWord != null) {
            uriBuilder.appendQueryParameter("q", NewsObject.searchWord);
        }
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page", String.valueOf(pageNumber));
        uriBuilder.appendQueryParameter("page-size", pageSize);
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,headline");
        uriBuilder.appendQueryParameter("api-key", "63032432-d0d7-4208-9988-6ca63df962cd");

        Log.v(LOG_TAG, "URL is: " + uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsObject>> loader, List<NewsObject> newsList) {

        searchContent.setIconified(true);
        searchContent.setIconifiedByDefault(true);
        searchContent.setFocusable(false);
        searchContent.clearFocus();

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        if (!isSameSearch) {
            Toast.makeText(this, NewsObject.totalFindings + " Result(s) been Found", Toast.LENGTH_LONG).show();
        }

        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);
            pageNumberTextView = (TextView) findViewById(R.id.page_number);
            pageNumberTextView.setText(" " + NewsObject.currentPage + " / " + NewsObject.totalNumberOfPages);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsObject>> loader) {
        mAdapter.clear();
    }


}
