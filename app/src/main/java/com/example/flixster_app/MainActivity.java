package com.example.flixster_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.flixster_app.adapters.MovieAdapter;
import com.example.flixster_app.models.Movie;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    //private ActivitySimpleBinding binding;

    private static final String TAG = "Mainactivity";
    private List<Movie> movies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //System.out.println(R.string.movie_API_Key + " NICNEINCINECINECINECIN");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ctivitySimpleBinding.inflate()
        movies = new ArrayList<>();

        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        MovieAdapter movieAdapter = new MovieAdapter(this, movies);
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));


        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://api.themoviedb.org/3/movie/now_playing?api_key=6c7d2ca1a121bb949248f39b309ff1c2&", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + movies.size());
                }
                catch(JSONException e){
                    Log.e(TAG, "Hit Json exception", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure ");
            }
        });
    }
}