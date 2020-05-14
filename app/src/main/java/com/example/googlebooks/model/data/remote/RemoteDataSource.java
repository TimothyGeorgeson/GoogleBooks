package com.example.googlebooks.model.data.remote;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.googlebooks.model.Book;
import com.example.googlebooks.utils.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RemoteDataSource {
    private VolleyManager volleyManager;

    @Inject
    public RemoteDataSource(Context context) {
        volleyManager = VolleyManager.getInstance(context);
    }

    public LiveData<List<Book>> getBooks(String query) {
        String apiKey = "AIzaSyAFKNB2tPzOUemq1T9YEX-R1yyds6_tpIY";
        String baseURL = "https://www.googleapis.com/books/v1/volumes?q=intitle:";
        String additionalParams = "&orderBy=newest&maxResults=10&key=";
        String url = baseURL + query + additionalParams + apiKey;

        List<Book> books = new ArrayList<>();
        MutableLiveData<List<Book>> result = new MutableLiveData<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                            String imgURL;
                            String title;
                            String author = "";

                            try {
                                JSONObject imgObject = volumeInfo.getJSONObject("imageLinks");
                                imgURL = imgObject.getString("thumbnail");
                            } catch (Exception e) {
                                continue;
                            }

                            try {
                                title = volumeInfo.getString("title");
                            } catch (Exception e) {
                                continue;
                            }

                            try { //just take the first author
                                JSONArray authorArray = volumeInfo.getJSONArray("authors");
                                if (authorArray.length() > 0) {
                                    author = authorArray.getString(0);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            books.add(new Book(imgURL, title, author, query));
                        }

                        result.postValue(books);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(books);
                    }
                }, Throwable::printStackTrace);

        volleyManager.getRequestQueue().add(request);

        return result;
    }
}
