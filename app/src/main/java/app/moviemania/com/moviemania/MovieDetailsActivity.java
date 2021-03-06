package app.moviemania.com.moviemania;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.moviemania.com.moviemania.database.MoviesDatabase;

import static app.moviemania.com.moviemania.HomepageActivity.ACCEPT;
import static app.moviemania.com.moviemania.HomepageActivity.ACCEPT_JSON;
import static app.moviemania.com.moviemania.HomepageActivity.API_KEY;
import static app.moviemania.com.moviemania.HomepageActivity.BASE_URL;
import static app.moviemania.com.moviemania.HomepageActivity.REQUEST_GET;
import static app.moviemania.com.moviemania.HomepageActivity.RESULTS;
import static app.moviemania.com.moviemania.RecyclerViewAdapter.MOVIE_KEY;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetailsActivity";

    private static final String MOVIE = "/movie/";
    private static final String VIDEO = "/videos";
    private static final String REVIEWS = "/reviews";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String KEY = "key";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String REMOVE_FROM_FAVOURITES = "Remove from favourites";
    private static final String MARK_AS_FAVOURITE = "Mark as favourite";
    private static String THUMBNAIL_URL = "https://img.youtube.com/vi/";

    private TextView titleText, overviewText, release_dateText, votingsText, vote_avgText, adultText, reviews, shareTrailer;
    private ImageView movieThumbnail, playicon;
    private Button favouriteButton;
    private List<String> trailers;
    private List<Review> reviewList;
    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;
    private RecyclerView recyclerView;
    RecyclerView reviewsRecyclerView;

    private AppExecutors appExecutors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        titleText = findViewById(R.id.md_title_id);
        overviewText = findViewById(R.id.md_overview_id);
        release_dateText = findViewById(R.id.md_release_date_id);
        //langText = findViewById(R.id.md_lang_id);
        votingsText = findViewById(R.id.md_votes);
        vote_avgText = findViewById(R.id.md_vote_avg_id);
        adultText = findViewById(R.id.md_adult_id);
        //posterImage = findViewById(R.id.md_poster_id);
        recyclerView = findViewById(R.id.trailers_recyclerViewid);
        reviews = findViewById(R.id.md_reviews_id);
        shareTrailer = findViewById(R.id.shareTrailerID);
        reviewList = new ArrayList<>();
        movieThumbnail = findViewById(R.id.movieThumbnailID);
        playicon = findViewById(R.id.play_icon_id);
        favouriteButton = findViewById(R.id.favouriteID);
        appExecutors = AppExecutors.getInstance(getApplicationContext());

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reviewList.size() <= 0) {
                    Log.e(TAG, "No reviews found. returning...");
                    Toast.makeText(getApplicationContext(), "Bummer!\nnobody reviewed this yet", Toast.LENGTH_LONG).show();
                    return;
                }
                reviewsRecyclerView = new RecyclerView(MovieDetailsActivity.this);
                reviewsAdapter = new ReviewsAdapter(reviewList);
                reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));
                reviewsRecyclerView.setAdapter(reviewsAdapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this)
                        .setTitle("Reviews")
                        .setView(reviewsRecyclerView);
                builder.create().show();
            }
        });

        final Movie movie = getIntent().getParcelableExtra(MOVIE_KEY);
        if (movie != null) {
            titleText.setText(movie.getTitle());
            overviewText.setText(movie.getOverview());
            //langText.setText(movie.getLanguage().toUpperCase());
            release_dateText.setText(movie.getRelease_date());
            String votes_text = movie.getVotings() + " votes";
            votingsText.setText(votes_text);
            String likePercentage = String.valueOf(movie.getVote_avg() * 10) + "%";
            vote_avgText.setText(likePercentage);
            if (movie.isAdult())
                adultText.setText("(A)");
            else
                adultText.setText("(AU/U)");

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    final Cursor Itemid = MoviesDatabase.getInstance(getApplicationContext()).movieDao().getMovieID(movie.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Itemid.getCount() >= 1) {
                                //Toast.makeText(getApplicationContext(),"Favourite",Toast.LENGTH_LONG).show();
                                favouriteButton.setText(REMOVE_FROM_FAVOURITES);
                                favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmarked, 0, 0, 0);
                            } else {
                                //Toast.makeText(getApplicationContext(),"Not Favourite",Toast.LENGTH_LONG).show();
                                favouriteButton.setText(MARK_AS_FAVOURITE);
                                favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_removed, 0, 0, 0);
                            }
                        }
                    });
                }
            });

            long id = movie.getId();
            String framedUrl = BASE_URL + MOVIE + id + VIDEO + API_KEY;
            new LoadTrailerLinks(framedUrl).execute();
            String framedReviewsUrl = BASE_URL + MOVIE + id + REVIEWS + API_KEY;
            new LoadReviews(framedReviewsUrl).execute(REVIEWS);
            Log.e(TAG, "Creating connection to " + framedUrl + " in the background");

            /*Picasso.with(this)
                    .load(BASE_URL_IMAGE + SIZE + movie.getPoster_path())
                    .placeholder(android.R.drawable.stat_notify_error)
                    .error(R.drawable.movie_icon)
                    .into(posterImage);*/

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(movie.getTitle());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie != null) {
                    if (favouriteButton.getText().equals(REMOVE_FROM_FAVOURITES)) {
                        appExecutors.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                MoviesDatabase.getInstance(getApplicationContext()).movieDao().deleteMovie(movie);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Bookmark removed", Toast.LENGTH_LONG).show();
                                        favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_removed, 0, 0, 0);
                                        favouriteButton.setText(MARK_AS_FAVOURITE);
                                    }
                                });
                            }
                        });
                    } else {
                        appExecutors.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                MoviesDatabase.getInstance(getApplicationContext()).movieDao().insertMovie(movie);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Bookmarked movie", Toast.LENGTH_LONG).show();
                                        favouriteButton.setText(REMOVE_FROM_FAVOURITES);
                                        favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmarked, 0, 0, 0);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        shareTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trailers.get(0) == null)
                    Toast.makeText(getApplicationContext(), "No trailers found", Toast.LENGTH_LONG).show();
                else {
                    ShareCompat.IntentBuilder.from(MovieDetailsActivity.this)
                            .setChooserTitle("Share movie")
                            .setText("I found this movie intersting. why don't you check it out? \n\n" + trailers.get(0))
                            .setType("text/plain")
                            .startChooser();
                }
            }
        });

        playicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trailers != null && trailers.size() > 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailers.get(0)));
                    if (intent.resolveActivity(v.getContext().getPackageManager()) != null)
                        v.getContext().startActivity(intent);
                    else
                        Toast.makeText(v.getContext(), "No app available to perform this action", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "No trailer found", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private class LoadTrailerLinks extends AsyncTask<String, Void, String> {
        String url;

        public LoadTrailerLinks(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            try {
                Log.e(TAG, "Trying to connect to URL: " + url);
                URL apiUrl = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) apiUrl.openConnection();
                httpURLConnection.setRequestMethod(REQUEST_GET);
                httpURLConnection.setRequestProperty(ACCEPT, ACCEPT_JSON);
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String buffer;

                    while ((buffer = bufferedReader.readLine()) != null)
                        result.append(buffer);

                    bufferedReader.close();
                    Log.e(TAG, "Retrieved Json to load trailers: \n" + result.toString());
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Invalid URL Supplied: " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Failed to finish the operation: " + e.toString());
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e(TAG, "Received result: " + result);
            try {
                JSONObject mainObject = new JSONObject(result.toString());
                JSONArray movies = mainObject.getJSONArray(RESULTS);
                trailers = new ArrayList<>();
                List<String> thumbnails = new ArrayList<>();
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject jsonObject = movies.getJSONObject(i);
                    trailers.add(YOUTUBE_BASE_URL + jsonObject.getString(KEY));
                    Log.e(TAG, "Extracted youtube url for trailer " + i + ": " + trailers.get(i));
                    String required_thumbnail_url = THUMBNAIL_URL + movies.getJSONObject(i).getString(KEY) + "/0.jpg";
                    thumbnails.add(required_thumbnail_url);
                }
                trailersAdapter = new TrailersAdapter(MovieDetailsActivity.this, trailers, thumbnails);
                recyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(trailersAdapter);
                String required_thumbnail_url = THUMBNAIL_URL + movies.getJSONObject(0).getString(KEY) + "/0.jpg";
                Log.e(TAG, "Thumbnail URL extracted: " + required_thumbnail_url);
                Picasso.with(MovieDetailsActivity.this)
                        .load(required_thumbnail_url)
                        .error(android.R.drawable.stat_notify_error)
                        .placeholder(android.R.drawable.stat_notify_error)
                        .fit()
                        .into(movieThumbnail);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse json: " + e.toString());
            }
        }
    }

    private class LoadReviews extends AsyncTask<String, Void, String> {
        String url;
        StringBuilder result = new StringBuilder();

        public LoadReviews(String framedReviewsUrl) {
            this.url = framedReviewsUrl;
            Log.e(TAG, "Received reviews URL: " + url);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL reviewUrl = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) reviewUrl.openConnection();
                httpURLConnection.setRequestProperty(ACCEPT, ACCEPT_JSON);
                httpURLConnection.setRequestMethod(REQUEST_GET);
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String buffer;

                    while ((buffer = bufferedReader.readLine()) != null)
                        result.append(buffer);

                    bufferedReader.close();
                    Log.e(TAG, "Retrieved Json to load reviews: \n" + result.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "Received result on onPostExecute(): " + s);
            try {
                JSONObject mainJsonObject = new JSONObject(s);
                JSONArray reviewsArray = mainJsonObject.getJSONArray(RESULTS);
                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject jsonObject = reviewsArray.getJSONObject(i);
                    Review review = new Review();
                    review.setTitle(jsonObject.getString(AUTHOR));
                    Log.e(TAG, "Adding title: " + review.getTitle());
                    review.setDescription(jsonObject.getString(CONTENT));
                    //Log.e(TAG,"Adding Description: " + review.getDescription());
                    reviewList.add(review);
                }
                String reviewsText = reviewsArray.length() + " reviews>";
                reviews.setText(reviewsText);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse json: " + e.toString());
            }
        }
    }
}
