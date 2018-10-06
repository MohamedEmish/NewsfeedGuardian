package com.example.amosh.newsfeedguardian;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String SEARCH_RESULTS = "newFeedsResults";

    FeedAdapter adapter;
    ListView listView;
    TextView NoDataFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NoDataFound = (TextView) findViewById(R.id.empty_view);

        adapter = new FeedAdapter(this, -1);

        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Feed currentFeed = adapter.getItem(position);
                Uri feedUri = Uri.parse(currentFeed.getmUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, feedUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        if (savedInstanceState != null) {
            Feed[] feeds = (Feed[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            adapter.addAll(feeds);
        }
        if (isInternetConnectionAvailable()) {
            FeedAsyncTask task = new FeedAsyncTask();
            task.execute();
        } else {
            Toast.makeText(MainActivity.this, R.string.no_internet_connection,
                    Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isInternetConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork)
            return false;
        return activeNetwork.isConnectedOrConnecting();
    }
    private void updateUi(List<Feed> feeds) {
        if (feeds.isEmpty()) {
            NoDataFound.setVisibility(View.VISIBLE);
        } else {
            NoDataFound.setVisibility(View.GONE);
        }
        adapter.clear();
        adapter.addAll(feeds);
    }


    private String getUrlForHttpRequest() {
        final String baseUrl = "https://content.guardianapis.com/search?api-key=test";
        return baseUrl;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Feed[] feeds = new Feed[adapter.getCount()];
        for (int i = 0; i < feeds.length; i++) {
            feeds[i] = adapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) feeds);
    }

    private class FeedAsyncTask extends AsyncTask<URL, Void, List<Feed>> {

        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected List<Feed> doInBackground(URL... urls) {
            URL url = createURL(getUrlForHttpRequest());
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Feed> feeds = parseJson(jsonResponse);
            return feeds;
        }

        @Override
        protected void onPostExecute(List<Feed> feeds) {
            if (feeds == null) {
                return;
            }
            updateUi(feeds);
            linlaHeaderProgress.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        private URL createURL(String stringUrl) {
            try {
                return new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("MainActivity", "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Feed> parseJson(String json) {

            if (json == null) {
                return null;
            }

            List<Feed> feeds = QueryUtils.extractFeeds(json);
            return feeds;
        }
    }
}
