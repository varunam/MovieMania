package app.moviemania.com.moviemania;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static app.moviemania.com.moviemania.Constants.ADULT;
import static app.moviemania.com.moviemania.Constants.LANGUAGE;
import static app.moviemania.com.moviemania.Constants.OVERVIEW;
import static app.moviemania.com.moviemania.Constants.RELEASE_DATE;
import static app.moviemania.com.moviemania.Constants.THUMBNAIL;
import static app.moviemania.com.moviemania.Constants.TITLE;
import static app.moviemania.com.moviemania.Constants.VOTE_AVG;
import static app.moviemania.com.moviemania.Constants.VOTINGS;

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

        Bundle bundle = getIntent().getExtras();
        String title = "Error loading!", overview = "Error loading!", releaseDate = "Error loading!", lang = "Error loading!", poster = "Error loading!";
        int votings = 0;
        double vote_avg = 0;
        boolean adult = false;
        if (bundle != null) {
            title = bundle.getString(TITLE);
            overview = bundle.getString(OVERVIEW);
            releaseDate = bundle.getString(RELEASE_DATE);
            lang = bundle.getString(LANGUAGE);
            votings = bundle.getInt(VOTINGS);
            vote_avg = bundle.getDouble(VOTE_AVG);
            adult = bundle.getBoolean(ADULT);
            poster = bundle.getString(THUMBNAIL);
        }

        titleText.setText(title);
        overviewText.setText(overview);
        langText.setText(lang.toUpperCase());
        release_dateText.setText(releaseDate);
        votingsText.setText(votings + " votes");
        vote_avgText.setText(vote_avg + "");
        if (adult)
            adultText.setText("A");
        else
            adultText.setText("AU/U");

        Picasso.with(this)
                .load(poster)
                .placeholder(android.R.drawable.stat_notify_error)
                .error(R.drawable.movie_icon)
                .into(posterImage);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
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
