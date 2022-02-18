package com.example.codepath_flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.codepath_flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {
    // I have already limited what it can be used on in the google APIs console.
    public static final String YOUTUBE_API_KEY = "AIzaSyCa0HBJkmbIertIzE28JdnNT_Hf4xWF4r0";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    TextView tvDetailTitle;
    TextView tvDetailOverview;
    RatingBar rbDetailRating;
    YouTubePlayerView ytPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailOverview = findViewById(R.id.tvDetailOverview);
        rbDetailRating = findViewById(R.id.rbDetailRating);
        ytPlayerView = findViewById(R.id.ytPlayerView);

        Movie movieDetails = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvDetailTitle.setText(movieDetails.getTitle());
        tvDetailOverview.setText(movieDetails.getOverview());
        rbDetailRating.setRating((float) movieDetails.getRating());

        // Locale does not matter in this link.
        @SuppressLint("DefaultLocale")
        final String VIDEO_API_REQUEST = String.format(VIDEOS_URL, movieDetails.getId());

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(VIDEO_API_REQUEST, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray response = json.jsonObject.getJSONArray("results");
                    if(response.length() == 0) {
                        return;
                    }
                    String youtubeUrlKey = response.getJSONObject(0).getString("key");
                    Log.d("DetailActivity", youtubeUrlKey);
                    initializeYoutube(youtubeUrlKey);
                } catch (JSONException e) {
                    Log.e("DetailActivity", "error parsing response JSON");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    private void initializeYoutube(String youtubeUrlKey) {
        ytPlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cueVideo(youtubeUrlKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "onInitializationFailure");
            }
        });
    }
}