package app.moviemania.com.moviemania;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static app.moviemania.com.moviemania.Constants.ADULT;
import static app.moviemania.com.moviemania.Constants.LANGUAGE;
import static app.moviemania.com.moviemania.Constants.OVERVIEW;
import static app.moviemania.com.moviemania.Constants.RELEASE_DATE;
import static app.moviemania.com.moviemania.Constants.THUMBNAIL;
import static app.moviemania.com.moviemania.Constants.TITLE;
import static app.moviemania.com.moviemania.Constants.VOTE_AVG;
import static app.moviemania.com.moviemania.Constants.VOTINGS;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p";
    private static final String SIZE = "/w185";

    private Context context;
    private List<Movie> movieList;

    public RecyclerViewAdapter(Context context, List<Movie> movies) {
        this.context = context;
        movieList = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.title.setText(movieList.get(position).getTitle());

        String thumbail_url = BASE_URL_IMAGE + SIZE + movieList.get(position).getPoster_path();
        Picasso.with(context)
                .load(thumbail_url)
                .placeholder(R.drawable.movie_icon)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.poster);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = movieList.get(position);
                Intent intent = new Intent(v.getContext(), MovieDetailsActivity.class);
                intent.putExtra(TITLE, movie.getTitle());
                intent.putExtra(OVERVIEW, movie.getOverview());
                intent.putExtra(THUMBNAIL, BASE_URL_IMAGE + SIZE + movieList.get(position).getPoster_path());
                intent.putExtra(LANGUAGE, movie.getLanguage());
                intent.putExtra(RELEASE_DATE, movie.getRelease_date());
                intent.putExtra(VOTINGS, movie.getVotings());
                intent.putExtra(VOTE_AVG, movie.getVote_avg());
                intent.putExtra(ADULT, movie.isAdult());
                v.getContext().startActivity(intent);
                /*View view = LayoutInflater.from(context).inflate(R.layout.layout_movie_details, null);
                TextView title = view.findViewById(R.id.md_title_id);
                TextView overview = view.findViewById(R.id.md_overview_id);
                TextView votes = view.findViewById(R.id.md_votes);
                TextView votes_avg = view.findViewById(R.id.md_vote_avg_id);
                TextView language = view.findViewById(R.id.md_lang_id);
                TextView release_date = view.findViewById(R.id.md_release_date_id);
                TextView adult = view.findViewById(R.id.md_adult_id);
                ImageView poster = view.findViewById(R.id.md_poster_id);
                title.setText(movieList.get(position).getTitle());
                overview.setText(movieList.get(position).getOverview());
                votes.setText(movieList.get(position).getVotings() + " votes");
                votes_avg.setText(movieList.get(position).getVote_avg() + "/10");
                language.setText(movieList.get(position).getLanguage().toUpperCase());
                release_date.setText(movieList.get(position).getRelease_date());
                if (movieList.get(position).isAdult())
                    adult.setText("A");
                else
                    adult.setText("AU/U");

                Picasso.with(context).load(BASE_URL_IMAGE + SIZE + movieList.get(position).getPoster_path())
                        .placeholder(R.drawable.movie_icon).into(poster);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(view).setCancelable(true).create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();*/
            }
        });


    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView poster;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            title = itemView.findViewById(R.id.movieTitleID);
            poster = itemView.findViewById(R.id.moviePosterID);

        }
    }
}
