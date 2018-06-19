package app.moviemania.com.moviemania;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static int LOADER_ID = 101;
    private static String URL_KEY = "url";
    private static final String TAG = "HomePageActivity";
    private static final String MOST_POPULAR_STRING = "Most Popular";
    private static final String TOP_RATED_STRING = "Most Rated";
    private static final String FAVOURITES_STRING = "Favourites";
    private static final String CHOSEN_OPTION_KEY = "chosen";
    private static final String SCROLL_POSITION = "scroll position";
    private static final String SELECT_FILTER = "Select Filter";

    public static final String REQUEST_GET = "GET";
    public static final String ACCEPT = "accept";
    public static final String ACCEPT_JSON = "application/json";

    //API URLs
    public static final String BASE_URL = "http://api.themoviedb.org/3";
    public static final String API_KEY = "?api_key=" + BuildConfig.THE_GUARDIAN_API_KEY;
    private static final String MODE_MOST_POPULAR = "/movie/popular";
    private static final String MODE_TOP_RATED = "/movie/top_rated";
    private static final String MOST_POPULAR = BASE_URL + MODE_MOST_POPULAR + API_KEY;
    private static final String TOP_RATED = BASE_URL + MODE_TOP_RATED + API_KEY;

    //related to json
    public static final String RESULTS = "results";
    private static final String VOTE_COUNT = "vote_count";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String LANGUAGE = "original_language";
    private static final String ADULT = "adult";
    private static final String RELEASE_DATE = "release_date";
    private static final String OVERVIEW = "overview";
    private static final String ID = "id";

    private RecyclerViewAdapter recyclerViewAdapter;
    private ProgressDialog progressBar;
    private RecyclerView recyclerView;
    private List<Movie> movieList;
    private android.support.v4.app.LoaderManager loaderManager;
    private Loader<String> loader;
    private GridLayoutManager gridLayoutManager;
    private int position = 0;

    private boolean option_popular = false, option_rated = false, option_favourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        loaderManager = getSupportLoaderManager();
        loader = loaderManager.getLoader(LOADER_ID);
        progressBar = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerViewID);
        movieList = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(this, movieList);
        int posterWidth = 350;
        gridLayoutManager = new GridLayoutManager(this, calculateBestSpanCount(posterWidth));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (savedInstanceState != null) {
            String chosen_option = savedInstanceState.getString(CHOSEN_OPTION_KEY);
            position = savedInstanceState.getInt(SCROLL_POSITION);
            if (chosen_option != null)
                switch (chosen_option) {
                    case MOST_POPULAR_STRING:
                        loadData(MOST_POPULAR);
                        break;
                    case TOP_RATED_STRING:
                        loadData(TOP_RATED);
                        break;
                    case FAVOURITES_STRING:
                        loadFavouriteMovies();
                        break;
                    default:
                        break;
                }
        } else {
            loadData(MOST_POPULAR);
        }
    }

    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }

    private void loadData(String category) {
        if (!networkUnavailable()) {
            showNoNetworkDialog(category);
            return;
        }
        if (movieList != null)
            movieList.clear();

        recyclerView.smoothScrollToPosition(position);
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(URL_KEY, category);

        if (category.equals(MOST_POPULAR))
            setChosenOption(MOST_POPULAR_STRING);
        else if (category.equals(TOP_RATED))
            setChosenOption(TOP_RATED_STRING);

        if (loader == null) {
            Log.e(TAG, "initiating loader to load " + getChosenOption() + " movies...");
            //If I don't destroy loader, details are not loaded when option is chosen from filter. Need help here
            //use case below
            //1. comment below line and run the app
            //2. MOST POPULAR movies will be loaded. now use filter to change it to top rated
            //3. It won't load. Try same uncommenting below line, it works. Please explain
            loaderManager.destroyLoader(LOADER_ID);
            loaderManager.initLoader(LOADER_ID, loaderBundle, this);
        } else {
            Log.e(TAG, "restarting loader to load " + getChosenOption() + " movies...");
            loaderManager.restartLoader(LOADER_ID, loaderBundle, this);
        }
    }

    private void loadFavouriteMovies() {
        MainViewModel mainViewModel = ViewModelProviders.of(HomepageActivity.this).get(MainViewModel.class);
        Log.e(TAG, "Extracted mainViewModel...");
        mainViewModel.getMovies().observe(HomepageActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.e(TAG, "Receiving database update from LiveData in ViewModel...");
                recyclerViewAdapter.setMovieList(movies);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
        setChosenOption(FAVOURITES_STRING);
    }

    private void showNoNetworkDialog(final String category) {
        new AlertDialog.Builder(this)
                .setTitle("Are you offline?")
                .setMessage("Seems like you don't have an active network connection.")
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadData(category);
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    @Override
    protected void onStop() {
        if (getChosenOption().equals(FAVOURITES_STRING))
            getSupportLoaderManager().destroyLoader(LOADER_ID);
        super.onStop();
    }

    private boolean networkUnavailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filterID) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(SELECT_FILTER)
                    .setPositiveButton(MOST_POPULAR_STRING, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadData(MOST_POPULAR);
                        }
                    })
                    .setNegativeButton(TOP_RATED_STRING, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadData(TOP_RATED);
                        }
                    })
                    .setCancelable(false)
                    .create();
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Favourites", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    loadFavouriteMovies();
                }
            });
            dialog.show();
        }
        return true;
    }

    private String getChosenOption() {
        String result;
        if (option_rated)
            result = TOP_RATED_STRING;
        else if (option_favourite)
            result = FAVOURITES_STRING;
        else
            result = MOST_POPULAR_STRING;

        Log.e(TAG, "Returning chosen option: " + result);
        return result;
    }

    private void setChosenOption(String OPTION) {
        switch (OPTION) {
            case MOST_POPULAR_STRING:
                option_popular = true;
                option_favourite = false;
                option_rated = false;
                break;
            case TOP_RATED_STRING:
                option_popular = false;
                option_favourite = false;
                option_rated = true;
                break;
            case FAVOURITES_STRING:
                option_popular = false;
                option_favourite = true;
                option_rated = false;
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CHOSEN_OPTION_KEY, getChosenOption());
        outState.putInt(SCROLL_POSITION, gridLayoutManager.findFirstCompletelyVisibleItemPosition());
        Log.e(TAG, "Saving instance with option " + getChosenOption());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            public String loadInBackground() {
                String url = bundle.getString(URL_KEY);
                Log.e(TAG, "Received Url in loader: " + url);
                try {
                    URL receivedUrl = new URL(url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) receivedUrl.openConnection();
                    httpURLConnection.setRequestMethod(REQUEST_GET);
                    httpURLConnection.setRequestProperty(ACCEPT, ACCEPT_JSON);

                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.e(TAG, "Connection Successful... to " + url);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String buffer;
                        StringBuilder result = new StringBuilder();

                        while ((buffer = bufferedReader.readLine()) != null)
                            result.append(buffer);

                        bufferedReader.close();
                        //Log.e(TAG, "Retrieved Json: \n" + result.toString());

                        return result.toString();
                    } else
                        Log.e(TAG, "Failed to connect... to " + url);

                } catch (IOException e) {
                    Log.e(TAG, "Failed to parse URL" + e.toString());
                }
                return null;
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null)
                    return;

                progressBar.setTitle("Loading...");
                progressBar.show();

                Log.e(TAG, "ForceLoad started...");
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String resultJson) {
        if (movieList != null)
            movieList.clear();
        if (progressBar.isShowing())
            progressBar.cancel();

        try {
            JSONObject mainObject = new JSONObject(resultJson);
            JSONArray movies = mainObject.getJSONArray(RESULTS);
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movieObject = movies.getJSONObject(i);
                String title = movieObject.getString(TITLE);
                String overview = movieObject.getString(OVERVIEW);
                int votes = Integer.parseInt(movieObject.getString(VOTE_COUNT));
                double vote_avg = Double.parseDouble(movieObject.getString(VOTE_AVERAGE));
                String poster_path = movieObject.getString(POSTER_PATH);
                boolean adult = Boolean.parseBoolean(movieObject.getString(ADULT));
                String released_date = movieObject.getString(RELEASE_DATE);
                String language = movieObject.getString(LANGUAGE);
                int id = movieObject.getInt(ID);

                /*Log.e(TAG, "Retrieved Details:\n" +
                        TITLE + " " + title + "\n" +
                        OVERVIEW + " " + overview + "\n" +
                        VOTE_COUNT + " " + votes + "\n" +
                        VOTE_AVERAGE + " " + vote_avg + "\n" +
                        POSTER_PATH + " " + poster_path + "\n" +
                        ADULT + " " + adult + "\n" +
                        RELEASE_DATE + " " + released_date + "\n" +
                        ID + " " + id + "\n");*/

                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setLanguage(language);
                movie.setOverview(overview);
                movie.setVotings(votes);
                movie.setVote_avg(vote_avg);
                movie.setPoster_path(poster_path);
                movie.setAdult(adult);
                movie.setRelease_date(released_date);
                movie.setId(id);

                movieList.add(movie);
            }
            recyclerViewAdapter.setMovieList(movieList);
            recyclerViewAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse result to JSON Object " + e.toString());
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
