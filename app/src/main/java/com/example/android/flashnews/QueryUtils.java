/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.flashnews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link NewsObject} objects.
     */
    public static List<NewsObject> fetchNewsData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("MainActivity", "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsObject}s
        List<NewsObject> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("MainActivity", "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("MainActivity", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Problem retrieving the NewsObject JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
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

    /**
     * Return a list of {@link NewsObject} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<NewsObject> extractFeatureFromJson(String newsJSON) {

        // Create an empty ArrayList that we can start adding earthquakes to
        List<NewsObject> newsList = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            String status = responseObject.getString("status");

            if (status.equalsIgnoreCase("ok")) {

                NewsObject.totalFindings = responseObject.getInt("total");
                NewsObject.currentPage = responseObject.getInt("currentPage");
                NewsObject.totalNumberOfPages = responseObject.getInt("pages");
                JSONArray results = responseObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {

                    JSONObject currentNews = results.getJSONObject(i);
                    String sectionName = currentNews.getString("sectionName");
                    String webPublicationDate = currentNews.getString("webPublicationDate");
                    String webTitle = currentNews.getString("webTitle");
                    String webUrl = currentNews.getString("webUrl");
                    JSONObject fields = currentNews.optJSONObject("fields");
                    String headline;
                    String thumbnailUrl;
                    if(fields == null){
                        headline = webTitle;
                        thumbnailUrl = "";
                    }else {
                        headline = fields.optString("headline");
                        thumbnailUrl = fields.optString("thumbnail");
                    }
                    JSONArray tags = currentNews.optJSONArray("tags");
                    String contributor = "";
                    if(tags.length() < 1){
                        contributor = "";
                    }else {
                        contributor = tags.getJSONObject(0).getString("webTitle");
                    }

                    NewsObject news = new NewsObject(sectionName, webPublicationDate, webTitle, webUrl, headline, thumbnailUrl, contributor);
                    newsList.add(news);
                }
            }else Log.v("QueryUtils", "JSON status is NOT OK");

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of News
        Log.v("MainActivity", "NewsList is: " + newsList.toString());
        return newsList;
    }

}
