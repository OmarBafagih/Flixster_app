package com.example.flixster_app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster_app.R;
import com.example.flixster_app.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {


    Context context;

    // the movie to display
    Movie movie;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPosterOrBackdrop;

    String youtubeVidID;
    private List<Movie> movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        ivPosterOrBackdrop = (ImageView) findViewById(R.id.ivPosterOrBackdrop);
        movies = new ArrayList<>();


        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        //setting the image or backdrop
        String imageURL;

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            imageURL = movie.getBackdropPath();
        }
        else{
            imageURL = movie.getPosterPath();
        }
        Glide.with(this).load(imageURL)
                .placeholder(R.drawable.flicks_movie_placeholder)
                .into(ivPosterOrBackdrop);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(("https://api.themoviedb.org/3/movie/" + Integer.toString(movie.getId()) + "/videos?api_key=<<api_key>>&language=en-US"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    movies.addAll(Movie.fromJsonArray(results));
                    youtubeVidID = Integer.toString(movies.get(0).getId());


                }
                catch(JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                //Log.d(TAG, "onFailure ");
            }
        });

        ivPosterOrBackdrop.isClickable();
        ivPosterOrBackdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                myIntent.putExtra("key", youtubeVidID); //Optional parameters
                MovieDetailsActivity.this.startActivity(myIntent);
            }
        });

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);
    }
}