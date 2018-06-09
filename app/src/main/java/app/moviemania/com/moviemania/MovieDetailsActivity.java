package app.moviemania.com.moviemania;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static app.moviemania.com.moviemania.RecyclerViewAdapter.BASE_URL_IMAGE;
import static app.moviemania.com.moviemania.RecyclerViewAdapter.MOVIE_KEY;
import static app.moviemania.com.moviemania.RecyclerViewAdapter.SIZE;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView titleText, overviewText, release_dateText, langText, votingsText, vote_avgText, adultText;
    private ImageView posterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movide_details);

        titleText = findViewById(R.id.md_title_id);
        overviewText = findViewById(R.id.md_overview_id);
        release_dateText = findViewById(R.id.md_release_date_id);
        langText = findViewById(R.id.md_lang_id);
        votingsText = findViewById(R.id.md_votes);
        vote_avgText = findViewById(R.id.md_vote_avg_id);
        adultText = findViewById(R.id.md_adult_id);
        posterImage = findViewById(R.id.md_poster_id);

        Movie movie = getIntent().getParcelableExtra(MOVIE_KEY);
        if (movie != null) {
            titleText.setText(movie.getTitle());
            overviewText.setText(movie.getOverview());
            langText.setText(movie.getLanguage().toUpperCase());
            release_dateText.setText(movie.getRelease_date());
            votingsText.setText(movie.getVotings() + " votes");
            vote_avgText.setText(movie.getVote_avg() + "");
            if (movie.isAdult())
                adultText.setText("A");
            else
                adultText.setText("AU/U");

            Picasso.with(this)
                    .load(BASE_URL_IMAGE + SIZE + movie.getPoster_path())
                    .placeholder(android.R.drawable.stat_notify_error)
                    .error(R.drawable.movie_icon)
                    .into(posterImage);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(movie.getTitle());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}
